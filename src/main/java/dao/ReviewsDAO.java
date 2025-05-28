package dao;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import model.Reviews;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReviewsDAO {
	private static final Logger logger = Logger.getLogger(ReviewsDAO.class.getName());
	private DataSource datasource;

	public ReviewsDAO(DataSource datasource) {
		this.datasource = datasource;
	}

	public ReviewsDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Retrieves all current movie reviews for a given movie ID.
	 *
	 * @param movieID the ID of the movie
	 * @return a list of Reviews objects
	 * @throws NoSuchAlgorithmException if a hashing algorithm is not found
	 */
	public List<Reviews> getAllCurrentMovieReviews(int movieID) throws NoSuchAlgorithmException {
		List<Reviews> allReviews = new ArrayList<>();

		String selectStatement = "SELECT r.ReviewID, r.Rating, r.ReviewText, r.ReviewDate, " + "r.UserID, "
				+ "m.movieID, m.Title, m.ReleaseYear, m.Description, m.PosterURL, "
				+ "u.username, GROUP_CONCAT(DISTINCT(d.DirectorName)) AS Directors, "
				+ "GROUP_CONCAT(DISTINCT(g.GenreName)) AS Genres, "
				+ "(SELECT AVG(r2.Rating) FROM reviews r2 WHERE r2.MovieID = m.MovieID) AS AverageRating "
				+ "FROM reviews r " + "JOIN movies m ON r.MovieID = m.MovieID " + "JOIN users u ON r.UserID = u.UserID "
				+ "JOIN moviedirectors md ON md.MovieID = m.MovieID "
				+ "JOIN directors d ON d.DirectorID = md.DirectorID " + "JOIN moviegenres mg ON mg.MovieID = m.MovieID "
				+ "JOIN genres g ON g.GenreID = mg.GenreID " + "WHERE m.MovieID = ? "
				+ "GROUP BY r.ReviewID, r.Rating, r.ReviewText, r.ReviewDate, "
				+ "m.Title, m.ReleaseYear, m.Description, m.PosterURL, u.username "
				+ "ORDER BY ReviewDate DESC LIMIT 10";

		try (Connection mycon = datasource.getConnection();
				PreparedStatement prepStmt = mycon.prepareStatement(selectStatement)) {

			prepStmt.setInt(1, movieID);
			try (ResultSet rs = prepStmt.executeQuery()) {
				while (rs.next()) {
					int reviewID = rs.getInt("ReviewID");
					int rating = rs.getInt("Rating");
					String reviewText = rs.getString("ReviewText");
					String reviewDate = rs.getString("ReviewDate");
					int userID = rs.getInt("UserID"); // Retrieve UserID
					int dbMovieID = rs.getInt("movieID");
					String title = rs.getString("Title");
					String releaseYear = rs.getString("ReleaseYear");
					String description = rs.getString("Description");
					String posterUrl = rs.getString("PosterURL");
					String username = rs.getString("Username");
					String directors = rs.getString("Directors") != null ? rs.getString("Directors") : "";
					String genres = rs.getString("Genres") != null ? rs.getString("Genres") : "";
					double averageRating = rs.getDouble("AverageRating");

					// Update the Reviews constructor to include userID
					Reviews reviewData = new Reviews(reviewID, rating, reviewText, reviewDate, userID, dbMovieID, title,
							releaseYear, description, posterUrl, username, directors, genres, averageRating);
					allReviews.add(reviewData);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error retrieving movie reviews for movie ID: " + movieID, e);
			throw new RuntimeException("Failed to retrieve movie reviews", e);
		}

		return allReviews;
	}

	/**
	 * Adds a new review for a movie.
	 *
	 * @param movieID    the ID of the movie
	 * @param userID     the ID of the user
	 * @param rating     the rating given by the user
	 * @param reviewText the text of the review
	 * @return status code indicating success or failure
	 * @throws NoSuchAlgorithmException if a hashing algorithm is not found
	 */
	public boolean isUserAlreadyReview(int movieID, int userID) {
		try (Connection connection = datasource.getConnection();
				PreparedStatement prepStmt = connection
						.prepareStatement("SELECT COUNT(*) FROM reviews WHERE MovieID = ? AND UserID = ?")) {

			prepStmt.setInt(1, movieID);
			prepStmt.setInt(2, userID);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0; // Returns true if count is greater than 0
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error checking if user already review", e);
		}
		return false; // Return false if user still have not review the selected movie
	}

	public List<String> userReviewMovie(int movieID, int userID) {
		try (Connection connection = datasource.getConnection();
				PreparedStatement prepStmt = connection
						.prepareStatement("SELECT ReviewText, Rating FROM reviews WHERE MovieID = ? AND UserID = ?")) {

			prepStmt.setInt(1, movieID);
			prepStmt.setInt(2, userID);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				List<String> userReviewMovie = new ArrayList<>();
				userReviewMovie.add(rs.getString(1)); // ReviewText
				userReviewMovie.add(rs.getString(2)); // Rating
				return userReviewMovie; // Return user ReviewText and Rating
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error checking if user already review", e);
		}
		return null; // Return false if user still have not review the selected movie
	}

	public int addReview(int movieID, int userID, int rating, String reviewText) throws NoSuchAlgorithmException {
		int status = 0;
		String insertStatement = "INSERT into reviews(MovieID, UserID, Rating, ReviewText) values(?,?,?,?)";

		try (Connection mycon = datasource.getConnection();
				PreparedStatement prepStmt = mycon.prepareStatement(insertStatement)) {

			prepStmt.setInt(1, movieID);
			prepStmt.setInt(2, userID);
			prepStmt.setInt(3, rating);
			prepStmt.setString(4, reviewText);
			int rs = prepStmt.executeUpdate();

			if (rs == 1) {
				status = 1;
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error adding movie review", e);
		}

		return status;
	}

	public int updateReview(int movieID, int userID, int rating, String reviewText) throws NoSuchAlgorithmException {
		int status = 0;
		String updateStatement = "UPDATE reviews SET Rating = ?, ReviewText = ? " + "WHERE MovieID = ? AND UserID = ?";

		try (Connection mycon = datasource.getConnection();
				PreparedStatement prepStmt = mycon.prepareStatement(updateStatement)) {

			prepStmt.setInt(1, rating);
			prepStmt.setString(2, reviewText);
			prepStmt.setInt(3, movieID);
			prepStmt.setInt(4, userID);
			int rs = prepStmt.executeUpdate();

			if (rs == 1) {
				status = 1;
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error updating movie review", e);
		}

		return status;
	}

	public int deleteReview(int movieID, int userID) throws NoSuchAlgorithmException {
		int status = 0;
		String updateStatement = "DELETE FROM reviews WHERE MovieID = ? AND UserID = ?";

		try (Connection mycon = datasource.getConnection();
				PreparedStatement prepStmt = mycon.prepareStatement(updateStatement)) {

			prepStmt.setInt(1, movieID);
			prepStmt.setInt(2, userID);
			int rs = prepStmt.executeUpdate();

			if (rs == 1) {
				status = 1;
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error deleting movie review", e);
		}

		return status;
	}

}
