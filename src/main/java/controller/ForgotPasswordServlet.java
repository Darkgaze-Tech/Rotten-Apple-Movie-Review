package controller;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import utils.EmailUtility;
import utils.JWTUtility;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.logging.Level;

import dao.UserDAO;

import java.util.logging.Logger;
 
import javax.sql.DataSource;
@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
	private static final String MESSAGE_PAGE = "Message.jsp";
	private static final String FORGOTPASSWORD_PAGE = "Forgotpassword.jsp";
	private static final Logger logger = Logger.getLogger(ForgotPasswordServlet.class.getName());
	private static final long serialVersionUID = 1L;
	 
	    private String host;
	    private String port;
	    private String email;
	    private String name;
	    private String pass;
	    private UserDAO user;
	    
	    @Override
	    public void init() throws ServletException {
	        // reads SMTP server setting from web.xml file
	        ServletContext context = getServletContext();
	        host = context.getInitParameter("host");
	        port = context.getInitParameter("port");
	        email = context.getInitParameter("email");
	        name = context.getInitParameter("name");
	        pass = context.getInitParameter("pass");
	        super.init();	    	
	    	try {
	    		user = new UserDAO(datasource);
	    	}
	    	catch(Exception exc)
	    	{
	    		throw new ServletException(exc);
	    	}
	    }
		    
	    @Resource(name = "jdbc/moviedb")
	    private DataSource datasource;
 
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	 
	    	//doPost(request, response);
	 
	    }
	    
	   
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        String recipient = request.getParameter("email");
	        String subject = "Your Password has been reset";
	        boolean userpresent= user.isEmailTaken(recipient);
	       
	        if(userpresent) {
	        
	       String JWTToken = JWTUtility.generateToken(recipient);
	       
	       HttpRequest httprequest =  HttpRequest.newBuilder()
	    		   	  .GET()
	    		      .uri(URI.create("http://localhost:8080/rottenapple/Resetpassword.jsp?token="+JWTToken))
	    		      .timeout(Duration.ofMinutes(1))
	    		      .header("Content-Type", "application/json")
	    		      .build();
	     
	       logger.log(Level.INFO, "JWT token " + JWTToken);
	       logger.log(Level.INFO, "Httprequest " + httprequest);
	      
			
	        String content = "Hi, this is your new Token to reset: " + httprequest;
	        content += "\nNote: For security reason link is valid for 10mins, "
	                + "you this link to reset your password";
	 
	        try {
	            EmailUtility.sendEmail(host, port, email, name, pass,
	                    recipient, subject, content);
	           
	        } catch (Exception ex) {
	            ex.printStackTrace();
	           
	        } finally {
	            request.setAttribute("errorMessage", "Link to reset your password has been sent. Please check your e-mail");
	            request.getRequestDispatcher(FORGOTPASSWORD_PAGE).forward(request, response);
	        }
	        }
			
			else {
				request.setAttribute("errorMessage", "There were an error resetting password , Email doest not exist.Please register ");
	            request.getRequestDispatcher(FORGOTPASSWORD_PAGE).forward(request, response);
				
				
			}
	        
	 
	    
		}
	   


}
