package pizzeria.order.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pizzeria.order.Application;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

/**
 * Verifies the JWT token in the request for validity.
 */
@Component
public class JwtTokenVerifier {
    @Value("${jwt.secret}")  // automatically loads jwt.secret from resources/application.properties
    private transient String jwtSecret;

    /**
     * Validate the JWT token for expiration.
     */
    @Application.ExcludeFromJacocoGeneratedReport
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Get the id from the token
     * @param token JWT token
     * @return The id from the token
     */
    public String getNetIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Returns a collection of granted authorities from the token (in our case it will be only one authority)
     * @param token JWT token
     * @return A collection of granted authorities in the format ROLE_(Given role)
     */
    public Collection
            <GrantedAuthority> getRoleFromToken(String token) {
        String role = getClaimFromToken(token, claims -> claims.get("role").toString());
        role = role.replace("[", "").replace("]", "");
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    /**
     * Return the expiration date from the JWT toekn
     * @param token JWT token
     * @return Date of expiration
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Checks whether the token has expired
     * @param token JWT token
     * @return True or False depending on whether the token has expired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);

        return expiration.before(new Date());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}
