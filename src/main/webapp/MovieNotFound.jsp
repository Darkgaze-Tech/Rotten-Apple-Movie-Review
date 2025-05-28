<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error Page</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.3.0/font/bootstrap-icons.css">
<style>
:root {
	--dark-brown: #322D29;
	--deep-red: #72383D;
	--light-beige: #D1C7BD;
	--off-white: #EFE9E1;
	--light-gray: #D9D9D9;
}

body {
	background-color: var(--off-white);
	font-family: 'Arial', sans-serif;
	min-height: 100vh;
	display: flex;
	flex-direction: column;
}

.error-container {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 2rem;
}

.error-card {
	background-color: white;
	border-radius: 8px;
	box-shadow: 0 4px 12px rgba(50, 45, 41, 0.15);
	padding: 3rem;
	max-width: 600px;
	width: 100%;
	text-align: center;
}

.error-icon {
	font-size: 4rem;
	color: var(--deep-red);
	margin-bottom: 1.5rem;
}

.error-title {
	color: var(--dark-brown);
	font-size: 2rem;
	margin-bottom: 1rem;
}

.error-message {
	color: var(--dark-brown);
	margin-bottom: 2rem;
}

.error-code {
	background-color: var(--light-beige);
	color: var(--dark-brown);
	padding: 0.5rem 1rem;
	border-radius: 4px;
	display: inline-block;
	margin-bottom: 2rem;
	font-family: monospace;
}

.btn-primary {
	background-color: var(--dark-brown);
	border: none;
	color: var(--off-white);
	padding: 0.6rem 1.2rem;
	font-weight: 500;
	letter-spacing: 0.3px;
	transition: all 0.2s ease;
	text-transform: uppercase;
	font-size: 0.875rem;
}

.btn-primary:hover {
	background-color: var(--deep-red);
	transform: translateY(-1px);
}

@media ( max-width : 768px) {
	.error-card {
		padding: 2rem;
		margin: 1rem;
	}
	.error-icon {
		font-size: 3rem;
	}
	.error-title {
		font-size: 1.5rem;
	}
}
</style>
</head>
<body>
	<jsp:include page='Header.jsp' />

	<div class="error-container">
		<div class="error-card">
			<div class="error-icon">🎬</div>
			<h1 class="error-title">Movie Not Found</h1>
			
			<p class="error-message">We couldn't find the movie you're looking for. It may have been removed or the ID might be incorrect.</p>

			<div class="d-grid gap-2 d-md-block">
				<a href="MovieListServlet" class="btn btn-primary"> <i
					class="bi bi-film"></i> Browse Movies
				</a>
			</div>

		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>