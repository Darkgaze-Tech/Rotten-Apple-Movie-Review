<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Movie Details Page</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.3.0/font/bootstrap-icons.css">
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

/* Movie Header Section */
.movie-header {
	padding: 3rem 0;
	margin-bottom: 2rem;
}

.movie-poster {
	max-width: 300px;
	border-radius: 8px;
	box-shadow: 0 4px 12px rgba(50, 45, 41, 0.15);
}

.movie-info {
	background-color: white;
	padding: 2rem;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(50, 45, 41, 0.1);
}

/* Rating Styles */
.rating-display {
	color: var(--deep-red);
	font-size: 24px;
}

.star-rating {
	color: var(--light-gray);
}

.star-rating.filled {
	color: var(--deep-red);
}

.rating {
	display: flex;
	flex-direction: row-reverse;
	justify-content: center;
}

.rating input {
	display: none;
}

.rating label {
	cursor: pointer;
	font-size: 30px;
	color: var(--light-gray);
	padding: 5px;
}

.rating input:checked ~ label, .rating label:hover, .rating label:hover 
	 ~ label {
	color: var(--deep-red);
}

/* Review Section */
.review-card {
	background-color: white;
	padding: 1.5rem;
	margin-bottom: 1rem;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(50, 45, 41, 0.1);
	transition: transform 0.2s ease;
}

.review-card:hover {
	transform: translateY(-2px);
}

.login-prompt {
	text-align: center;
	padding: 2rem;
	background-color: var(--light-beige);
	border-radius: 8px;
	margin: 2rem 0;
}

/* Button Styles */
.btn {
	border-radius: 4px;
	padding: 0.6rem 1.2rem;
	font-weight: 500;
	letter-spacing: 0.3px;
	transition: all 0.2s ease;
	text-transform: uppercase;
	font-size: 0.875rem;
}

.btn-primary {
	background-color: var(--dark-brown);
	border: none;
	color: var(--off-white);
}

.btn-primary:hover {
	background-color: var(--deep-red);
	transform: translateY(-1px);
}

.btn-outline-primary {
	background-color: transparent;
	border: 2px solid var(--dark-brown);
	color: var(--dark-brown);
}

.btn-outline-primary:hover {
	background-color: var(--dark-brown);
	color: var(--off-white);
	transform: translateY(-1px);
}

.btn-success {
	background-color: var(--dark-brown);
	border: none;
	color: var(--off-white);
}

.btn-success:hover {
	background-color: var(--dark-brown); /* or any other color you prefer */
	transform: translateY(-1px);
}

.btn-danger {
	background-color: var(--deep-red);
	border: none;
	color: var(--off-white);
}

.btn-success:disabled, .btn-success.disabled {
	background-color: var(--light-gray);
	color: var(--dark-brown);
	opacity: 0.7;
	border: none;
	transform: none;
}

.btn-success:disabled:hover, .btn-success.disabled:hover {
	background-color: var(--light-gray);
	transform: none;
}

/* Alert Styles */
.alert {
	border: none;
	border-radius: 8px;
	padding: 1rem;
	margin-bottom: 1rem;
	background-color: var(--light-beige);
	color: var(--dark-brown);
}

.alert-success {
	background-color: var(--light-beige);
	color: var(--dark-brown);
}

.alert-primary {
	background-color: var(--light-beige);
	color: var(--dark-brown);
}

.alert-danger {
	background-color: var(--deep-red);
	color: var(--off-white);
}

/* Form Elements */
.form-control {
	border: 2px solid var(--light-gray);
	border-radius: 4px;
	padding: 0.75rem;
	transition: border-color 0.2s ease;
}

.form-control:focus {
	border-color: var(--dark-brown);
	box-shadow: 0 0 0 2px rgba(50, 45, 41, 0.1);
	outline: none;
}

/* Links */
a {
	color: var(--deep-red);
	text-decoration: none;
	transition: color 0.2s ease;
}

a:hover {
	color: var(--dark-brown);
}

/* Responsive Adjustments */
@media ( max-width : 768px) {
	.movie-header {
		padding: 2rem 0;
	}
	.movie-poster {
		max-width: 250px;
		margin-bottom: 1.5rem;
	}
	.rating label {
		font-size: 24px;
	}
	.btn {
		padding: 0.5rem 1rem;
		font-size: 0.8rem;
	}
}

