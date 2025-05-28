package model;

public class Movie {
	private int movieID;
	private String title;
	private int releaseYear;
	private String description;
	private String posterUrl;
	private String directors;
	private String genres;
	private double averageRating;

	public Movie() {

	}

	public Movie(int movieID, String title, int releaseYear, String description, String posterUrl) {
		this.movieID = movieID;
		this.title = title;
		this.releaseYear = releaseYear;
		this.description = description;
		this.posterUrl = posterUrl;
	}

	public Movie(int movieId, String title, int releaseYear, String genres, String directors, String description,
			String posterUrl, double averageRating) {
		this.movieID = movieId;
		this.title = title;
		this.releaseYear = releaseYear;
		this.genres = genres;
		this.directors = directors;
		this.description = description;
		this.posterUrl = posterUrl;
		this.averageRating = averageRating;
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

	public int getMovieID() {
		return movieID;
	}

	public String getTitle() {
		return title;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public String getDescription() {
		return description;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setMovieID(int movieID) {
		this.movieID = movieID;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

}
