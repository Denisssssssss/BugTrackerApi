package com.itis.bugtracker.BugTrackerLibApi.security.token;

import javax.servlet.http.HttpServletRequest;

public interface TokenProvider {

    String generate(Long userId);

    boolean validate(String token);

    Long getUserIdFromToken(String accessToken);

    String getTokenFromRequest(HttpServletRequest request);
}