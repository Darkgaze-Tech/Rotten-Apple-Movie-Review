package controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Movie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

@WebServlet("/search")
public class SearchResultServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String query = request.getParameter("query");
        
        // Handle empty or null query
        if (query == null || query.trim().isEmpty()) {
            request.setAttribute("error", "Please enter a search term");
            request.getRequestDispatcher("/search-results.jsp").forward(request, response);
            return;
        }
        
        query = query.trim(); // Clean up any leading/trailing whitespace
        List<Movie> results = performSearch(query);
        
        request.setAttribute("query", query); // Store cleaned query
        request.setAttribute("results", results);
        request.getRequestDispatcher("/search-results.jsp").forward(request, response);
    }

    private List<Movie> performSearch(String query) {
        List<Movie> results = new ArrayList<>();

        // Check if the query is just a year
        boolean isYearOnly = query.matches("\\d{4}");
        int year = -1;

        if (!isYearOnly) {
            // Extract year if present in the query (e.g., "Inception (2010)")
            Pattern pattern = Pattern.compile("\\((\\d{4})\\)");
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                year = Integer.parseInt(matcher.group(1));
                query = query.replaceAll("\\(\\d{4}\\)", "").trim(); // Remove year from query
            }
        } else {
            year = Integer.parseInt(query);
            query = ""; // Clear the query as we're searching by year only
        }

        String sql = "SELECT " +
                "    m.MovieID, " +
                "    m.Title, " +
                "    m.ReleaseYear, " +
                "    m.Description, " +
                "    m.PosterURL, " +
                "    GROUP_CONCAT(DISTINCT d.DirectorName SEPARATOR ', ') AS Directors, " +
                "    GROUP_CONCAT(DISTINCT g.GenreName SEPARATOR ', ') AS Genres, " +
                "    AVG(r.Rating) AS AverageRating " +
                "FROM " +
                "    movies m " +
                "LEFT JOIN " +
                "    moviedirectors md ON m.MovieID = md.MovieID " +
                "LEFT JOIN " +
                "    directors d ON md.DirectorID = d.DirectorID " +
                "LEFT JOIN " +
                "    moviegenres mg ON m.MovieID = mg.MovieID " +
                "LEFT JOIN " +
                "    genres g ON mg.GenreID = g.GenreID " +
                "LEFT JOIN " +
                "    reviews r ON m.MovieID = r.MovieID " +
                "WHERE " + (isYearOnly 
                ? "m.ReleaseYear = ? " 
                : (year != -1 
                    ? "CONCAT(m.Title, ' ', m.ReleaseYear) LIKE ? " 
                    : "CONCAT(m.Title, ' ', m.ReleaseYear) LIKE ? ")) +

                "GROUP BY " +
                "    m.MovieID " +
                "ORDER BY AverageRating DESC " +
                "LIMIT 20";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (isYearOnly) {
                statement.setInt(1, year);
            } else if (year != -1) {
                statement.setString(1, "%" + query + "%");
                statement.setInt(2, year);
            } else {
                statement.setString(1, "%" + query + "%");
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int movieId = resultSet.getInt("MovieID");
                    String title = resultSet.getString("Title");
                    int releaseYear = resultSet.getInt("ReleaseYear");
                    String description = resultSet.getString("Description");
                    String posterUrl = resultSet.getString("PosterURL");
                    String directors = resultSet.getString("Directors");
                    String genres = resultSet.getString("Genres");
                    double averageRating = resultSet.getDouble("AverageRating");

                    Movie movie = new Movie(movieId, title, releaseYear, genres, directors, description, posterUrl, averageRating);
                    results.add(movie);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        return results;
    }
}