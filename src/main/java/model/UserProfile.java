package model;

// Import necessary classes
import java.sql.Timestamp;

public class UserProfile {

	public static class Review {
		// Fields representing the attributes of a review
		private int reviewID; // Unique identifier for the review
		private int movieID; // Identifier for the associated movie
		private int userID; // Identifier for the user who wrote the review
		private int rating; // Rating given by the user (e.g., out of 5 or 10)
		private String reviewText; // Text of the review
		private Timestamp reviewDate; // Date and time when the review was created
		private String title;
		private int releaseYear;
		private String posterURL;

		// Constructor for review (with all fields)
		public Review(int reviewID, int movieID, int userID, int rating, String reviewText, Timestamp reviewDate) {
			this.reviewID = reviewID;
			this.movieID = movieID;
			this.userID = userID;
			this.rating = rating;
			this.reviewText = reviewText;
			this.reviewDate = reviewDate;
		}

		// Constructor for review for profile
		public Review(int movieID, int reviewID, String posterURL, String title, int releaseYear, int rating, Timestamp reviewDate,
				String reviewText) {
			this.movieID = movieID;
			this.reviewID = reviewID;
			this.posterURL = posterURL;
			this.title = title;
			this.releaseYear = releaseYear;
			this.rating = rating;
			this.reviewDate = reviewDate;
			this.reviewText = reviewText;
		}


		// Getters and Setters
		public int getReviewID() {
			return reviewID;
		}

		public void setReviewID(int reviewID) {
			this.reviewID = reviewID;
		}

		public int getMovieID() {
			return movieID;
		}

		public void setMovieID(int movieID) {
			this.movieID = movieID;
		}

		public int getUserID() {
			return userID;
		}

		public void setUserID(int userID) {
			this.userID = userID;
		}

		public int getRating() {
			return rating;
		}

		public void setRating(int rating) {
			this.rating = rating;
		}

		public String getReviewText() {
			return reviewText;
		}

		public void setReviewText(String reviewText) {
			this.reviewText = reviewText;
		}

		public Timestamp getReviewDate() {
			return reviewDate;
		}

		public void setReviewDate(Timestamp reviewDate) {
			this.reviewDate = reviewDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getReleaseYear() {
			return releaseYear;
		}

		public void setReleaseYear(int releaseYear) {
			this.releaseYear = releaseYear;
		}

		public String getPosterURL() {
			return posterURL;
		}

		public void setPosterURL(String posterURL) {
			this.posterURL = posterURL;
		}
	}

	public static class User {
		private int userID;
		private String username;
		private String bio;
		private Timestamp createdAt;

		public User(int userID, String username, String bio, Timestamp createdAt) {
			this.userID = userID;
			this.username = username;
			this.bio = bio;
			this.createdAt = createdAt;
		}

		// Getters and Setters
		public String getUsername() {
			return username;
		}

		public int getUserID() {
			return userID;
		}

		public void setUserID(int userID) {
			this.userID = userID;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getBio() {
			return bio;
		}

		public void setBio(String bio) {
			this.bio = bio;
		}

		public Timestamp getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Timestamp createdAt) {
			this.createdAt = createdAt;
		}
	}
}
