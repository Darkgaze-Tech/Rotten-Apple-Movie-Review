package controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SimpleUserBasedRecommendation;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/recommend")
public class RecommendationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private SimpleUserBasedRecommendation recommendationSystem;

    @Override
    public void init() throws ServletException {
        super.init();
        if (dataSource == null) {
            throw new ServletException("DataSource is null! Check your resource configuration.");
        }
        recommendationSystem = new SimpleUserBasedRecommendation(dataSource);
        System.out.println("RecommendationServlet initialized successfully.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = parseUserId(req);
        if (userId == -1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            return;
        }

        try {
            List<SimpleUserBasedRecommendation.MovieRecommendation> recommendations = 
                recommendationSystem.recommendMovies(userId, 8); // Get top 8 recommendations
            req.setAttribute("recommendations", recommendations);
            req.getRequestDispatcher("/recommendations.jsp").forward(req, resp);
        } catch (SQLException e) {
            handleError(e, resp);
        }
    }

    private int parseUserId(HttpServletRequest req) {
        String userIdParam = req.getParameter("UserID");
        try {
            int userId = Integer.parseInt(userIdParam);
            if (userId > 0) {
                return userId;
            }
        } catch (NumberFormatException e) {
            // Log the error
            System.err.println("Invalid user ID format: " + userIdParam);
        }
        return -1;
    }

    private void handleError(Exception e, HttpServletResponse resp) throws IOException {
        e.printStackTrace(); // Log the error stack trace to the console
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
    }
}