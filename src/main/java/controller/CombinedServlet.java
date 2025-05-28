package controller;

import dao.UserProfileDAO;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/updateOrEdit")
public class CombinedServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private UserProfileDAO userProfileDAO;
    private static final Logger LOGGER = Logger.getLogger(CombinedServlet.class.getName());

    private static final String UPDATE_USER_INFO = "updateUserInfo";
    private static final String EDIT_REVIEW = "editReview";

    @Override
    public void init() throws ServletException {
        userProfileDAO = new UserProfileDAO(dataSource);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        try {
            switch (action) {
                case UPDATE_USER_INFO:
                    int userID = Integer.parseInt(request.getParameter("userID"));
                    String username = request.getParameter("username");
                    String bio = request.getParameter("bio");

                    boolean updateStatus = userProfileDAO.updateUserInfo(userID, username, bio);
                    writeResponse(response, updateStatus, "User info updated successfully.", "Failed to update user info.");
                    break;

                case EDIT_REVIEW:
                    int reviewID = Integer.parseInt(request.getParameter("reviewID"));
                    String newText = request.getParameter("reviewText");

                    boolean editStatus = userProfileDAO.editReview(reviewID, newText);
                    writeResponse(response, editStatus, "Review updated successfully.", "Failed to update review.");
                    break;

                default:
                    writeErrorResponse(response, "Invalid action specified.");
                    break;
            }
        } catch (NumberFormatException e) {
            writeErrorResponse(response, "Invalid input format: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.severe("Error processing request for action '" + action + "': " + e.getMessage());
            writeErrorResponse(response, "An error occurred while processing your request.");
        }
    }

    private void writeResponse(HttpServletResponse response, boolean success, String successMessage, String failureMessage) throws IOException {
        String message = success ? successMessage : failureMessage;
        response.getWriter().write("{\"success\": " + success + ", \"message\": \"" + message + "\"}");
    }

    private void writeErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.getWriter().write("{\"success\": false, \"error\": \"" + errorMessage + "\"}");
    }
}
