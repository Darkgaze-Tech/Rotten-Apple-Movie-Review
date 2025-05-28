<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
 <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title> Responsive Login and Signup Form </title>
        <!-- CSS -->
        <style><%@include file="/WEB-INF/css/style.css"%></style>
                
       <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
                       
</head>
  
   <body>
        <section class="container forms">
            <div class="form login">
                <div class="form-content">
                    <header>Login</header>
                     <p class="Error">${errorMessage}</p>
                     <p class="success">${Message}</p>
                    <form action="LoginServlet" method="POST">
                        <div class="field input-field">
                            <input type="email" placeholder="Email" class="input" name="email" pattern="/^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/"required>
                        </div>
                        <div class="field input-field">
                            <input type="password" placeholder="Password" class="password"name="password" id="password" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" title="Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters"  required>
                             <i class='fa fa-eye' id="togglePassword" ></i>
                        </div>
                        <div class="form-link">
                            <a href="Forgotpassword.jsp" class="forgot-pass">Forgot password?</a>
                        </div>
                        <div class="field button-field">
                            <button>Login</button>
                        </div>
                    </form>
                    <div class="form-link">
                        <span>Don't have an account? <a href="Register.jsp" class="link signup-link">Signup</a></span>
                    </div>
                </div>
                <div class="line"></div>
                <div class="media-options">
                    <a href="GoogleLoginServlet" class="field google">
                        <img src="google.png" alt="" class="google-img">
                        <span>Login with Google</span>
                    </a>
                </div>
            </div>
            </section>
           
            
        <!-- JavaScript -->
           <script >
  
      const togglePassword = document.getElementById("togglePassword");
      togglePassword?.addEventListener("click", function (e) {
    	  // toggle the type attribute
    	    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    	    password.setAttribute('type', type);
    	    // toggle the eye slash icon
    	    this.classList.toggle('fa-eye-slash');
      });
      
       </script>
       
    </body>
</html>