package mkoutra.birthdaykeeper.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Provides services for handling JWT (JSON Web Token) operations,
 * including token creation, reading claims, setting expiration,
 * and managing various token attributes.
 */
@Service
public class JwtService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Create the jwt token. The token contains the username
     * and the role provided.
     * @param username  User's username
     * @param role      User's role
     * @return  The jwt token.
     */
    public String generateToken(String username, String role) {
        var claims = new HashMap<String, Object>();
        claims.put("role", role);
        return Jwts
                .builder()
                .setIssuer("mkoutra")
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the provided JWT token by checking the username and expiration.
     *
     * @param token the JWT token to validate.
     * @param userDetails the user details whose username is checked against the token's subject.
     * @return true if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String subject = extractSubject(token);
        return (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String getStringClaim(String token, String claim) {
        return extractAllClaims(token).get(claim, String.class);
    }

    /**
     * Extracts the subject (username in our implementation) from the JWT token.
     *
     * @param token the JWT token.
     * @return the username in the token.
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates an HMAC-SHA256 (HS256) signing key for JWT authentication.
     * This method decodes the provided base64-encoded secret key into a byte array
     * and uses it to create a {@link javax.crypto.SecretKey} instance that implements
     * the {@link java.security.Key} interface. The generated key is suitable for
     * signing and verifying JWT tokens using the HS256 algorithm.
     *
     * @return a SecretKey object compatible with the HS256 signing algorithm.
     */
    private Key getSignInKey() {
        // Decodes the base64-encoded secret key
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        // Creates an HMAC-SHA256 signing key from the byte array
        return Keys.hmacShaKeyFor(keyBytes);
    }
}