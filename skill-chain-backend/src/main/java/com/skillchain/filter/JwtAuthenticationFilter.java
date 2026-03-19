package com.skillchain.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillchain.common.Result;
import com.skillchain.dto.LoginDTO;
import com.skillchain.entity.User;
import com.skillchain.service.AuthService;
import com.skillchain.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtUtil jwtUtil;
    private AuthService authService;
    private ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthService authService, ObjectMapper objectMapper) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), LoginDTO.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
            );
            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = authResult.getName();
        User user = authService.getUserByUsername(username);
        if (user == null) {
            Result<Void> result = Result.error(401, "Login failed: user not found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("user", user);

        Result<Map<String, Object>> result = Result.success("Login successful", responseData);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Result<Void> result = Result.error(401, "Login failed: " + failed.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
