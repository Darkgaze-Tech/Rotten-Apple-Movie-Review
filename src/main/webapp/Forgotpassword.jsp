<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Forgot password page</title>
<script src="https://www.google.com/recaptcha/api.js"></script>
 <style><%@include file="/WEB-INF/css/style.css"%></style>
</head>
<body>

  <section class="container forms">
            <div class="form login">
                <div class="form-content">
                    <header>Forgot Password ?</header>
                     <p class="Error">${errorMessage}</p>
                    <form action="ForgotPasswordServlet" method="post">
                        <div class="field input-field">
                            <input type="email" placeholder="Email" class="input" name="email"pattern="/^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/"required>
                        </div>
                         <div class="g-recaptcha" data-sitekey="6Lf2ri8qAAAAAF0UfVkbLf9l_aHAOXMpy-KPNGrM" data-callback="callback"></div>
                         <div class="field button-field">
                            <button id="submitBtn" class="submit-btn">Email Link</button>
                        </div>
                    </form> 
                         
                </div>
            </div>
       </section>
       <script>
 function callback() {
    	  const submitBtn = document.getElementById('submitBtn');
    	  submitBtn.classList.add('enabled');
          submitBtn.disabled = false;
        }
      
     
       </script>

</body>
</html>