package controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/deleteReview")
public class DeleteReviewServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String reviewID = request.getParameter("reviewID");

        if (reviewID == null || reviewID.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\":\"Invalid review ID.\"}");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM reviews WHERE reviewID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, reviewID);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"message\":\"Review deleted successfully.\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\":\"Review not found.\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"An error occurred while deleting the review.\"}");
            e.printStackTrace(); // Consider logging this instead of printing the stack trace
        } finally {
            out.close();
        }
    }
}