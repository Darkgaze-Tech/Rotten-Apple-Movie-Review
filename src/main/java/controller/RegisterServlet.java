package controller;

import dao.UserDAO;
import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet implementation class RegisterServlet
 * Handles user account registration.
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String LOGIN_PAGE = "Login.jsp";
    private static final String REGISTER_PAGE = "Register.jsp";
    private static final String HOME_PAGE = "Home.jsp";
    private static final Logger logger = Logger.getLogger(RegisterServlet.class.getName());

    @Resource(name = "jdbc/moviedb")
    private DataSource datasource;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO(datasource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            processRegistration(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during account registration: " + e.getMessage(), e);
            throw new ServletException("Registration error: " + e.getMessage(), e);
        }
    }

    private void processRegistration(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Invalid email format.");
            forwardToJsp(request, response, REGISTER_PAGE);
            return;
        }

        if (!isValidPassword(password)) {
            request.setAttribute("errorMessage", "Password must be at least 8 characters long and contain upper/lowercase letters, numbers, and special characters.");
            forwardToJsp(request, response, REGISTER_PAGE);
            return;
        }

        // Check if the email is already registered
        if (userDAO.isEmailTaken(email)) {
            request.setAttribute("errorMessage", "Email is already registered.");
            forwardToJsp(request, response, REGISTER_PAGE);
            return;
        }

        boolean status = userDAO.register(email, password); // Ensure password is hashed in DAO
        if (status) {
            forwardToJsp(request, response, LOGIN_PAGE);
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            forwardToJsp(request, response, REGISTER_PAGE);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 
               && password.matches(".*[A-Z].*")
               && password.matches(".*[a-z].*")
               && password.matches(".*[0-9].*")
               && password.matches(".*[!@#$%^&*()].*");
    }

    private void forwardToJsp(HttpServletRequest request, HttpServletResponse response, String page) 
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
