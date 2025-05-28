<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Movie List</title>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
<style>
/* Custom Properties */
:root {
	--dark-brown: #322D29;
	--deep-red: #72383D;
	--light-beige: #D1C7BD;
	--off-white: #EFE9E1;
	--light-gray: #D9D9D9;
}

/* Base Styles */
body {
	background-color: var(--off-white);
	font-family: 'Arial', sans-serif;
}

.container {
	max-width: 1200px;
	margin: auto;
	padding: 20px;
}

/* Header and Filters */
h1 {
	color: var(--dark-brown);
	margin-bottom: 1.5rem;
	font-weight: bold;
}

.filters {
	margin-bottom: 2rem;
	display: flex;
	gap: 1rem;
	align-items: center;
}

.form-control {
	border: 2px solid var(--light-gray);
	border-radius: 4px;
	transition: border-color 0.2s ease;
	min-width: 150px;
}

.form-control:focus {
	border-color: var(--dark-brown);
	box-shadow: 0 0 0 2px rgba(50, 45, 41, 0.1);
	outline: none;
}

/* Card Styles */
.card {
	height: 100%;
	display: flex;
	flex-direction: column;
	border: none;
	border-radius: 8px;
	background-color: white;
	box-shadow: 0 2px 8px rgba(50, 45, 41, 0.1);
	transition: transform 0.3s ease, box-shadow 0.3s ease;
	cursor: pointer;
}

.card:hover {
	transform: translateY(-5px);
	box-shadow: 0 8px 24px rgba(50, 45, 41, 0.2);
}

.card-img-top {
	height: 350px;
	object-fit: cover;
	border-top-left-radius: 8px;
	border-top-right-radius: 8px;
}

.card-body {
	flex: 1;
	display: flex;
	flex-direction: column;
	padding: 1.5rem;
}

.card-title {
	font-size: 1.25rem;
	font-weight: bold;
	color: var(--dark-brown);
	margin-bottom: 0.75rem;
}

/* Rating Styles */
.rating-container {
	display: flex;
	align-items: center;
	gap: 6px;
	margin-bottom: 0.75rem;
}

.rating-stars {
	color: var(--deep-red);
	font-size: 1.1rem;
}

.rating-stars .empty {
	color: var(--light-gray);
}

.rating-number {
	color: var(--dark-brown);
	font-weight: 500;
}

.genres-text {
	color: var(--deep-red);
	font-size: 0.95rem;
	margin-top: auto;
}

/* Loading and Error States */
#loadingSpinner {
	color: var(--deep-red);
	margin: 2rem auto;
	display: none;
}

#errorMessage {
	background-color: var(--deep-red);
	color: var(--off-white);
	padding: 1rem;
	border-radius: 8px;
	margin-bottom: 1rem;
	display: none;
}

