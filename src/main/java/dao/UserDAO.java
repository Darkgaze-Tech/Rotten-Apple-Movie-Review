package dao;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.sql.DataSource;

import org.apache.commons.text.RandomStringGenerator;

import utils.PasswordHashUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO class for user-related database operations.
 */
public class UserDAO {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    private static final String SELECT_USER_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email,username, PasswordHash, Salt) VALUES (?, ?, ?, ?)";
    private static final String CHECK_EMAIL_QUERY = "SELECT COUNT(*) FROM users WHERE Email = ?";
    private static final String UPDATE_PASSWORD_QUERY="UPDATE users SET PasswordHash=?,Salt=? where email=?;";
  

    private DataSource datasource;

    public UserDAO(DataSource datasource) {
        this.datasource = datasource; // Initialize the DataSource
    }

    protected Connection connect() throws SQLException {
        return datasource.getConnection(); // Get a connection from the DataSource
    }

    public int authenticate(String email, String password) throws NoSuchAlgorithmException {
        int userID = 0;

        try (Connection connection = connect();
             PreparedStatement prepStmt = connection.prepareStatement(SELECT_USER_QUERY)) {
             
            prepStmt.setString(1, email);
            ResultSet rs = prepStmt.executeQuery();

            if (rs.next()) {
                String hashPasswordDB = rs.getString("PasswordHash");
                String saltDB = rs.getString("Salt");

                if (hashPasswordDB.equals(PasswordHashUtils.hashPassword(password + saltDB))) {
                    userID = rs.getInt("UserID");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error during authentication", e);
        }
        return userID;
    }

    public boolean register(String email, String password) throws NoSuchAlgorithmException {
        boolean status = false;

        // Validate email and password here (e.g., regex for email format)
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        String username = extractUsername(email); // Extract username from email
        System.out.println("Username: " + username); // Print username to console
        String salt = PasswordHashUtils.getSalt();
        String passwordHashedSalted = PasswordHashUtils.hashPassword(password + salt);

        try (Connection connection = connect()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement prepStmt = connection.prepareStatement(INSERT_USER_QUERY)) {
                prepStmt.setString(1, email);
                prepStmt.setString(2, username); // Assuming you have a column for username
                prepStmt.setString(3, passwordHashedSalted);
                prepStmt.setString(4, salt);

                int affectedRows = prepStmt.executeUpdate();
                if (affectedRows == 1) {
                    connection.commit(); // Commit transaction
                    status = true;
                } else {
                    connection.rollback(); // Rollback if no rows affected
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error during registration", e);
                connection.rollback(); // Rollback transaction on error
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error", e);
        }

        return status;
    }

    private String extractUsername(String email) {
        return email.substring(0, email.indexOf("@"));
    }


    public boolean isEmailTaken(String email) {
        try (Connection connection = connect();
             PreparedStatement prepStmt = connection.prepareStatement(CHECK_EMAIL_QUERY)) {
             
            prepStmt.setString(1, email);
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
            	 
            System.out.print(rs.getInt(1));
            	
                return rs.getInt(1) > 0; // Returns true if count is greater than 0
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if email is taken", e);
        }
        return false; // Return false if there's an error or email not found
    }
    
    public boolean updatePassword(String email, String password) throws NoSuchAlgorithmException {
    	 
    	
    	 
    	boolean status = false;
    	 String salt = PasswordHashUtils.getSalt();
         String passwordHashedSalted = PasswordHashUtils.hashPassword(password + salt);
    	
		  try (Connection connection = connect()) {
	            connection.setAutoCommit(false); // Start transaction
			String updateStatement = UPDATE_PASSWORD_QUERY;
			
			try (PreparedStatement prepStmt = connection.prepareStatement(updateStatement)){
		
			  prepStmt.setString(1, passwordHashedSalted);
	            prepStmt.setString(2, salt);
	            prepStmt.setString(3, email);
	            int affectedRows = prepStmt. executeUpdate();
	            connection.commit(); // Commit transaction
	            prepStmt.close();
	            if (affectedRows == 1) {
                    status = true;
                }
	                        
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error during token update", e);
                connection.rollback(); // Rollback transaction on error
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error", e);
        }
		
    	
    
    	return status;
    }
}
