package doore.login.utils;

import doore.login.exception.LoginException;
import doore.login.exception.LoginExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expire-length}")
    private long expireTimeMilliSecond;

    public String generateToken(final String id) {
        final Claims claims = Jwts.claims();
        claims.put("memberId", id);
        final Date now = new Date();
        final Date expiredDate = new Date(now.getTime() + expireTimeMilliSecond);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractMemberId(final String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("memberId")
                    .toString();
        } catch (final ExpiredJwtException error) {
            throw new LoginException(LoginExceptionType.EXPIRED_ACCESS_TOKEN);
        } catch (final Exception error) {
            throw new LoginException(LoginExceptionType.INVALID_ACCESS_TOKEN);
        }
    }
}