/* Responsive Adjustments */
@media ( max-width : 768px) {
	.filters {
		flex-direction: column;
		align-items: stretch;
	}
	.form-control {
		width: 100%;
	}
	.card-img-top {
		height: 300px;
	}
	.card-title {
		font-size: 1.1rem;
	}
}
</style>
<script>
	function getStarRating(rating) {
		const fullStars = Math.floor(rating);
		let stars = '';

		// Add filled stars
		for (let i = 0; i < fullStars; i++) {
			stars += '<span>★</span>';
		}

		// Add empty stars
		for (let i = fullStars; i < 5; i++) {
			stars += '<span class="empty">★</span>';
		}

		return stars;
	}

	$(document).ready(function() {
		$('#genreSelect, #sortSelect').change(function() {
			loadMovies();
		});

		function loadMovies() {
			var genre = $('#genreSelect').val();
			var sort = $('#sortSelect').val();
			$('#loadingSpinner').show(); // Show loading spinner

			$.ajax({
				url: 'MovieListServlet',
				data: {
					genre: genre,
					sort: sort,
					ajax: 'true'
				},
				dataType: 'json',
				success: function(response) {
					$('#loadingSpinner').hide(); // Hide loading spinner
					var cardContainer = $('#movieCardContainer');
					cardContainer.empty();

					// Sorting logic
					if (sort === 'rating') {
						response.sort(function(a, b) {
							return b.rating - a.rating;
						});
					}

					$.each(response, function(i, movie) {
						var card = '<div class="col-md-3 mb-4">'
							+ '<form action="reviewMovie" method="POST" class="movie-card">'
							+ '<input type="hidden" name="movieID" value="' + movie.movieID + '">'
							+ '<div class="card">'
							+ '<img src="' + movie.posterUrl + '" class="card-img-top" alt="Poster of ' + movie.title + '">'
							+ '<div class="card-body">'
							+ '<h5 class="card-title">' + movie.title + ' (' + movie.releaseYear + ')</h5>'
							+ '<div class="rating-container">'
							+ '<span class="rating-stars">' + getStarRating(movie.rating) + '</span>'
							+ '<span class="rating-number">' + movie.rating.toFixed(1) + '</span>'
							+ '</div>'
							+ '<p class="genres-text">' + movie.genres.join(', ') + '</p>'
							+ '</div>'
							+ '</div>'
							+ '</form>'
							+ '</div>';
						cardContainer.append(card);
					});
				},
				error: function() {
					$('#loadingSpinner').hide(); // Hide loading spinner
					$('#errorMessage').text('Error loading movies. Please try again.').show();
				}
			});
		}

		// Event delegation for clicks on movie cards
		$('#movieCardContainer').on('click', '.movie-card', function(event) {
			event.preventDefault(); // Prevent default form submission
			$(this).submit();
		});
	});
</script>
</head>
<body>
	<jsp:include page='Header.jsp' />
	<div class="container mt-4">
		<h1 class="mb-4">Movie List</h1>
		
		<div class="filters mb-3">
			<select id="genreSelect" class="form-control d-inline-block w-auto mr-2" name="genre">
				<option value="">All Genres</option>
				<c:forEach items="${genreList}" var="genre">
					<option value="${genre}" ${genre eq selectedGenre ? 'selected' : ''}>${genre}</option>
				</c:forEach>
			</select>
			<select id="sortSelect" class="form-control d-inline-block w-auto" name="sort">
				<option value="">Sort By</option>
				<option value="title" ${selectedSort eq 'title' ? 'selected' : ''}>Title</option>
				<option value="year" ${selectedSort eq 'year' ? 'selected' : ''}>Year</option>
				<option value="rating" ${selectedSort eq 'rating' ? 'selected' : ''}>Rating</option>
			</select>
		</div>
		
		<div id="loadingSpinner" class="spinner-border" role="status">
			<span class="sr-only">Loading...</span>
		</div>
		<div id="errorMessage" class="alert alert-danger"></div>

		<div id="movieCardContainer" class="row">
			<c:forEach items="${movieList}" var="movie">
				<div class="col-md-3 mb-4">
					<form action="reviewMovie" method="POST" class="movie-card">
						<input type="hidden" name="movieID" value="${movie.movieID}">
						<div class="card">
							<img src="${movie.posterUrl}" class="card-img-top" alt="Poster of ${movie.title}">
							<div class="card-body">
								<h5 class="card-title">${movie.title} (${movie.releaseYear})</h5>
								<div class="rating-container">
									<span class="rating-stars">
										<c:forEach begin="1" end="5" var="i">
											<span ${i <= movie.rating ? '' : 'class="empty"'}>★</span>
										</c:forEach>
									</span>
									<span class="rating-number">${String.format("%.1f", movie.rating)}</span>
								</div>
								<p class="genres-text">
									<c:forEach items="${movie.genres}" var="genre" varStatus="status">
										${genre}<c:if test="${!status.last}">, </c:if>
									</c:forEach>
								</p>
							</div>
						</div>
					</form>
				</div>
			</c:forEach>
		</div>
	</div>
</body>
</html>
