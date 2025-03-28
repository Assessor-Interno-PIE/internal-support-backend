package app.config;

//JwtService.java

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import app.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceGenerator {  

  public String generateToken(User userDetails) {
	
	  
	  //AQUI VOCÊ PODE COLOCAR O QUE MAIS VAI COMPOR O PAYLOAD DO TOKEN
      Map<String, Object> extraClaims = new HashMap<>();
      extraClaims.put("id", userDetails.getId().toString());
      extraClaims.put("name", userDetails.getName());
      extraClaims.put("username", userDetails.getUsername());
      extraClaims.put("department", userDetails.getDepartment());
      extraClaims.put("isAdmin", userDetails.getIsAdmin());
	  
      
      return Jwts
              .builder()
              .setClaims(extraClaims)
              .setSubject(userDetails.getUsername())
              .setIssuedAt(new Date(System.currentTimeMillis()))
              .setExpiration(new Date(new Date().getTime() + 3600000 * JwtConfig.TOKEN_EXPIRATION_TIME))
              .signWith(getSigningKey(), JwtConfig.SIGNATURE_ALGORITHM)
              .compact();
  }
  
  private Claims extractAllClaims(String token) {
      return Jwts
              .parserBuilder()
              .setSigningKey(getSigningKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
  }


  public boolean isTokenValid(String token, UserDetails userDetails) {
      final String username = extractUsername(token);
      return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
      return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
      return extractClaim(token, Claims::getExpiration);
  }

  private Key getSigningKey() {
      byte[] keyBytes = Decoders.BASE64.decode(JwtConfig.SECRET_KEY);
      return Keys.hmacShaKeyFor(keyBytes);
  }
  

  public String extractUsername(String token) {
      return extractClaim(token,Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
  }

}