.highlight {
	background-color: #f8f5f2 !important; /* Lighter version of off-white */
	border-left: 4px solid var(--deep-red) !important;
	box-shadow: 0 2px 12px rgba(114, 56, 61, 0.15) !important;
}
</style>
</head>
<body>
	<jsp:include page='Header.jsp' />

	<!-- Updated Alert Messages Section -->

	<c:if test="${addStatus=='1'}">
		<div
			class="alert alert-success alert-dismissible fade show text-center"
			role="alert">
			<i class="bi bi-check-circle-fill me-2"></i> <strong>Thank
				you for your review!</strong>
			<button type="button" class="btn-close" data-bs-dismiss="alert"
				aria-label="Close"></button>
		</div>
	</c:if>

	<c:if test="${updateStatus=='1'}">
		<div
			class="alert alert-primary alert-dismissible fade show text-center"
			role="alert">
			<i class="bi bi-check-circle-fill me-2"></i> <strong>Movie
				review updated!</strong>
			<button type="button" class="btn-close" data-bs-dismiss="alert"
				aria-label="Close"></button>
		</div>
	</c:if>

	<c:if test="${deleteStatus=='1'}">
		<div
			class="alert alert-danger alert-dismissible fade show text-center"
			role="alert">
			<i class="bi bi-check-circle-fill me-2"></i> <strong>Movie
				review deleted!</strong>
			<button type="button" class="btn-close" data-bs-dismiss="alert"
				aria-label="Close"></button>
		</div>
	</c:if>

	<!-- Movie Header -->
	<div class="movie-header">
		<div class="container">
			<div class="row align-items-center">
				<div class="col-md-4 text-center">
					<img src="${posterUrl}" alt="${title}"
						class="movie-poster img-fluid mb-3">
				</div>
				<div class="col-md-8">
					<h1 class="mb-4">${title}</h1>
					<div class="rating-display mb-3">
						<c:set var="formattedAverageRating">
							<fmt:formatNumber value="${averageRating}" maxFractionDigits="1"
								minFractionDigits="1" />
						</c:set>

						<c:forEach begin="1" end="5" var="i">
							<span
								class="star-rating ${i <= Math.round(averageRating) ? 'filled' : ''}">★</span>
						</c:forEach>
						<span class="ms-2 text-muted">${formattedAverageRating != null ? formattedAverageRating : 'N/A'}</span>
					</div>
					<div class="movie-info">
						<p>
							<strong>Director:</strong> ${directors}
						</p>
						<p>
							<strong>Release Year:</strong> ${releaseYear}
						</p>
						<p>
							<strong>Genres:</strong> ${genres}
						</p>
						<p>
							<strong>Plot:</strong> ${description}
						</p>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Reviews Section -->
	<div class="container my-5">
		<h2 class="mb-4">Reviews</h2>

		<!-- Login Prompt for Guest Users -->
		<c:if test="${!isLoggedIn}">
			<div class="login-prompt">
				<h5>Want to share your thoughts?</h5>
				<p>Log in to write a review!</p>
				<a href="Login.jsp" class="btn btn-primary">Login</a> <a
					href="Register.jsp" class="btn btn-outline-primary ms-2">Register</a>
			</div>
		</c:if>

		<!-- Review Form for Logged-in Users -->
		<c:if test="${isLoggedIn}">
			<div class="review-card mb-4">
				<form id="review-form" action="reviewMovie" method="POST">
					<input type="hidden" name="movieID" value="${movieID}">

					<div class="mb-3">
						<label for="user-review" class="form-label">Write Your
							Review</label>
						<textarea class="form-control" id="user-review" name="user-review"
							rows="3" required><c:if test="${buttonStatus=='1'}">${fn:escapeXml(currentReviewText.trim())}</c:if></textarea>
					</div>


					<div class="mb-3">
						<label class="form-label">Rating</label>
						<div class="rating">
							<input type="radio" name="rating" value="5" id="5"
								<c:if test="${currentRating=='5'}">checked</c:if>> <label
								for="5">★</label> <input type="radio" name="rating" value="4"
								id="4" <c:if test="${currentRating=='4'}">checked</c:if>>
							<label for="4">★</label> <input type="radio" name="rating"
								value="3" id="3"
								<c:if test="${currentRating=='3'}">checked</c:if>> <label
								for="3">★</label> <input type="radio" name="rating" value="2"
								id="2" <c:if test="${currentRating=='2'}">checked</c:if>>
							<label for="2">★</label> <input type="radio" name="rating"
								value="1" id="1"
								<c:if test="${currentRating=='1'}">checked</c:if>> <label
								for="1">★</label>
						</div>
					</div>

					<div class="text-center">
						<c:choose>
							<c:when test="${buttonStatus=='1'}">
								<button type="submit" class="btn btn-primary me-2" name="act"
									value="Update Review">Edit Review</button>
								<button type="submit" class="btn btn-danger" name="act"
									value="Delete Review">Delete Review</button>
							</c:when>
							<c:otherwise>
								<button type="submit" id="submitReviewBtn"
									class="btn btn-success" name="act" value="Submit Review"
									disabled>Submit Review</button>
							</c:otherwise>
						</c:choose>
					</div>
				</form>
			</div>
		</c:if>

		<!-- Reviews List -->
		<c:choose>
			<c:when test="${not empty listReviews}">
				<c:forEach items="${listReviews}" var="reviewObj">
					<div
						class="review-card ${reviewObj.getUserID() == userID ? 'highlight' : ''}">
						<div
							class="d-flex justify-content-between align-items-center mb-2">
							<h5 class="mb-0">
								<a href="listReviewsByUserID?userID=${reviewObj.getUserID()}">${reviewObj.getUsername()}</a>
							</h5>
							<div>
								<c:forEach var="r" begin="1" end="5">
									<span
										class="star-rating ${r <= reviewObj.getRating() ? 'filled' : ''}">★</span>
								</c:forEach>
							</div>
						</div>
						<p class="mb-2">${reviewObj.getReviewText()}</p>
						<small class="text-muted">${reviewObj.getReviewDate()}</small>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="review-card text-center">
					<p>No reviews yet. Be the first to review this movie!</p>
				</div>
			</c:otherwise>
		</c:choose>

	</div>
