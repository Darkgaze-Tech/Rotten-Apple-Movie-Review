package controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.UserAccount;
import model.UserProfile.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.jakarta.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Servlet implementation class LoginCallbackServlet
 */
@WebServlet("/GoogleLoginCallbackServlet")
public class GoogleLoginCallbackServlet extends HttpServlet {
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	private static final String CLIENT_ID = "";
	private static final String CLIENT_SECRET = "";
	private static final String REDIRECT_URI = "http://localhost:8080/rottenapple/GoogleLoginCallbackServlet";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String code = request.getParameter("code");

		if (code == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing authorization code");
			return;
		}

		try {
			GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(),
					JacksonFactory.getDefaultInstance(), CLIENT_ID, CLIENT_SECRET, code, REDIRECT_URI).execute();

			GoogleIdToken idToken = tokenResponse.parseIdToken();
			GoogleIdToken.Payload payload = idToken.getPayload();

			String email = payload.getEmail();

			// Check if user exists in the database by email
			UserAccount existingUser = findUserByEmail(email); // Change to find by email

			HttpSession session = request.getSession(true);

			if (existingUser != null) {
				session.setAttribute("userID", existingUser.getUser_id());
				session.setAttribute("userEmail", existingUser.getEmail());
			} else {
				insertUser(email); // Insert the user with their userId and email
				session.setAttribute("userEmail", email);
			}

			// Set other session attributes
			session.setAttribute("userName", (String) payload.get("name"));
			session.setAttribute("userPicture", (String) payload.get("picture"));
			session.setAttribute("accessToken", tokenResponse.getAccessToken());

			response.sendRedirect("MovieListServlet");

		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("Error while processing the authentication: " + e.getMessage());
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("Authentication failed: " + e.getMessage());
		}
	}

	private UserAccount findUserByEmail(String email) throws Exception {
		String query = "SELECT * FROM users WHERE email = ?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, email);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return new UserAccount(resultSet.getInt("userId"), resultSet.getString("email"));
			}
			return null;
		}
	}

	private void insertUser(String email) throws Exception {
		// Validate the email format
		if (email == null || !email.contains("@")) {
			throw new IllegalArgumentException("Invalid email format.");
		}

		// Extract username from email
		String username = email.substring(0, email.indexOf('@'));

		String query = "INSERT INTO users (email, username) VALUES (?, ?)";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, email);
			statement.setString(2, username);
			statement.executeUpdate();
		}
	}

}