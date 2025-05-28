package utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JWTUtility {

	   

	    	static String jwtsecret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

	    	
	    	private static SecretKey getKey() {
	            byte[] keyBytes = Decoders.BASE64.decode(jwtsecret);
	            return Keys.hmacShaKeyFor(keyBytes);
	        }
	
	    public static String generateToken(String useremail) {
	        Map<String, Object> claims = new HashMap<>();
	        claims.put("useremail",useremail );
	       
	        return Jwts.builder()
	                .claims()
	                .add(claims)
	                .subject(useremail)
	                .issuedAt(new Date(System.currentTimeMillis()))
	                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
	                .and()
	                .signWith(getKey())
	                .compact();

	    }
	    
	  
	   
	    public static Jws<Claims> parseJwt(String jwtString) throws Exception{

	        Jws<Claims> jwt = Jwts.parser()
	                .verifyWith(getKey() )
	                .build()
	                .parseSignedClaims(jwtString);
	       
	        return jwt;
	    }
	 

}
