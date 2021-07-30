package com.itis.bugtracker.BugTrackerLibImpl.security.jwt;

import com.itis.bugtracker.BugTrackerLibApi.security.token.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.Instant;

@Component
public class JwtProvider implements TokenProvider {

    @Value("${security.jwt.secret}")
    private String JWT_SECRET;

    @Value("${security.jwt.token-expire-time}")
    private long TOKEN_EXPIRE_TIME;

    @Value("${security.jwt.header}")
    private String TOKEN_HEADER;

    @Override
    public String generate(Long userId) {
        Instant expireDate = Instant.now().plusMillis(TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expireDate))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    @Override
    public boolean validate(String token) {
        if (token == null) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public Long getUserIdFromToken(String accessToken) {
        return Long.valueOf(Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject()
        );
    }

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {

        return request.getHeader(TOKEN_HEADER);
    }
}
