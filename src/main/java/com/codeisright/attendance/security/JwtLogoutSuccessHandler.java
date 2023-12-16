package com.codeisright.attendance.security;

import com.codeisright.attendance.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class JwtLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private final UserDetailsServiceImpl userDetailsService;

    public JwtLogoutSuccessHandler(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        String token = request.getHeader("Authorization").substring(7);
        Claims claims = Jwts.parser()
                .setSigningKey("secret".getBytes())
                .parseClaimsJws(token)
                .getBody();
        String Id = claims.getSubject();
        userDetailsService.deleteJwt(Id);

        // 返回一个json
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if(userDetailsService.getJwt(Id)==null)
            try {
                response.getWriter().write("{\"userId\":\"" + Id + "\",\"status\":\"ok\",\"msg\":\"Logout success\"}");
            } catch (Exception e) {
                logger.error("Error happened when writing response" + e);
            }
        else
            try {
                response.getWriter().write("{\"userId\":\"" + Id + "\",\"status\":\"error\",\"msg\":\"Logout failed. Please try again\"}");
            }catch (Exception e) {
                logger.error("Error happened when writing response" + e);
            }
    }
}