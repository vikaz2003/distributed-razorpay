package com.vikas.razorpay.commonlib.idempotency;

import java.time.Duration;
import java.util.Optional;

public interface IdempotencyStore {

    String IN_PROGRESS = "__IN_PROGRESS__";

    boolean setIfAbsent(String key, Duration tll);

    void store(String key,String value,Duration ttl);

    Optional<String> get(String key);

    void delete(String key);
}
