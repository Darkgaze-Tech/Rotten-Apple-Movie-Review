package controller;

import dao.UserProfileDAO;
import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.UserProfile.Review;
import model.UserProfile.User;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(urlPatterns = { "/listReviews", "/listReviewsByUserID" })
public class UserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserProfileServlet.class.getName());

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private UserProfileDAO userProfileDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userProfileDAO = new UserProfileDAO(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if ("/listReviews".equals(path)) {
                handleListReviews(request, response);
            } else if ("/listReviewsByUserID".equals(path)) {
                handleListReviewsByUserID(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Database error occurred", ex);
            handleDatabaseError(request, response, ex);
        } catch (NumberFormatException ex) {
            logger.log(Level.WARNING, "Invalid user ID format", ex);
            request.setAttribute("errorMessage", "Invalid user ID format");
            forwardToJsp(request, response);
        }
    }

    private void handleListReviews(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        try {
            List<Review> listReview = userProfileDAO.listAllReviews();
            request.setAttribute("listReview", listReview);
            forwardToJsp(request, response);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving all reviews", ex);
            throw ex;
        }
    }

    private void handleListReviewsByUserID(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String userId = request.getParameter("userID");
        if (userId == null || userId.isEmpty()) {
            request.setAttribute("errorMessage", "User ID is required");
            forwardToJsp(request, response);
            return;
        }

        try {
            int userID = Integer.parseInt(userId);
            
            // First try to get the user
            User user = userProfileDAO.getUserByID(userID);
            if (user == null) {
                request.setAttribute("errorMessage", "User not found");
                forwardToJsp(request, response);
                return;
            }

            // If user exists, get their reviews and other information
            List<Review> listReview = userProfileDAO.listReviewsByUserID(userID);
            Timestamp lastReviewDate = userProfileDAO.getLastReviewDateByUserID(userID);

            // Set attributes
            request.setAttribute("user", user);
            request.setAttribute("listReview", listReview);
            request.setAttribute("lastReviewDate", lastReviewDate);
            request.setAttribute("noReviews", listReview.isEmpty());

            // Check if the current user is the profile owner
            HttpSession session = request.getSession();
            Integer loggedInUserId = (Integer) session.getAttribute("userID");
            boolean isOwner = (loggedInUserId != null && loggedInUserId.equals(userID));
            request.setAttribute("isOwner", isOwner);

        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("User not found")) {
                request.setAttribute("errorMessage", "User not found");
            } else {
                logger.log(Level.SEVERE, "Database error while retrieving user profile", ex);
                request.setAttribute("errorMessage", "An error occurred while retrieving the user profile");
            }
        }
        
        forwardToJsp(request, response);
    }

    private void handleDatabaseError(HttpServletRequest request, HttpServletResponse response, SQLException ex) 
            throws ServletException, IOException {
        String errorMessage;
        if (ex.getMessage() != null && ex.getMessage().contains("User not found")) {
            errorMessage = "User not found";
        } else {
            errorMessage = "A database error occurred. Please try again later.";
        }
        request.setAttribute("errorMessage", errorMessage);
        forwardToJsp(request, response);
    }

    private void forwardToJsp(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("UserProfile.jsp");
        dispatcher.forward(request, response);
    }
}