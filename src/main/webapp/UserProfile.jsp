<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="java.util.List"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${user.username}'sProfile</title>
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

.profile-header {
	background-color: white;
	border-radius: 12px;
	padding: 2rem;
	margin: 2rem 0;
	box-shadow: 0 4px 6px rgba(50, 45, 41, 0.1);
	display: flex;
	align-items: flex-start;
	gap: 2rem;
	transition: transform 0.2s ease;
}

.profile-header:hover {
	transform: translateY(-2px);
}

.profile-pic {
	width: 120px;
	height: 120px;
	border-radius: 50%;
	object-fit: cover;
	border: 4px solid var(--light-beige);
	box-shadow: 0 2px 8px rgba(50, 45, 41, 0.1);
	transition: transform 0.3s ease;
}

.profile-pic:hover {
	transform: scale(1.05);
}

.profile-info {
	flex: 1;
}

.profile-name {
	font-size: 2rem;
	font-weight: 700;
	margin: 0 0 0.5rem;
	color: var(--dark-brown);
}

.profile-bio {
	color: var(--dark-brown);
	margin: 0.5rem 0 1.5rem;
	font-size: 1.1rem;
	line-height: 1.6;
}

.edit-input {
	display: none;
	width: 100%;
	padding: 0.75rem;
	border: 2px solid var(--light-gray);
	border-radius: 8px;
	font-size: 1rem;
	transition: border-color 0.2s ease;
	margin: 0.5rem 0;
}

.edit-input:focus {
	border-color: var(--dark-brown);
	box-shadow: 0 0 0 3px rgba(50, 45, 41, 0.1);
	outline: none;
}

.btn {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	padding: 0.75rem 1.5rem;
	border-radius: 8px;
	font-weight: 600;
	font-size: 0.95rem;
	cursor: pointer;
	transition: all 0.2s ease;
	border: none;
	text-transform: uppercase;
	letter-spacing: 0.5px;
}

.edit-btn {
	background-color: var(--dark-brown);
	color: var(--off-white);
	margin-right: 1rem;
}

.edit-btn:hover {
	background-color: var(--deep-red);
	transform: translateY(-2px);
	box-shadow: 0 4px 6px rgba(50, 45, 41, 0.1);
}

.delete-btn {
	background-color: var(--deep-red);
	color: var(--off-white);
}

.delete-btn:hover {
	background-color: #8B4348;
	transform: translateY(-2px);
	box-shadow: 0 4px 6px rgba(50, 45, 41, 0.1);
}

.recent-reviews {
	margin: 2rem 0;
}

.recent-reviews h2 {
	font-size: 1.5rem;
	font-weight: 700;
	color: var(--dark-brown);
	margin-bottom: 1.5rem;
	padding-bottom: 0.5rem;
	border-bottom: 2px solid var(--light-beige);
}

