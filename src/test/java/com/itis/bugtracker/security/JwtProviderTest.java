package com.itis.bugtracker.security;

import com.itis.bugtracker.BugTrackerLibApi.security.token.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.time.Instant;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtProviderTest {

    @Value("${security.jwt.secret}")
    private String JWT_SECRET;

    @Autowired
    private TokenProvider provider;

    @Test
    public void validateTokenTest() {
        Long userId = 1L;
        String token = provider.generate(userId);

        Assert.assertFalse(provider.validate(null));
        Assert.assertTrue(provider.validate(token));

        Instant expireDate = Instant.now().plusMillis(-1000);
        String badToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expireDate))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();

        Assert.assertFalse(provider.validate(badToken));
    }
}
