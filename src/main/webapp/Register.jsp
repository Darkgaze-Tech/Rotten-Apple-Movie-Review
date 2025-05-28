<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
 <script src="https://www.google.com/recaptcha/api.js"></script>
   <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
 <!-- CSS -->
        <style><%@include file="/WEB-INF/css/style.css"%></style>
         <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
          
</head>
<body>
    <section class="container forms">
        <div class="form signup">
            <div class="form-content">
                <header>Signup</header>

                <!-- Display error message if exists -->
                <c:if test="${not empty errorMessage}">
                    <div>
                        <p class="Error">${errorMessage}</p>
                    </div>
                </c:if>

                <form id="registrationForm" action="RegisterServlet" method="post">
                    <div class="field input-field">
                        <input type="email" placeholder="Email" class="input" name="email" pattern="/^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/"required>
                    </div>
                    <div class="field input-field">
                        <input type="password" id="password" placeholder="Create password" class="password" name="password" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" title="Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters" required>
                    </div>
                    <div class="field input-field">
                        <input type="password" id="confirmPassword" placeholder="Confirm password" class="password" name="confirmPassword" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" title="Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters" required>
                        <i class='fa fa-eye' id ="togglePassword"></i>                 
                    </div>
                      <span id="passwordError" class="passworderror"></span>
                    <div class="g-recaptcha" data-sitekey="6Lf2ri8qAAAAAF0UfVkbLf9l_aHAOXMpy-KPNGrM" data-callback="callback"></div>
                    
                    <div class="field input-field">
                        <button class="submit-btn" type="submit" id="submitBtn" disabled>Signup</button>
                    </div>
                </form>
                
                <div class="form-link">
                    <span>Already have an account? <a href="Login.jsp" class="link login-link">Login</a></span>
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
     
       <script >
       
  
  
      const togglePassword = document.getElementById("togglePassword");
      togglePassword?.addEventListener("click", function (e) {
    	  // toggle the type attribute
    	    const type = confirmPassword.getAttribute('type') === 'password' ? 'text' : 'password';
    	    confirmPassword.setAttribute('type', type);
    	    // toggle the eye slash icon
    	    this.classList.toggle('fa-eye-slash');
      });
      
      document.getElementById('registrationForm').addEventListener('input', function () {
          validateForm();
      });
      function validateForm() {
    	  const password = document.getElementById('password').value;
          const confirmPassword = document.getElementById('confirmPassword').value;
          const submitBtn = document.getElementById('submitBtn');
    	  const errorElement = document.getElementById('passwordError');

          let isValid = true;

          if ( !password || !confirmPassword) {
              isValid = false;
          }
	      if (password !== confirmPassword) {
	          errorElement.textContent = 'Passwords do not match';
	          errorElement.classList.remove('success');
	          errorElement.classList.add('Error');
	          isValid = false;
	      } else {
	          errorElement.textContent = 'Passwords match';
	          errorElement.classList.remove('Error');
	          errorElement.classList.add('success');
	      }
	    
      }
     
      function callback() {
    	  const submitBtn = document.getElementById('submitBtn');
    	  submitBtn.classList.add('enabled');
          submitBtn.disabled = false;
        }
      
     
       </script>
</body>

</html>