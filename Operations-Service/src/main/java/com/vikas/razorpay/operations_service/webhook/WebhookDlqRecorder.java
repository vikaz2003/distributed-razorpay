package com.vikas.razorpay.operations_service.webhook;


import com.vikas.razorpay.commonlib.enums.WebhookEventStatus;
import com.vikas.razorpay.operations_service.entity.DlqEvent;
import com.vikas.razorpay.operations_service.entity.WebhookEvent;
import com.vikas.razorpay.operations_service.repository.DlqEventRepository;
import com.vikas.razorpay.operations_service.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebhookDlqRecorder {

    private final DlqEventRepository dlqEventRepository;
    private final WebhookEventRepository webhookEventRepository;


    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void recordAfterAttemptsExhausted(WebhookEvent event, String finalError){
         event.setStatus(WebhookEventStatus.DEAD);
         webhookEventRepository.save(event);

        DlqEvent dlqEvent=DlqEvent.builder()
                .finalError(finalError)
                .merchantId(event.getMerchantId())
                .payload(event.getPaylod())
                .webhookEvent(event)
                .movedAt(LocalDateTime.now())
                .build();
        dlqEventRepository.save(dlqEvent);
    }

    public void recordConsumerFailed(ConsumerRecord<String, Map<String, Object>> record, String message) {

        Map<String,Object> envelope=record.value();

        UUID merchantId=null;

        try{
           Map<String,Object> data=(Map<String,Object>) envelope.get("data");
           Object merchantIdRaw=data!=null?data.get("merchantId"):null;
           if(merchantIdRaw!=null){
               merchantId=UUID.fromString(merchantIdRaw.toString());
           }

        } catch (Exception ignored) {

        }
        DlqEvent dlqEvent=DlqEvent.builder()
                .finalError(message)
                .merchantId(merchantId)
                .payload(envelope!=null?envelope:Map.of())
                .webhookEvent(null)
                .movedAt(LocalDateTime.now())
                .build();
        dlqEventRepository.save(dlqEvent);
    }
}
