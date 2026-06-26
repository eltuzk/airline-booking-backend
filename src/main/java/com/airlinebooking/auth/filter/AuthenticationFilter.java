package com.airlinebooking.auth.filter;

import com.airlinebooking.cache.services.RedisService;
import com.airlinebooking.common.constans.RedisKeyConstants;
import com.airlinebooking.common.dto.LoginSessionCache;
import com.airlinebooking.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String dataAuthen = request.getHeader("Authorization");
        if (dataAuthen != null && dataAuthen.startsWith("Bearer ")) {
            String token = dataAuthen.substring(7);
            Claims claim = jwtUtil.parseAccessToken(token);
            if (claim != null) {
                String userId = claim.getSubject();
                String role = claim.get("role", String.class);
                List<GrantedAuthority> authorityList = new ArrayList<>();
                if (role != null && !role.isBlank()) {
                    authorityList.add(new SimpleGrantedAuthority(role));
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorityList);
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);

                String sessionKey = RedisKeyConstants.LOGIN_SESSION + userId;
                LoginSessionCache session = (LoginSessionCache) redisService.get(sessionKey);
                if (session != null) {
                    session.setLastActivityTime(System.currentTimeMillis());
                    redisService.set(sessionKey, session, RedisKeyConstants.SESSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
