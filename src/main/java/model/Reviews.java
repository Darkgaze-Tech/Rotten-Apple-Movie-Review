package model;


public class Reviews {
	

	private int reviewID;
	private int rating;
	private String reviewText;
	private String reviewDate;
	private int userID;
	private int movieID;
	private String title;
	private String releaseYear;
	private String description;
	private String posterUrl;
	private String username;
	private String directors;
	private String genres;
	private double averageRating;
	
	public Reviews(int reviewID, int rating, String reviewText, String reviewDate, int userID, int movieID, String title, String releaseYear,
			String description, String posterUrl, String username, String directors, String genres, double averageRating) {
		super();
		this.reviewID = reviewID;
		this.rating = rating;
		this.reviewText = reviewText;
		this.reviewDate = reviewDate;
		this.userID = userID;
		this.movieID = movieID;
		this.title = title;
		this.releaseYear = releaseYear;
		this.description = description;
		this.posterUrl = posterUrl;
		this.username = username;
		this.directors = directors;
		this.genres = genres;
		this.averageRating = averageRating;
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

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public int getReviewID() {
		return reviewID;
	}
	public void setReviewID(int reviewID) {
		this.reviewID = reviewID;
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
	public String getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}
	public int getMovieID() {
		return movieID;
	}

	public void setMovieID(int movieID) {
		this.movieID = movieID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getReleaseYear() {
		return releaseYear;
	}
	public void setReleaseYear(String releaseYear) {
		this.releaseYear = releaseYear;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
	public String getUsername() {
		return username;
	}
	public void setEmail(String username) {
		this.username = username;
	}
	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directors) {
		this.directors = directors;
	}

	public String getGenres() {
		return genres;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}
	
	public double getAverageRating() {
        return averageRating;
    }

}
