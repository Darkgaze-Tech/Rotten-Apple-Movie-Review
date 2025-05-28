package dao;

import model.Movie;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieDAO {
    private static final Logger logger = Logger.getLogger(MovieDAO.class.getName());
    private DataSource dataSource;

    public MovieDAO(DataSource dataSource) {
        this.dataSource = dataSource; // Use DataSource for connection management
    }

    /**
     * Retrieves all movies from the database.
     *
     * @return a list of Movie objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";

        // Using try-with-resources for automatic resource management
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
             
            while (resultSet.next()) {
            	int movieID = resultSet.getInt("MovieID");
                String title = resultSet.getString("Title");
                int releaseYear = resultSet.getInt("ReleaseYear");
                String description = resultSet.getString("Description");
                String posterUrl = resultSet.getString("PosterUrl");

                Movie movie = new Movie(movieID, title, releaseYear, description, posterUrl);
                movies.add(movie);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving movies from the database", e);
            throw e; // Rethrow the exception for higher-level handling
        }

        return movies; // Return the list of movies
    }
    
    public Movie getCurrentMovie(int movieID) throws SQLException {
        // First, check if the movie exists in the movies table
        String checkSql = "SELECT COUNT(*) FROM movies WHERE movieID = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, movieID);
            try (ResultSet checkResult = checkStmt.executeQuery()) {
                if (checkResult.next() && checkResult.getInt(1) == 0) {
                    logger.info("No movie found with ID: " + movieID);
                    return null;
                }
            }
        }

        // If movie exists, get all its details
        String sql = """
            SELECT 
                m.movieID, 
                m.Title, 
                m.ReleaseYear, 
                m.Description, 
                m.PosterURL,
                GROUP_CONCAT(DISTINCT d.DirectorName) as Directors,
                GROUP_CONCAT(DISTINCT g.GenreName) as Genres
            FROM movies m
            LEFT JOIN moviedirectors md ON md.MovieID = m.MovieID
            LEFT JOIN directors d ON d.DirectorID = md.DirectorID
            LEFT JOIN moviegenres mg ON mg.MovieID = m.MovieID
            LEFT JOIN genres g ON g.GenreID = mg.GenreID
            WHERE m.MovieID = ?
            GROUP BY m.movieID, m.Title, m.ReleaseYear, m.Description, m.PosterURL
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, movieID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Movie movie = new Movie();
                    
                    // Get values with null checks
                    movie.setMovieID(movieID);
                    movie.setTitle(resultSet.getString("Title"));
                    movie.setReleaseYear(resultSet.getInt("ReleaseYear"));
                    movie.setDescription(resultSet.getString("Description"));
                    movie.setPosterUrl(resultSet.getString("PosterURL"));
                    
                    // Handle potentially null concatenated fields
                    String directors = resultSet.getString("Directors");
                    String genres = resultSet.getString("Genres");
                    movie.setDirectors(directors != null ? directors : "");
                    movie.setGenres(genres != null ? genres : "");

                    return movie;
                }
                return null;
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving movie with ID " + movieID + ": " + e.getMessage());
            throw new SQLException("Failed to retrieve movie details", e);
        }
    }

    

    // Additional methods for specific queries can be added here
}
