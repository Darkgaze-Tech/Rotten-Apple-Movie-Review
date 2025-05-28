package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/searchMovies")
public class MovieSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        response.setContentType("application/json");

        List<String> result = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.println("{\"error\":\"Query parameter is required.\"}");
            }
            return;
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("SELECT Title, ReleaseYear FROM movies WHERE CONCAT(Title, ' (', ReleaseYear, ')') LIKE ?")) {

            statement.setString(1, "%" + query + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("Title");
                    int releaseYear = resultSet.getInt("ReleaseYear");
                    result.add(title + " (" + releaseYear + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logging framework
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.println("{\"error\":\"An error occurred while processing your request.\"}");
            }
            return;
        }

        // Use StringBuilder for building JSON response
        StringBuilder jsonResponse = new StringBuilder("{\"movies\":[");

        for (int i = 0; i < result.size(); i++) {
            jsonResponse.append("\"").append(result.get(i)).append("\"");
            if (i < result.size() - 1) {
                jsonResponse.append(",");
            }
        }
        jsonResponse.append("]}");

        // Return the result in JSON format
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonResponse.toString());
        }
    }
}
