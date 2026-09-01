package com.vikas.razorpay.merchant_service.security;


import com.vikas.razorpay.commonlib.context.MerchantContext;
import com.vikas.razorpay.commonlib.exception.RateLimitException;
import com.vikas.razorpay.commonlib.ratelimit.RateLimitResult;
import com.vikas.razorpay.commonlib.ratelimit.RateLimiter;
import com.vikas.razorpay.merchant_service.Entity.ApiKey;
import com.vikas.razorpay.merchant_service.cache.ApiKeyCache;
import com.vikas.razorpay.merchant_service.cache.ApiKeyCacheEntry;
import com.vikas.razorpay.merchant_service.repo.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;


@Component
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {


    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver resolver;
    private final ApiKeyCache apiKeyCache;
    private final RateLimiter rateLimiter;

    @Value("${app.rate-limit.use-case.api-key.requests-per-minute:60}")
    private Integer requestPerMinute;



    public ApiKeyAuthenticationFilter(
            ApiKeyRepository apiKeyRepository,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            MerchantContext merchantContext,
            ApiKeyCache apiKeyCache,
            RateLimiter rateLimiter
    ) {
        this.apiKeyRepository=apiKeyRepository;
        this.resolver = resolver;
        this.merchantContext = merchantContext;
        this.apiKeyCache=apiKeyCache;
        this.rateLimiter=rateLimiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
           log.info("Incoming Request: {}",request.getRequestURI());

           try {
               String header = request.getHeader("Authorization");
               if (header == null || !header.startsWith("Basic ")) {
                   filterChain.doFilter(request, response);
                   return;
               }
               // Authorization: Basic key rgdrghsifhiesf
               String[] credentials = decode(header);
               if (credentials == null) {
                   throw new BadRequestException("Malformed API KEY HEADER");
               }

               String keyId = credentials[0];
               String rawSecret = credentials[1];

               ApiKeyCacheEntry apiKeyEntry=apiKeyCache
                       .get(keyId)
                       .orElseGet(()-> loadAndCache(keyId));

//               ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
//                       .orElseThrow(() -> new BadRequestException("Invalid or missing API Key"));
               if (apiKeyEntry!=null && !apiKeyEntry.enabled() || !secretMatches(rawSecret, apiKeyEntry)) {
                   throw new BadRequestException("Invalid Api Key");
               }
               RateLimitResult rateLimitResult=rateLimiter.check("apikey:"+keyId,requestPerMinute,60);
               if(!rateLimitResult.isAllowed()){
                   log.warn("Too many requests keyId={}",keyId);
                   throw new RateLimitException("Too many requests",rateLimitResult.retryAfterSeconds());
               }

               response.setHeader("X-RateLimit-Limit",String.valueOf(requestPerMinute));
               response.setHeader("X-RateLimit-Remaining",String.valueOf(rateLimitResult.remaining()));


               var auth = new UsernamePasswordAuthenticationToken(keyId, null,
                       List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));
               SecurityContextHolder.getContext().setAuthentication(auth);
               merchantContext.setKeyId(apiKeyEntry.keyId());
               merchantContext.setMerchantId(apiKeyEntry.merchantId());
               filterChain.doFilter(request,response);
           } catch (Exception e) {
               resolver.resolveException(request,response,null,e);
           }
    }

    private ApiKeyCacheEntry loadAndCache(String keyId) {
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                .orElse(null);
        if(apiKey!=null){
            ApiKeyCacheEntry apiKeyCacheEntry=new ApiKeyCacheEntry(
                    apiKey.getKeyId(),
                    apiKey.getKeySecretHash(),
                    apiKey.getPreviousKeySecretHash(),
                    apiKey.getGracePeriodExpiresAt(),
                    apiKey.getMerchant().getId(),
                    apiKey.getEnvironment(),

                    apiKey.isEnabled());
            apiKeyCache.put(keyId,apiKeyCacheEntry);
            return apiKeyCacheEntry;
        }else{
            return null;
        }
    }

    private String[] decode(String header){
        String encoded=header.substring("Basic ".length());
        String decoded=new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        int colon=decoded.indexOf(":");
        if(colon<1) return null;
        return new String[]{decoded.substring(0,colon),decoded.substring(colon+1)};

    }

    private boolean secretMatches(String rawSecret,ApiKeyCacheEntry apikey){
        if(passwordEncoder.matches(rawSecret, apikey.keySecretHash())){
            return true;
        }
        return apikey.isInGracePeriod() && passwordEncoder.matches(rawSecret, apikey.previousKeySecretHash());
    }
}
