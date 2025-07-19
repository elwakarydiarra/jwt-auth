package rentals.jwt_auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    //private static final String SECRET_KEY = "6B5970337336763979244226452948402B4D6251655468576D5A713474377721"; // 256-bit clé encodée en hexadécimal
	//private static final String SECRET_KEY = "NlI5NzAzMzczNjc2Mzk3OTI0NDIyNjQ1Mjk0ODQwMkI0RDYyNTE2NTQ2ODU3NkQ1QTcxMzQ3Mzc3MjE=";
	private static final String SECRET_KEY = "sbUzGmPZhvqDj0VclyLoDnUGNE4N0hWrBGspTw0GNYE=";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1h

    // Génère un token à partir de UserDetails
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrait le username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrait n’importe quelle donnée
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Décodage du token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Clé de signature HMAC-SHA256
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
