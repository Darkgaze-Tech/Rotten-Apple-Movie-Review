<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Reset Password Page</title>
<script src="https://www.google.com/recaptcha/api.js"></script>
<style><%@include file="/WEB-INF/css/style.css"%></style>
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
 <section class="container forms">
<div class="form login">
                <div class="form-content">
                    <header>Change to new password</header>
<form id= "ResetPasswordForm" action="ResetPasswordServlet" method="post">
                         <div class="field input-field">
                         <input type="hidden" name="token" value=<%=request.getParameter("token") %> />
                        <input type="password" id="password" placeholder="New password" class="password" name="password" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" title="Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters" required>
                    </div>
                    <div class="field input-field">
                        <input type="password" id="confirmPassword" placeholder="Confirm password" class="password" name="confirmPassword" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" title="Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters" required>
                        <i class='fa fa-eye' id ="togglePassword"></i>                 
                    </div>
                     <span id="passwordError" class="passworderror"></span>
                      <div class="g-recaptcha" data-sitekey="6Lf2ri8qAAAAAF0UfVkbLf9l_aHAOXMpy-KPNGrM" data-callback="callback"></div>
                         <div class="field button-field">
                            <button id="submitBtn" class="submit-btn">Reset Password</button>
                        </div>
                    </form> 
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
      
      document.getElementById('ResetPasswordForm').addEventListener('input', function () {
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