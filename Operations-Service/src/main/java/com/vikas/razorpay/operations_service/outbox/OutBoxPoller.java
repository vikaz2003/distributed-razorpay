package com.vikas.razorpay.operations_service.outbox;


import com.vikas.razorpay.commonlib.config.KafkaProperties;
import com.vikas.razorpay.commonlib.enums.OutBoxStatus;

import com.vikas.razorpay.operations_service.entity.OutBoxEvent;
import com.vikas.razorpay.operations_service.repository.OutBoxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutBoxPoller {

    private final OutBoxEventRepository outBoxEventRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final OutBoxHandler outBoxHandler;

    @Scheduled(fixedDelay = 5000)
    public void poll() {

            List<OutBoxEvent> pendingEvents = outBoxEventRepository.findByOutBoxStatusOrderByCreatedAtAsc(OutBoxStatus.PENDING);
            for (OutBoxEvent event : pendingEvents) {
                try {
                    String topic = kafkaProperties.topicFor(event.getAggregateType());
                    String key = extractMerchantId(event.getPayload());
                    Map<String, Object> envelope = Map.of(
                            "eventType", event.getEventType(),
                            "aggregateType", event.getAggregateType().name(),
                            "aggregateId", event.getAggregateId().toString(),
                            "data", event.getPayload()

                    );
                    kafkaTemplate.send(topic, key, envelope).get(5, TimeUnit.SECONDS);
                    outBoxHandler.handleEventPublisher(event);
                } catch (Exception ex) {
                    log.error("Outbox event failed,eventId:{} and attempts:{}",event.getId(),event.getRetries());
                    outBoxHandler.handleFailed(event,ex.getLocalizedMessage());
                }
            }


    }


    private String extractMerchantId(Map<String,Object> payload){
        Object value=payload.get("merchantId");
        return value !=null ? value.toString() : "unknown";
    }


}
