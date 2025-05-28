<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Movie Search Results</title>
<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<style>
:root {
	--dark-brown: #322D29;
	--deep-red: #72383D;
	--light-beige: #D1C7BD;
	--off-white: #EFE9E1;
	--light-gray: #D9D9D9;
	--shadow-sm: 0 2px 4px rgba(50, 45, 41, 0.05);
	--shadow-md: 0 4px 12px rgba(50, 45, 41, 0.1);
	--shadow-lg: 0 2px 8px rgba(50, 45, 41, 0.1);
}

body {
	font-family: 'Arial', sans-serif;
	margin: 0;
	padding: 0;
	background-color: var(--off-white);
	color: var(--dark-brown);
	line-height: 1.6;
}

.container {
	max-width: 1200px;
	margin: 0 auto;
	padding: 0 1rem;
}

h1 {
	color: var(--dark-brown);
	font-size: 2.5rem;
	font-weight: 700;
	margin: 2rem 0;
	padding-bottom: 0.5rem;
	border-bottom: 2px solid var(--light-beige);
}

.movie-card {
	background-color: white;
	margin-bottom: 2rem;
	border: none;
	border-radius: 12px;
	overflow: hidden;
	cursor: pointer;
	box-shadow: var(--shadow-sm);
	transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.movie-card:hover {
	transform: translateY(-2px);
	box-shadow: var(--shadow-md);
}

.movie-poster {
	width: 200px;
	height: 300px;
	object-fit: cover;
	float: left;
	margin-right: 1.5rem;
	border-radius: 8px;
	box-shadow: var(--shadow-lg);
}

.movie-info {
	padding: 1.5rem;
	overflow: hidden;
}

.movie-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 1rem;
}

.movie-title {
	font-size: 1.5rem;
	font-weight: 700;
	color: var(--dark-brown);
	margin-right: 1rem;
	line-height: 1.3;
}

.rating-stars {
    font-size: 1.3rem;
    margin-bottom: 1rem;
}

.star {
    margin-right: 3px;
    color: var(--light-gray);
}

.star.filled {
    color: var(--deep-red);
}

.movie-meta {
	color: var(--dark-brown);
	font-size: 1rem;
	margin-bottom: 0.75rem;
	line-height: 1.6;
}

.movie-meta strong {
	color: var(--deep-red);
	font-weight: 600;
}

.description {
	margin-top: 1rem;
	font-size: 1rem;
	color: var(--dark-brown);
	line-height: 1.7;
}

.alert {
	background-color: var(--light-beige);
	border: none;
	color: var(--dark-brown);
	padding: 1.5rem;
	border-radius: 8px;
	font-size: 1.1rem;
	text-align: center;
	margin: 2rem 0;
}

.alert-warning {
	border-left: 4px solid var(--deep-red);
}

@media ( max-width : 768px) {
	.movie-card {
		display: flex;
		flex-direction: column;
	}
	.movie-poster {
		width: 100%;
		height: 400px;
		float: none;
		margin-right: 0;
		border-radius: 12px 12px 0 0;
	}
	.movie-info {
		padding: 1.25rem;
	}
	.movie-header {
		flex-direction: column;
	}
	.movie-title {
		margin-bottom: 0.75rem;
	}
}

@media ( max-width : 480px) {
	h1 {
		font-size: 2rem;
		margin: 1.5rem 0;
	}
	.movie-poster {
		height: 300px;
	}
	.movie-title {
		font-size: 1.25rem;
	}
	.movie-info {
		padding: 1rem;
	}
	.description {
		font-size: 0.95rem;
	}
}
</style>
</head>
<body>
	<jsp:include page='Header.jsp' />
	<div class="container mt-4">
		<h1>
			Search Results for "
			<c:out value="${query}" />
			"
		</h1>

		<c:if test="${empty results}">
			<div class="alert alert-warning" role="alert">No movies found
				matching your search.</div>
		</c:if>

		<div class="movie-results">
			<c:forEach var="movie" items="${results}">
				<form action="reviewMovie" method="POST" class="movie-card">
					<input type="hidden" name="movieID" value="${movie.movieID}">
					<div onclick="this.closest('form').submit();">
						<img src="${movie.posterUrl}" class="movie-poster"
							alt="${movie.title} Poster">
						<div class="movie-info">
							<div class="movie-title">${movie.title}
								(${movie.releaseYear})</div>
							<div class="rating-stars">
								<c:forEach begin="1" end="5" var="i">
									<span class="star ${i <= movie.averageRating ? 'filled' : ''}">★</span>
								</c:forEach>
								<span class="ml-2"> <fmt:formatNumber
										value="${movie.averageRating}" pattern="#0.0" />
								</span>
							</div>
							<div class="movie-meta">
								<strong>Genre:</strong> ${movie.genres}
							</div>
							<div class="movie-meta">
								<strong>Director:</strong> ${movie.directors}
							</div>
							<div class="description">${movie.description}</div>
						</div>
					</div>
				</form>
			</c:forEach>
		</div>
	</div>

	<!-- Bootstrap JS and dependencies -->
	<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