</body>
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
        document.addEventListener('DOMContentLoaded', function() {
            const reviewTextarea = document.getElementById('user-review');
            const ratingInputs = document.querySelectorAll('input[name="rating"]');
            const submitButton = document.getElementById('submitReviewBtn');

            function checkFormValidity() {
                const reviewTextFilled = reviewTextarea.value.trim() !== '';
                const ratingSelected = Array.from(ratingInputs).some(input => input.checked);
                submitButton.disabled = !(reviewTextFilled && ratingSelected);
            }

            if (reviewTextarea && submitButton) {
                reviewTextarea.addEventListener('input', checkFormValidity);
                ratingInputs.forEach(input => input.addEventListener('change', checkFormValidity));
                reviewTextarea.focus(); // Set focus to the textarea
            }
        });
    </script>
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
        // Prevent double form submission
        function handleFormSubmit(form) {
            const submitButtons = form.querySelectorAll('button[type="submit"]');
            submitButtons.forEach(button => {
                button.disabled = true;
                button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';
            });
            return true;
        }

        document.addEventListener('DOMContentLoaded', function() {
            const reviewTextarea = document.getElementById('user-review');
            const ratingInputs = document.querySelectorAll('input[name="rating"]');
            const submitButton = document.getElementById('submitReviewBtn');

            function checkFormValidity() {
                const reviewTextFilled = reviewTextarea.value.trim() !== '';
                const ratingSelected = Array.from(ratingInputs).some(input => input.checked);
                if (submitButton) {
                    submitButton.disabled = !(reviewTextFilled && ratingSelected);
                }
            }

            if (reviewTextarea) {
                reviewTextarea.addEventListener('input', checkFormValidity);
                ratingInputs.forEach(input => input.addEventListener('change', checkFormValidity));
                reviewTextarea.focus();
            }

            // Auto-dismiss alerts after 5 seconds
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                setTimeout(() => {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }, 5000);
            });
        });
    </script>
</html>
