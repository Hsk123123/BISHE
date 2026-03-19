package com.skillchain.filter;

import com.skillchain.entity.User;
import com.skillchain.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            username = jwtUtil.getUsernameFromToken(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtUtil.isTokenExpired(token)) {
                Claims claims = jwtUtil.parseToken(token);
                Long userId = claims.get("userId", Long.class);
                Integer role = claims.get("role", Integer.class);

                User user = new User();
                user.setUserId(userId);
                user.setUsername(username);
                user.setRole(role);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role != null) {
                    if (role == 2) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    } else if (role == 1) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_WORKER"));
                    } else {
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    }
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // 将用户ID设置到request属性中，方便Controller使用@RequestAttribute获取
                request.setAttribute("userId", userId);
                request.setAttribute("username", username);
                request.setAttribute("role", role);
            }
        }

        filterChain.doFilter(request, response);
    }
}
