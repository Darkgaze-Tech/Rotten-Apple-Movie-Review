package controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JWTUtility;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import dao.UserDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * Servlet implementation class ResetPasswordServlet
 */
public class ResetPasswordServlet extends HttpServlet {
    private static final String MESSAGE_PAGE = "Login.jsp";  // Changed from Message.jsp to Login.jsp
    private static final Logger logger = Logger.getLogger(ResetPasswordServlet.class.getName());
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResetPasswordServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Resource(name = "jdbc/moviedb")
    private DataSource datasource;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO(datasource);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub

        String token = request.getParameter("token");
        String password = request.getParameter("password");

        String email="";
        logger.log(Level.INFO, "JWT token " + token);

        Jws<Claims> claims;
        try {
            claims = JWTUtility.parseJwt(token);
            email = claims.getPayload().get("useremail").toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            request.setAttribute("errorMessage", " Password reset link expired. Please generate new link using http://localhost:8080/rottenapple/Forgotpassword.jsp ");
            request.getRequestDispatcher(MESSAGE_PAGE).forward(request, response);
        }

        if(email != null) {
            try {
                userDAO.updatePassword(email,password);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                request.setAttribute("errorMessage", " Password change unsuccessful" );
                request.getRequestDispatcher(MESSAGE_PAGE).forward(request, response);
            }
            logger.log(Level.INFO, "password update in db successful");
            request.setAttribute("Message", " Password change successful , please login with your new password ");
            request.getRequestDispatcher(MESSAGE_PAGE).forward(request, response);
        }

        request.setAttribute("errorMessage", " Password change unsuccessful" );
        request.getRequestDispatcher(MESSAGE_PAGE).forward(request, response);
    }
}