.review {
	background-color: white;
	border-radius: 12px;
	padding: 1.5rem;
	margin-bottom: 1.5rem;
	box-shadow: 0 2px 4px rgba(50, 45, 41, 0.05);
	transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.review:hover {
	transform: translateY(-2px);
	box-shadow: 0 4px 6px rgba(50, 45, 41, 0.1);
}

.review-header {
	display: flex;
	align-items: flex-start;
	gap: 1.5rem;
	margin-bottom: 1rem;
}

.movie-poster {
	width: 80px;
	height: 120px;
	border-radius: 8px;
	object-fit: cover;
	box-shadow: 0 2px 4px rgba(50, 45, 41, 0.1);
}

.movie-info {
	flex: 1;
}

.movie-title {
	font-size: 1.25rem;
	font-weight: 600;
	color: var(--dark-brown);
	margin: 0 0 0.5rem;
}

.review-rating {
	color: var(--deep-red);
	font-size: 1.2rem;
	margin-right: 1rem;
}

.star {
	margin-right: 2px;
}

.star.filled {
	color: var(--deep-red);
}


.filled {
    color: var(--deep-red); /* Color for filled stars */
}

.empty {
    color: lightgray; /* Color for empty stars */
}


.review-date {
	font-size: 0.9rem;
	color: var(--dark-brown);
}

.review-text {
	color: var(--dark-brown);
	line-height: 1.6;
	margin: 1rem 0;
}

.button-group {
	display: flex;
	gap: 0.75rem; /* Space between buttons */
	margin-left: auto; /* Push buttons to the right */
}

a {
	color: var(--dark-brown);
	text-decoration: none;
	transition: color 0.2s ease;
}

a:hover {
	color: var(--deep-red);
}

@media ( max-width : 768px) {
	.profile-header {
		flex-direction: column;
		align-items: center;
		text-align: center;
		padding: 1.5rem;
	}
	.profile-pic {
		margin: 0 0 1rem;
	}
	.review-header {
		display: flex;
		align-items: flex-start;
		justify-content: space-between; /* Distribute space between items */
		position: relative;
		/* Ensure positioning context for absolute elements */
	}
	.movie-poster {
		margin-bottom: 1rem;
	}
	.button-group {
		flex-direction: column;
		width: 100%;
	}
	.btn {
		width: 100%;
		margin: 0.25rem 0;
	}
}

@media ( max-width : 480px) {
	.container {
		padding: 0 0.75rem;
	}
	.profile-header {
		padding: 1rem;
		margin: 1rem 0;
	}
	.profile-name {
		font-size: 1.5rem;
	}
	.review {
		padding: 1rem;
	}
	.movie-title {
		font-size: 1.1rem;
	}
}

.error-container {
	max-width: 600px;
	margin: 100px auto;
	text-align: center;
	padding: 2rem;
	background-color: white;
	border-radius: 12px;
	box-shadow: 0 4px 6px rgba(50, 45, 41, 0.1);
}

.error-title {
	color: var(--deep-red);
	font-size: 1.5rem;
	margin-bottom: 1rem;
}

.error-message {
	color: var(--dark-brown);
	margin-bottom: 2rem;
}

.home-button {
	background-color: var(--dark-brown);
	color: var(--off-white);
	padding: 0.75rem 1.5rem;
	border-radius: 8px;
	text-decoration: none;
	display: inline-block;
	transition: all 0.2s ease;
}

.home-button:hover {
	background-color: var(--deep-red);
	color: var(--off-white);
	transform: translateY(-2px);
	box-shadow: 0 4px 6px rgba(50, 45, 41, 0.1);
}
</style>
</head>
<body>
	<jsp:include page='Header.jsp' />

	<c:choose>
		<c:when test="${empty user}">
			<div class="error-container">
				<h1 class="error-title">User Not Found</h1>
				<p class="error-message">Sorry, we couldn't find the user you're
					looking for. They may have deleted their account or the URL might
					be incorrect.</p>
				<a href="${pageContext.request.contextPath}/home"
					class="home-button">Return to Home</a>
			</div>
		</c:when>
		<c:otherwise>
			<div class="container mt-4">
				<div class="profile-header">
					<img class="profile-pic"
						src="${userProfile.avatarUrl != null ? userProfile.avatarUrl : 'https://loremflickr.com/200/200?random=1'}"
						alt="${user.username}'s Profile Picture"
						onerror="this.src='https://loremflickr.com/200/200?random=1'" />
					<div class="profile-info">
						<h1 class="profile-name">
							<span id="usernameDisplay">${user.username}</span> <input
								type="text" id="usernameInput" class="edit-input" />
						</h1>
						<p class="profile-bio">
							<span id="bioDisplay"><c:out
									value="${user.bio != null ? user.bio : 'No bio available.'}" /></span>
							<textarea id="bioInput" class="edit-input"></textarea>
						</p>
						<!-- Conditionally render the edit button -->
						<c:if test="${isOwner}">
							<button class="btn edit-btn" id="editProfileBtn"
								onclick="toggleEditProfile()">Edit Profile</button>
						</c:if>
					</div>
				</div>

				<div class="recent-reviews">
					<h2>RECENT REVIEWS</h2>
					<c:forEach var="review" items="${listReview}">
						<div class="review" id="review-${review.reviewID}">
							<div class="review-header">
								<img class="movie-poster"
									src="<c:out value='${review.posterURL}' />" alt="Movie Poster"
									onerror="this.src='default-poster.jpg'" />
								<div class="movie-info">
									<form action="reviewMovie" method="POST"
										style="display: inline;">
										<input type="hidden" name="movieID" value="${review.movieID}" />
										<a href="reviewMovie?movieID=${review.movieID}"
											class="movie-title-link"> <strong><c:out
													value="${review.title} (${review.releaseYear})" /></strong>
										</a>
									</form>
									<div>
										<span class="review-rating"> <c:forEach begin="1"
												end="5" var="i">
												<span
													class="star ${i <= review.rating ? 'filled' : 'empty'}">
													★ </span>
											</c:forEach>
										</span> <span class="review-date">Watched <fmt:formatDate
												value="${review.reviewDate}" pattern="d MMM yyyy" /></span>
									</div>
								</div>
								<!-- Button container for alignment -->
								<c:if test="${isOwner}">
									<div class="button-group">
										<a href="javascript:void(0);" class="btn edit-btn"
											id="editReviewBtn-${review.reviewID}"
											onclick="toggleReview(${review.reviewID})">Edit Review</a> <a
											href="javascript:void(0);" class="btn delete-btn"
											onclick="deleteReview(${review.reviewID})">Delete Review</a>
									</div>
								</c:if>
							</div>
							<p class="review-text" id="reviewText-${review.reviewID}">
								<c:out value="${review.reviewText}" />
							</p>
							<textarea id="reviewTextInput-${review.reviewID}"
								class="edit-input" rows="3"></textarea>
						</div>


					</c:forEach>
				</div>
		</c:otherwise>
	</c:choose>

	<script>
        function toggleEditProfile() {
            const usernameDisplay = document.getElementById('usernameDisplay');
            const bioDisplay = document.getElementById('bioDisplay');
            const usernameInput = document.getElementById('usernameInput');
            const bioInput = document.getElementById('bioInput');
            const editProfileBtn = document.getElementById('editProfileBtn');

            if (usernameInput.style.display === "none" || usernameInput.style.display === "") {
                usernameInput.value = usernameDisplay.innerText;
                bioInput.value = bioDisplay.innerText;
                usernameDisplay.style.display = 'none';
                bioDisplay.style.display = 'none';
                usernameInput.style.display = 'inline';
                bioInput.style.display = 'block';
                editProfileBtn.innerText = "Save Profile";
                usernameInput.focus();
            } else {
                saveUserInfo(usernameInput.value, bioInput.value);
                usernameDisplay.innerText = usernameInput.value;
                bioDisplay.innerText = bioInput.value;
                usernameInput.style.display = 'none';
                bioInput.style.display = 'none';
                usernameDisplay.style.display = 'inline';
                bioDisplay.style.display = 'inline';
                editProfileBtn.innerText = "Edit Profile";
            }
        }

        function saveUserInfo(username, bio) {
            const userID = "${user.userID}";

            const body = new URLSearchParams();
            body.append('action', 'updateUserInfo');
            body.append('userID', userID);
            body.append('username', username);
            body.append('bio', bio);

            fetch('/rottenapple/updateOrEdit', {
                method: 'POST',
                body: body
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Failed to update user info.');
            });
        }

        function toggleReview(reviewID) {
            const reviewTextElem = document.getElementById('reviewText-' + reviewID);
            const reviewTextInput = document.getElementById('reviewTextInput-' + reviewID);
            const editBtn = document.getElementById('editReviewBtn-' + reviewID);

            if (reviewTextInput.style.display === "none" || reviewTextInput.style.display === "") {
                reviewTextInput.value = reviewTextElem.innerText;
                reviewTextElem.style.display = 'none';
                reviewTextInput.style.display = 'block';
                editBtn.innerText = "Save Review";
                reviewTextInput.focus();
            } else {
                const newText = reviewTextInput.value;
                const body = new URLSearchParams();
                body.append('action', 'editReview');
                body.append('reviewID', reviewID);
                body.append('reviewText', newText);

                fetch('/rottenapple/updateOrEdit', {
                    method: 'POST',
                    body: body
                })
                .then(response => response.json())
                .then(data => {
                    alert(data.message); // Directly alert the message from the response
                    reviewTextElem.innerText = newText;
                    reviewTextElem.style.display = 'block';
                    reviewTextInput.style.display = 'none';
                    editBtn.innerText = "Edit Review";
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to update review.');
                });
            }
        }



        function deleteReview(reviewID) {
            fetch('/rottenapple/deleteReview', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'reviewID=' + encodeURIComponent(reviewID)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text); });
                }
                return response.json();
            })
            .then(data => {
                if (data.message) {
                    document.getElementById('review-' + reviewID).remove();
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred: ' + error.message);
            });
        }
    </script>
</body>
</html>
