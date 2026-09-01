package com.vikas.razorpay.commonlib.ratelimit;

public interface RateLimiter {

    RateLimitResult check(String key,int maxRequestAllowed,long windowSeconds);

}
