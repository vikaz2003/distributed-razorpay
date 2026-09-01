package com.vikas.razorpay.commonlib.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;


@RequiredArgsConstructor
public class SlidingWindowRateLimiter implements  RateLimiter{

    private final StringRedisTemplate redisTemplate;


    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {
           long nowMs=System.currentTimeMillis();
           long floorMs=nowMs-windowSeconds*1000;
           String redisKey="ratelimit:sliding:"+key;
            var zset=redisTemplate.opsForZSet();
            zset.removeRangeByScore(redisKey,Double.NEGATIVE_INFINITY,floorMs);
            Long count=zset.zCard(redisKey);
            long current=count!=null?count:0L;
            if(current>=maxRequestAllowed){
                var oldest=zset.rangeWithScores(redisKey,0,0);
                int retryAfter=1;
                if((oldest!=null) && !oldest.isEmpty()){
                    Double oldestScore=oldest.iterator().next().getScore();
                    if(oldestScore!=null){
                        long windowExpiresMs=oldestScore.longValue()+windowSeconds*1000;
                        retryAfter=(int)Math.ceil((double) (windowExpiresMs - nowMs) /1000);
                    }
                }
                return RateLimitResult.denied(retryAfter);
            }
            zset.add(redisKey, UUID.randomUUID().toString(),nowMs);
            redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds+1));
            return RateLimitResult.allowed((int)(maxRequestAllowed-count-1L));


    }
}
