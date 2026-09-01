package com.vikas.razorpay.commonlib.idempotency;


import com.vikas.razorpay.commonlib.context.MerchantContext;
import com.vikas.razorpay.commonlib.ratelimit.FixedWindowRateLimiter;
import com.vikas.razorpay.commonlib.ratelimit.RateLimiter;
import com.vikas.razorpay.commonlib.ratelimit.SlidingWindowLuaLimiter;
import com.vikas.razorpay.commonlib.ratelimit.SlidingWindowRateLimiter;
import com.vikas.razorpay.commonlib.ratelimit.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;

@AutoConfiguration
public class SharedResilienceAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory){
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyStore.class)
    public IdempotencyStore idempotencyStore(StringRedisTemplate stringRedisTemplate){
        return new RedisIdempotencyStore(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyFilter.class)
    public IdempotencyFilter idempotencyFilter(MerchantContext merchantContext, IdempotencyStore idempotencyStore, @Qualifier("handlerExceptionResolver")HandlerExceptionResolver handlerExceptionResolver){
        return new IdempotencyFilter(merchantContext,idempotencyStore,handlerExceptionResolver);
    }


    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnProperty(name="app.rate-limit.method", havingValue = "fixed", matchIfMissing = true)
    public RateLimiter fixedWindowRateLimiter(StringRedisTemplate stringRedisTemplate){
        return new FixedWindowRateLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnProperty(name="app.rate-limit.method", havingValue = "sliding")
    public RateLimiter slidingWindowRateLimiter(StringRedisTemplate stringRedisTemplate){
        return new SlidingWindowRateLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnProperty(name="app.rate-limit.method", havingValue = "sliding-lua")
    public RateLimiter slidingWindowLuaLimiter(StringRedisTemplate stringRedisTemplate){
        return new SlidingWindowLuaLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnProperty(name="app.rate-limit.method", havingValue = "token-bucket")
    public RateLimiter tokenBucketRateLimiter(StringRedisTemplate stringRedisTemplate){
        return new TokenBucketRateLimiter(stringRedisTemplate);
    }

}
