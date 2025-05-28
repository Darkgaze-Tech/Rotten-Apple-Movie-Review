package controller;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;

@WebServlet("/MovieListServlet")
public class MovieListServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private List<Movie> movieList = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        try (Connection conn = dataSource.getConnection()) {
            loadMovies(conn);
            loadGenres(conn);
        } catch (SQLException e) {
            throw new ServletException("Database connection problem", e);
        }
    }

    private void loadMovies(Connection conn) throws SQLException {
        String sql = "SELECT m.MovieID, m.Title, m.ReleaseYear, m.PosterURL, " +
                     "IFNULL(AVG(r.Rating), 0) AS AvgRating " +
                     "FROM movies m LEFT JOIN reviews r ON m.MovieID = r.MovieID " +
                     "GROUP BY m.MovieID";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int movieId = rs.getInt("MovieID");
                String title = rs.getString("Title");
                int releaseYear = rs.getInt("ReleaseYear");
                String posterUrl = rs.getString("PosterURL");
                double avgRating = rs.getDouble("AvgRating"); // Get the average rating
                List<String> genres = loadGenresForMovie(conn, movieId);
                movieList.add(new Movie(movieId, title, releaseYear, genres.toArray(new String[0]), avgRating, posterUrl));
            }
        }
    }

    private List<String> loadGenresForMovie(Connection conn, int movieId) throws SQLException {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT g.GenreName FROM moviegenres mg JOIN genres g ON mg.GenreID = g.GenreID WHERE mg.MovieID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(rs.getString("GenreName"));
                }
            }
        }
        return genres;
    }

    private void loadGenres(Connection conn) throws SQLException {
        HashSet<String> genreSet = new HashSet<>();
        String sql = "SELECT GenreName FROM genres";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                genreSet.add(rs.getString("GenreName"));
            }
        }
        getServletContext().setAttribute("genreList", genreSet);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String genre = request.getParameter("genre");
        String sort = request.getParameter("sort");

        HashSet<String> genreSet = (HashSet<String>) getServletContext().getAttribute("genreList");
        List<Movie> filteredMovies = new ArrayList<>(movieList);

        if (genre != null && !genre.isEmpty()) {
            filteredMovies = filteredMovies.stream()
                .filter(movie -> Arrays.asList(movie.getGenres()).contains(genre))
                .collect(Collectors.toList());
        }

        if ("title".equals(sort)) {
            filteredMovies.sort(Comparator.comparing(Movie::getTitle));
        } else if ("year".equals(sort)) {
            filteredMovies.sort(Comparator.comparing(Movie::getReleaseYear).reversed());
        } else if ("rating".equals(sort)) {
            filteredMovies.sort(Comparator.comparing(Movie::getRating).reversed());
        }

        request.setAttribute("movieList", filteredMovies);
        request.setAttribute("genreList", genreSet);
        request.setAttribute("selectedGenre", genre);

        if ("true".equals(request.getParameter("ajax"))) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(filteredMovies);
            out.print(jsonResponse);
            out.flush();
        } else {
            request.getRequestDispatcher("Home.jsp").forward(request, response);
        }
    }

    public static class Movie {
    	private int movieID;
        private String title;
        private int releaseYear;
        private String[] genres;
        private double rating; 
        private String posterUrl;

        public Movie(int movieID, String title, int releaseYear, String[] genres, double rating, String posterUrl) {
            this.movieID = movieID;
        	this.title = title;
            this.releaseYear = releaseYear;
            this.genres = genres;
            this.rating = rating; 
            this.posterUrl = posterUrl;
        }

        public int getMovieID() {
            return movieID;
        }
        
        public String getTitle() {
            return title;
        }

        public int getReleaseYear() {
            return releaseYear;
        }

        public String[] getGenres() {
            return genres;
        }

        public double getRating() {
            return rating; 
        }

        public String getPosterUrl() {
            return posterUrl;
        }
    }
}
