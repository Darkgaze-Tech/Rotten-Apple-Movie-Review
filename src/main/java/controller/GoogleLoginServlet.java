package controller;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collections;

@WebServlet("/GoogleLoginServlet")
public class GoogleLoginServlet extends HttpServlet {
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";
    private static final String REDIRECT_URI = "http://localhost:8080/rottenapple/GoogleLoginCallbackServlet";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        GoogleAuthorizationCodeRequestUrl authUrl = new GoogleAuthorizationCodeRequestUrl(
                CLIENT_ID,
                REDIRECT_URI,
                Collections.singleton("email")
        )
        .setAccessType("offline")
        .setState("security_token=" + request.getSession().getId());

        response.sendRedirect(authUrl.build());
    }
}