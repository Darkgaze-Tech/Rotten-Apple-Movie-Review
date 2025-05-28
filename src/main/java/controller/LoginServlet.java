package controller;

import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import javax.sql.DataSource;

import dao.UserDAO;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private static final String HOME_PAGE = "MovieListServlet"; // Redirect after successful login
    private static final String LOGIN_PAGE = "Login.jsp"; // Redirect to login page on failure

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            loginAccount(request, response);
        } catch (Exception e) {
            e.printStackTrace(); // Handle appropriately in production code
            request.setAttribute("errorMessage", "An error occurred during login. Please try again.");
            RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
            dispatcher.forward(request, response);
        }
    }

    private void loginAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO(dataSource);
        int userID = userDAO.authenticate(email, password);

        if (userID != 0) {
            // Successful authentication
            HttpSession session = request.getSession();
            session.setAttribute("userID", userID);
            response.sendRedirect(HOME_PAGE); // Redirect to MovieListServlet
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Invalid email or password.");
            RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
            dispatcher.forward(request, response);
        }
    }
}
