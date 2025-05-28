package dao;

import model.UserProfile.Review;
import model.UserProfile.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserProfileDAO {
    private static final Logger logger = Logger.getLogger(UserProfileDAO.class.getName());
    private final DataSource dataSource;

    // SQL query constants
    private static final String SELECT_ALL_REVIEWS = "SELECT * FROM reviews LEFT JOIN movies ON reviews.MovieID = movies.MovieID ORDER BY reviews.reviewDate DESC";
    private static final String SELECT_REVIEWS_BY_USER_ID = "SELECT * FROM reviews LEFT JOIN movies ON reviews.MovieID = movies.MovieID WHERE reviews.UserID = ? ORDER BY reviews.reviewDate DESC";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE UserID = ?";
    private static final String SELECT_LAST_REVIEW_DATE = "SELECT MAX(reviewDate) AS lastReviewDate FROM reviews WHERE UserID = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE reviewID = ?";
    private static final String UPDATE_USER_INFO = "UPDATE users SET username = ?, bio = ? WHERE UserID = ?";
    private static final String EDIT_REVIEW = "UPDATE reviews SET reviewText = ? WHERE reviewID = ?";

    public UserProfileDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private List<Review> getReviews(String sql, Integer userID) throws SQLException {
        List<Review> listReview = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userID != null) {
                statement.setInt(1, userID);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    listReview.add(new Review(
                    	resultSet.getInt("movieID"),
                        resultSet.getInt("reviewID"),
                        resultSet.getString("posterURL"),
                        resultSet.getString("title"),
                        resultSet.getInt("releaseYear"),
                        resultSet.getInt("rating"),
                        resultSet.getTimestamp("reviewDate"),
                        resultSet.getString("reviewText")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching reviews", e);
            throw e; // Rethrow to handle it upstream
        }

        return listReview;
    }

    public List<Review> listAllReviews() throws SQLException {
        return getReviews(SELECT_ALL_REVIEWS, null);
    }

    public List<Review> listReviewsByUserID(int userID) throws SQLException {
        return getReviews(SELECT_REVIEWS_BY_USER_ID, userID);
    }

    public User getUserByID(int userID) throws SQLException {
        User user = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_ID)) {

            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                        resultSet.getInt("userID"),
                        resultSet.getString("username"),
                        resultSet.getString("bio"),
                        resultSet.getTimestamp("createdAt")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching user by ID: " + userID, e);
            throw e; // Rethrow to handle it upstream
        }

        if (user == null) {
            throw new SQLException("User not found with ID: " + userID);
        }

        return user; // Return the User directly
    }

    public Timestamp getLastReviewDateByUserID(int userID) throws SQLException {
        Timestamp lastReviewDate = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_LAST_REVIEW_DATE)) {

            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    lastReviewDate = resultSet.getTimestamp("lastReviewDate");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching last review date for user ID: " + userID, e);
            throw e; // Rethrow to handle it upstream
        }

        // Return null if no last review date was found, instead of throwing an exception
        return lastReviewDate; 
    }
    
    public void deleteReview(int reviewID) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_REVIEW)) {

            statement.setInt(1, reviewID);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No review found with ID: " + reviewID);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting review with ID: " + reviewID, e);
            throw e; // Rethrow to handle it upstream
        }
    }

    // New method to update user information
    public boolean updateUserInfo(int userID, String username, String bio) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_INFO)) {

            statement.setString(1, username);
            statement.setString(2, bio);
            statement.setInt(3, userID);
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0; // Return true if update was successful
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating user info for user ID: " + userID, e);
            throw e; // Rethrow to handle it upstream
        }
    }

    // New method to edit a review
    public boolean editReview(int reviewID, String newText) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(EDIT_REVIEW)) {

            statement.setString(1, newText);
            statement.setInt(2, reviewID);
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0; // Return true if update was successful
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error editing review with ID: " + reviewID, e);
            throw e; // Rethrow to handle it upstream
        }
    }
}
