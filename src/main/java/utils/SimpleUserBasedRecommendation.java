package utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleUserBasedRecommendation {
    private final DataSource dataSource;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final double SIMILARITY_THRESHOLD = 0.1; // Minimum similarity to consider

    public SimpleUserBasedRecommendation(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<MovieRecommendation> recommendMovies(int userId, int numRecommendations) throws SQLException {
        // Get all user ratings and user averages
        UserRatingData userData = getUserRatings();
        System.out.println("Total users with ratings: " + userData.userRatings.size());

        if (!userData.userRatings.containsKey(userId)) {
            System.out.println("User " + userId + " has no ratings.");
            return Collections.emptyList();
        }

        // Calculate similarities with other users
        Map<Integer, Double> userSimilarities = calculateUserSimilarities(userId, userData);
        System.out.println("Number of similar users: " + userSimilarities.size());

        // Calculate predicted ratings for unwatched movies
        Map<Integer, Double> predictedRatings = calculatePredictedRatings(userId, userData, userSimilarities);
        System.out.println("Number of predicted movies: " + predictedRatings.size());

        // Get top recommendations
        List<MovieRecommendation> recommendations = predictedRatings.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(entry -> getMovieDetails(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        System.out.println("Number of recommendations: " + recommendations.size());
        return recommendations;
    }

    private static class UserRatingData {
        final Map<Integer, Map<Integer, Integer>> userRatings;
        final Map<Integer, Double> userAverages;

        UserRatingData(Map<Integer, Map<Integer, Integer>> userRatings, Map<Integer, Double> userAverages) {
            this.userRatings = userRatings;
            this.userAverages = userAverages;
        }
    }

    private UserRatingData getUserRatings() throws SQLException {
        Map<Integer, Map<Integer, Integer>> userRatings = new HashMap<>();
        Map<Integer, List<Integer>> userRatingsList = new HashMap<>();
        String query = "SELECT UserID, MovieID, Rating FROM reviews";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("UserID");
                int movieId = rs.getInt("MovieID");
                int rating = rs.getInt("Rating");

                userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(movieId, rating);
                userRatingsList.computeIfAbsent(userId, k -> new ArrayList<>()).add(rating);
            }
        }

        // Calculate average ratings for each user
        Map<Integer, Double> userAverages = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : userRatingsList.entrySet()) {
            double average = entry.getValue().stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
            userAverages.put(entry.getKey(), average);
        }

        return new UserRatingData(userRatings, userAverages);
    }

    private Map<Integer, Double> calculateUserSimilarities(int userId, UserRatingData userData) {
        Map<Integer, Double> similarities = new HashMap<>();
        Map<Integer, Integer> userRating = userData.userRatings.get(userId);

        for (Map.Entry<Integer, Map<Integer, Integer>> entry : userData.userRatings.entrySet()) {
            int otherUserId = entry.getKey();
            if (otherUserId != userId) {
                double similarity = calculateCosineSimilarity(userRating, entry.getValue());
                if (similarity > SIMILARITY_THRESHOLD) {
                    similarities.put(otherUserId, similarity);
                }
            }
        }

        return similarities;
    }

    private double calculateCosineSimilarity(Map<Integer, Integer> ratings1, Map<Integer, Integer> ratings2) {
        Set<Integer> commonMovies = new HashSet<>(ratings1.keySet());
        commonMovies.retainAll(ratings2.keySet());

        if (commonMovies.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int movieId : commonMovies) {
            int rating1 = ratings1.get(movieId);
            int rating2 = ratings2.get(movieId);
            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private Map<Integer, Double> calculatePredictedRatings(int userId, UserRatingData userData,
            Map<Integer, Double> userSimilarities) {
        Map<Integer, Double> weightedSums = new HashMap<>();
        Map<Integer, Double> similaritySums = new HashMap<>();
        
        Map<Integer, Integer> userRatings = userData.userRatings.get(userId);
        double userAvgRating = userData.userAverages.get(userId);

        // Calculate weighted sums of deviations
        for (Map.Entry<Integer, Double> entry : userSimilarities.entrySet()) {
            int otherUserId = entry.getKey();
            double similarity = entry.getValue();
            Map<Integer, Integer> otherUserRatings = userData.userRatings.get(otherUserId);
            double otherUserAvgRating = userData.userAverages.get(otherUserId);

            for (Map.Entry<Integer, Integer> movieRating : otherUserRatings.entrySet()) {
                int movieId = movieRating.getKey();
                if (!userRatings.containsKey(movieId)) {
                    double ratingDeviation = movieRating.getValue() - otherUserAvgRating;
                    double weightedDeviation = similarity * ratingDeviation;
                    
                    weightedSums.merge(movieId, weightedDeviation, Double::sum);
                    similaritySums.merge(movieId, Math.abs(similarity), Double::sum);
                }
            }
        }

        // Calculate final predicted ratings
        Map<Integer, Double> predictedRatings = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : weightedSums.entrySet()) {
            int movieId = entry.getKey();
            double weightedSum = entry.getValue();
            double similaritySum = similaritySums.get(movieId);

            if (similaritySum > 0) {
                double predictedDeviation = weightedSum / similaritySum;
                double predictedRating = userAvgRating + predictedDeviation;
                
                // Clamp the predicted rating to valid range
                predictedRating = Math.min(MAX_RATING, Math.max(MIN_RATING, predictedRating));
                predictedRatings.put(movieId, predictedRating);
            }
        }

        return predictedRatings;
    }

    private MovieRecommendation getMovieDetails(int movieId, double predictedRating) {
        String query = "SELECT " +
                "    m.MovieID, " +
                "    m.Title, " +
                "    m.ReleaseYear, " +
                "    m.Description, " +
                "    m.PosterURL, " +
                "    GROUP_CONCAT(DISTINCT d.DirectorName SEPARATOR ', ') AS Directors, " +
                "    GROUP_CONCAT(DISTINCT g.GenreName SEPARATOR ', ') AS Genres, " +
                "    AVG(r.Rating) AS AverageRating " +
                "FROM movies m " +
                "LEFT JOIN moviedirectors md ON m.MovieID = md.MovieID " +
                "LEFT JOIN directors d ON md.DirectorID = d.DirectorID " +
                "LEFT JOIN moviegenres mg ON m.MovieID = mg.MovieID " +
                "LEFT JOIN genres g ON mg.GenreID = g.GenreID " +
                "LEFT JOIN reviews r ON m.MovieID = r.MovieID " +
                "WHERE m.MovieID = ? " +
                "GROUP BY m.MovieID";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MovieRecommendation(
                            rs.getInt("MovieID"),
                            rs.getString("Title"),
                            rs.getInt("ReleaseYear"),
                            rs.getString("Genres"),
                            rs.getString("Directors"),
                            rs.getString("Description"),
                            rs.getString("PosterURL"),
                            rs.getDouble("AverageRating"),
                            predictedRating
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MovieRecommendation(movieId, "Unknown Title", 0, "Unknown", "Unknown",
                "No description available", "", 0.0, predictedRating);
    }

    public static class MovieRecommendation {
        private final int movieId;
        private final String title;
        private final int releaseYear;
        private final String genres;
        private final String directors;
        private final String description;
        private final String posterUrl;
        private final double averageRating;
        private final double predictedRating;

        public MovieRecommendation(int movieId, String title, int releaseYear, String genres,
                String directors, String description, String posterUrl,
                double averageRating, double predictedRating) {
            this.movieId = movieId;
            this.title = title;
            this.releaseYear = releaseYear;
            this.genres = genres;
            this.directors = directors;
            this.description = description;
            this.posterUrl = posterUrl;
            this.averageRating = averageRating;
            this.predictedRating = predictedRating;
        }

        // Getters
        public int getMovieId() { return movieId; }
        public String getTitle() { return title; }
        public int getReleaseYear() { return releaseYear; }
        public String getGenres() { return genres; }
        public String getDirectors() { return directors; }
        public String getDescription() { return description; }
        public String getPosterUrl() { return posterUrl; }
        public double getAverageRating() { return averageRating; }
        public double getPredictedRating() { return predictedRating; }
    }
}