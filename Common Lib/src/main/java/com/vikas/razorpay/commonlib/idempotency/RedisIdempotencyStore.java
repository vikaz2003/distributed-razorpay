package com.vikas.razorpay.commonlib.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.util.Optional;



@Slf4j
@RequiredArgsConstructor
public class RedisIdempotencyStore implements IdempotencyStore{

    private static final String Prefix="idempotency";
    private final StringRedisTemplate redis;

    @Override
    public boolean setIfAbsent(String key, Duration ttl) {
        try{
            Boolean set=redis.opsForValue().setIfAbsent(Prefix+key,IN_PROGRESS,ttl);
            return Boolean.TRUE.equals(set);
        } catch (DataAccessException e) {
            log.warn("Idempotency store available ,failing open for key={}",key,e);
            return true;
        }

    }

    @Override
    public void store(String key, String value, Duration ttl) {
           try{
               redis.opsForValue().set(Prefix+key,value,ttl);
           } catch (DataAccessException e) {
               log.warn("Failed to persist,failing open for key={}",key,e);

           }
    }

    @Override
    public Optional<String> get(String key) {
        try{
            return Optional.ofNullable(redis.opsForValue().get(Prefix+key));
        }catch (DataAccessException e){
            log.warn("Failed to persist, failing open for key={}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        try {
            redis.delete(Prefix + key);
        } catch (DataAccessException e) {
            log.warn("Failed to clear idempotency key={}", key, e);
        }
    }
}
