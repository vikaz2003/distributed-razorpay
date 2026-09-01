package com.vikas.razorpay.merchant_service.security;

import com.vikas.razorpay.commonlib.context.MerchantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver resolver;
    private final MerchantContext merchantContext;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            MerchantContext merchantContext
    ) {
        this.jwtUtil = jwtUtil;
        this.resolver = resolver;
        this.merchantContext = merchantContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming request: {}",request.getRequestURI());
        try {
            final String authToken = request.getHeader("Authorization");

            if (authToken == null || !authToken.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwtToken = authToken.split("Bearer ")[1];
            Claims claims = jwtUtil.verifyAccessToken(jwtToken);
            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var auth = new UsernamePasswordAuthenticationToken(claims
                        .getSubject(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + jwtUtil.extractRole(claims))));
                SecurityContextHolder.getContext().setAuthentication(auth);
                merchantContext.setMerchantId(UUID.fromString(jwtUtil.extractMerchantId(claims)));
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            resolver.resolveException(request,response,null,e);
        }


    }
}
