package controller;

import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Movie;
import model.Reviews;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import dao.MovieDAO;
import dao.ReviewsDAO;

@WebServlet("/reviewMovie")
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ReviewsDAO reviews;
    private MovieDAO movie;

    @Resource(name = "jdbc/moviedb")
    private DataSource datasource;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            reviews = new ReviewsDAO(datasource);
            movie = new MovieDAO(datasource);
        } catch (Exception exc) {
            throw new ServletException("Error initializing ReviewsDAO", exc);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Check for any flash messages
            HttpSession session = request.getSession();
            String statusMessage = (String) session.getAttribute("statusMessage");
            if (statusMessage != null) {
                request.setAttribute("statusMessage", statusMessage);
                session.removeAttribute("statusMessage"); // Clear the message
            }

            Integer operationStatus = (Integer) session.getAttribute("operationStatus");
            if (operationStatus != null) {
                request.setAttribute("operationStatus", operationStatus);
                session.removeAttribute("operationStatus"); // Clear the status
            }

            getAllCurrentMovieReviews(request, response);
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void getAllCurrentMovieReviews(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userID = String.valueOf(request.getSession().getAttribute("userID"));
		int movieID = Integer.parseInt(request.getParameter("movieID"));

		// Set isLoggedIn attribute
		boolean isLoggedIn = userID != null && !userID.equals("null");
		request.setAttribute("isLoggedIn", isLoggedIn);
		
		// Check if movie exists
        Movie movieDetails = movie.getCurrentMovie(movieID);
        System.out.println("Fetched movie details: " + movieDetails);
        if (movieDetails == null) {
            request.getRequestDispatcher("MovieNotFound.jsp").forward(request, response);
            return;
        }
        
		// Get all reviews for the movie
		List<Reviews> allReviews = reviews.getAllCurrentMovieReviews(movieID);
		request.setAttribute("listReviews", allReviews);

		// Check if there are any reviews and set attributes accordingly
		if (allReviews != null && !allReviews.isEmpty()) {
			Reviews sampleReview = allReviews.get(0);
			setMovieAttributes(request, sampleReview);

			// Since average rating is part of the review data, get it from the first review
			double averageRating = sampleReview.getAverageRating();
			request.setAttribute("averageRating", averageRating);
		} else {
			setMovieAttributes(request, movieDetails);
			request.setAttribute("averageRating", 0.0); // No reviews, set average rating to 0
		}

		// Set user-specific attributes if logged in
		if (isLoggedIn) {
			request.setAttribute("userID", userID);
			boolean hasReviewed = reviews.isUserAlreadyReview(movieID, Integer.parseInt(userID));
			if (hasReviewed) {
				List<String> userReviewMovie = reviews.userReviewMovie(movieID, Integer.parseInt(userID));
				request.setAttribute("currentReviewText", userReviewMovie.get(0));
				request.setAttribute("currentRating", userReviewMovie.get(1));
				request.setAttribute("buttonStatus", 1);
			} else {
				request.setAttribute("buttonStatus", 0);
			}
		}

		// Forward to the JSP
		RequestDispatcher dispatcher = request.getRequestDispatcher("MovieDetails.jsp");
		dispatcher.forward(request, response);
	}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("act");
            HttpSession session = request.getSession();
            int movieID = Integer.parseInt(request.getParameter("movieID"));

            // Clear any existing status messages first
            clearAllStatus(session);

            if (action != null && request.getParameter("rating") != null
                    && request.getParameter("user-review") != null) {

                int status = 0;

                switch (action) {
                    case "Submit Review":
                        status = handleAddReview(request);
                        if (status > 0) {
                            session.setAttribute("addStatus", "1");
                        }
                        break;
                    case "Update Review":
                        status = handleUpdateReview(request);
                        if (status > 0) {
                            session.setAttribute("updateStatus", "1");
                        }
                        break;
                    case "Delete Review":
                        status = handleDeleteReview(request);
                        if (status > 0) {
                            session.setAttribute("deleteStatus", "1");
                        }
                        break;
                }

                // Redirect to prevent form resubmission
                response.sendRedirect(request.getContextPath() + "/reviewMovie?movieID=" + movieID);
                return;
            }

            // If we get here, something went wrong with the parameters
            session.setAttribute("errorMessage", "Invalid request parameters");
            response.sendRedirect(request.getContextPath() + "/reviewMovie?movieID=" + movieID);

        } catch (Exception e) {
            HttpSession session = request.getSession();
            clearAllStatus(session);
            session.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reviewMovie?movieID=" + 
                request.getParameter("movieID"));
        }
    }

    // Helper method to clear all status attributes
    private void clearAllStatus(HttpSession session) {
        session.removeAttribute("addStatus");
        session.removeAttribute("updateStatus");
        session.removeAttribute("deleteStatus");
        session.removeAttribute("errorMessage");
    }

    private int handleAddReview(HttpServletRequest request) throws Exception {
        int movieID = Integer.parseInt(request.getParameter("movieID"));
        int userID = Integer.parseInt(String.valueOf(request.getSession().getAttribute("userID")));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String userReview = request.getParameter("user-review");
        
        return reviews.addReview(movieID, userID, rating, userReview);
    }

    private int handleUpdateReview(HttpServletRequest request) throws Exception {
        int movieID = Integer.parseInt(request.getParameter("movieID"));
        int userID = Integer.parseInt(String.valueOf(request.getSession().getAttribute("userID")));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String userReview = request.getParameter("user-review");
        
        return reviews.updateReview(movieID, userID, rating, userReview);
    }

    private int handleDeleteReview(HttpServletRequest request) throws Exception {
        int movieID = Integer.parseInt(request.getParameter("movieID"));
        int userID = Integer.parseInt(String.valueOf(request.getSession().getAttribute("userID")));
        
        return reviews.deleteReview(movieID, userID);
    }

    private void setMovieAttributes(HttpServletRequest request, Movie movie) {
        request.setAttribute("movieID", movie.getMovieID());
        request.setAttribute("title", movie.getTitle());
        request.setAttribute("releaseYear", movie.getReleaseYear());
        request.setAttribute("description", movie.getDescription());
        request.setAttribute("posterUrl", movie.getPosterUrl());
        request.setAttribute("directors", movie.getDirectors());
        request.setAttribute("genres", movie.getGenres());
    }

    private void setMovieAttributes(HttpServletRequest request, Reviews review) {
        request.setAttribute("movieID", review.getMovieID());
        request.setAttribute("title", review.getTitle());
        request.setAttribute("releaseYear", review.getReleaseYear());
        request.setAttribute("description", review.getDescription());
        request.setAttribute("posterUrl", review.getPosterUrl());
        request.setAttribute("directors", review.getDirectors());
        request.setAttribute("genres", review.getGenres());
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("errorMessage", "An error occurred while processing your request.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("MovieNotFound.jsp");
        dispatcher.forward(request, response);
    }
}