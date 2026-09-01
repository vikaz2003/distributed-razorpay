package com.vikas.razorpay.payment_service.outbox;


import com.vikas.razorpay.commonlib.enums.EventAggregateType;
import com.vikas.razorpay.payment_service.entity.OutBoxEvent;
import com.vikas.razorpay.payment_service.repository.OutBoxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutBoxEventPublisher {

    private final OutBoxEventRepository repo;


    public void publish(EventAggregateType aggregateType, UUID aggregateId, String eventType, Map<String,Object> payload){
        OutBoxEvent outBoxEvent=    OutBoxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .publishedAt(LocalDateTime.now())
                .build();

        repo.save(outBoxEvent);
    }
}
