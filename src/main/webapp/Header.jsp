<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
<style>
@import
	url('https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400;500;600;700&display=swap')
	;

* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
	font-family: 'Poppins', sans-serif;
}

body {
	background: #EFE9E1;
}

.navbar {
	background: #322D29;
	padding: 10px 20px;
	display: flex;
	justify-content: center; /* Center-aligns the navbar items */
	align-items: center;
	flex-wrap: wrap; /* Allows items to wrap on smaller screens */
} 

.navbar-menu {
	display: flex;
	list-style-type: none;
	margin: 0 20px; /* Adds margin around the menu */
}

.navbar-menu li {
	margin-left: 20px; /* Adjust as needed for spacing */
}

.navbar-brand {
	color: #D1C7BD;
	font-size: 24px;
	font-weight: bold;
	text-decoration: none;
	margin-right: auto; /* Allows brand to stay at the left */
}

.navbar-menu li a {
	color: #D9D9D9; /* Updated to new light gray color */
	text-decoration: none;
	padding: 0.5rem 1rem;
	border-radius: 4px;
	transition: all 0.3s ease;
}

.navbar-menu li a:hover {
	background-color: #72383D;
	color: #EFE9E1;
}

.search-input {
	background: #fff;
	border-radius: 5px;
	position: relative;
	width: 300px;
}

.search-input input {
	height: 40px;
	width: 100%;
	outline: none;
	border: none;
	border-radius: 5px;
	padding: 0 60px 0 20px;
	font-size: 16px;
}

.search-input.active input {
	border-radius: 5px 5px 0 0;
}

.search-input .autocom-box {
	padding: 0;
	opacity: 0;
	pointer-events: none;
	max-height: 280px;
	overflow-y: auto;
	position: absolute; /* Ensure it is positioned absolutely */
	width: 100%;
	background: #fff;
	border-radius: 0 0 5px 5px;
	transition: opacity 0.2s;
	z-index: 1000; /* Set a high z-index */
}

.search-input.active .autocom-box {
	padding: 10px 8px;
	opacity: 1;
	pointer-events: auto;
}

.autocom-box li {
	list-style: none;
	padding: 8px 12px;
	display: none;
	width: 100%;
	cursor: default;
	border-radius: 3px;
}

.search-input.active .autocom-box li {
	display: block;
}

.autocom-box li:hover {
	background: #EFE9E1; /* Updated to match body background color */
}

.search-input .icon {
	position: absolute;
	right: 0px;
	top: 0px;
	height: 40px;
	width: 40px;
	text-align: center;
	line-height: 40px;
	font-size: 20px;
	color: #72383D; /* Updated to new deep red color */
	cursor: pointer;
}
</style>
</head>
<body>
	<header class="navbar" role="navigation">
		<span class="navbar-brand">RottenApple</span>
		<nav>
			<ul class="navbar-menu">
				<li><a href="MovieListServlet" aria-label="View movies">Movies</a></li>
				<c:choose>
					<c:when test="${sessionScope.userID == null}">
						<li><a href="Login.jsp" aria-label="Login to your account">Login</a></li>
						<li><a href="Register.jsp" aria-label="Create a new account">Register</a></li>
					</c:when>
					<c:otherwise>
						<li><a
							href="recommend?UserID=<%=session.getAttribute("userID")%>"
							aria-label="Get movie recommendations">Recommendation</a></li>
						<li><a
							href="listReviewsByUserID?userID=<%=session.getAttribute("userID")%>"
							aria-label="View your profile">Profile</a></li>
						<li><a href="LogoutServlet"
							aria-label="Logout from your account">Logout</a></li>
					</c:otherwise>
				</c:choose>
			</ul>
		</nav>
		<div class="search-input">
			<input type="text" placeholder="Search for movies..."
				aria-label="Search for movies" oninput="showSuggestions(this.value)">
			<div class="autocom-box">
				<!-- Suggestions will be inserted here by JavaScript -->
			</div>
			<div class="icon">
				<i class="fas fa-search"></i>
			</div>
		</div>
	</header>


	<script>
        const searchWrapper = document.querySelector(".search-input");
        const inputBox = searchWrapper.querySelector("input");
        const suggBox = searchWrapper.querySelector(".autocom-box");
        const icon = searchWrapper.querySelector(".icon");

        inputBox.onkeyup = async (e) => {
            let userData = e.target.value;
            if (userData) {
                try {
                    // Fetching data from the servlet
                    const response = await fetch('searchMovies?query=' + encodeURIComponent(userData));
                    const data = await response.json();

                    if (data.movies && data.movies.length > 0) {
                        showSuggestions(data.movies);
                        searchWrapper.classList.add("active");
                    } else {
                        searchWrapper.classList.remove("active");
                    }
                } catch (error) {
                    console.error("Error fetching data:", error);
                    suggBox.innerHTML = `<li>Error fetching suggestions</li>`;
                    searchWrapper.classList.remove("active");
                }
            } else {
                searchWrapper.classList.remove("active");
            }
        };

        icon.onclick = () => {
            performSearch();
        };

        inputBox.onkeypress = (e) => {
            if (e.key === 'Enter') {
                performSearch();
            }
        };

        function performSearch() {
            let searchQuery = inputBox.value;
            if (searchQuery) {
                window.location.href = 'search?query=' + encodeURIComponent(searchQuery);
            }
        }

        function select(element) {
            let selectData = element.textContent;
            inputBox.value = selectData; // Display the selected title in the input
            searchWrapper.classList.remove("active");
            performSearch();
        }

        function showSuggestions(movies) {
            suggBox.innerHTML = ''; // Clear previous suggestions
            if (movies.length === 0) {
                suggBox.innerHTML = `<li>No results found</li>`;
                return;
            }

            movies.forEach(movie => {
                const listItem = document.createElement('li');
                listItem.textContent = movie; // Set the text content to the movie title
                listItem.onclick = () => select(listItem); // Attach click event
                suggBox.appendChild(listItem); // Append the <li> to the suggestions list
            });
        }
    </script>
</body>
</html>
