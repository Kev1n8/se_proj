package com.codeisright.attendance.security;

import com.codeisright.attendance.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

public class JwtLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private final UserDetailsServiceImpl userDetailsService;

    public JwtLogoutSuccessHandler(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        String token = request.getHeader("Authorization").substring(7);
        Claims claims = Jwts.parser()
                .setSigningKey("secret".getBytes())
                .parseClaimsJws(token)
                .getBody();
        String Id = claims.getSubject();
        String currentToken = userDetailsService.getJwt(Id);
        if (!token.equals(currentToken)) {
            logger.error("Invalid token");
            response.getWriter().write("{\"userId\":\"" + Id + "\",\"status\":\"error\",\"msg\":\"Logout failed. " +
                    "Wrong token.\"}");
            return;
        }
        logger.info("User: " + Id + " logout successfully.");
        userDetailsService.deleteJwt(Id);

        // 返回一个json
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if (userDetailsService.getJwt(Id) == null)
            response.getWriter().write("{\"userId\":\"" + Id + "\",\"status\":\"ok\",\"msg\":\"Logout success\"}");
        else
            response.getWriter().write("{\"userId\":\"" + Id + "\",\"status\":\"error\",\"msg\":\"Logout failed. " +
                    "Please try again\"}");
    }
}