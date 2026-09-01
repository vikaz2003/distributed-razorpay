package com.vikas.razorpay.operations_service.webhook;


import com.vikas.razorpay.commonlib.enums.WebhookEventStatus;
import com.vikas.razorpay.operations_service.entity.WebhookEvent;
import com.vikas.razorpay.operations_service.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryExecutor {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;
    private final RestClient restClient;
    private final WebhookDlqRecorder webhookDlqRecorder;

    private static final List<Duration> BACKOFF=List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5),
            Duration.ofMinutes(30), Duration.ofHours(2),
            Duration.ofHours(8), Duration.ofHours(24)
    );

    private final int MAX_ATTEMPTS=7; // max attempts before we put them into a dlq

    @Value("${webhook.delivery.signature-header:X-Razorpay-Signature}")
    private String signatureHeader;


    @Transactional
    public void deliver(UUID webhookEventId){
        Optional<WebhookEvent> webhookEvent=webhookEventRepository.findById(webhookEventId);
        if(webhookEvent.isEmpty()){
            log.warn("No webhook event found with id: {}",webhookEventId);
            return;
        }
        WebhookEvent event=webhookEvent.get();
        if(event.getStatus()== WebhookEventStatus.DEAD||event.getStatus()==WebhookEventStatus.DELIVERED){
            log.warn("Cannot deliver the event {} in status : {}",webhookEventId,event.getStatus());
            return;
        }
        event.setAttempts(event.getAttempts()+1);
        event.setLastAttemptAt(LocalDateTime.now());

       try{
          var response= restClient.post()
                   .uri(event.getTargetUrl())
                   .header(signatureHeader,event.getSignature())
                   .contentType(MediaType.APPLICATION_JSON)
                   .body(Map.of("event",event.getEventType(),"payload",event.getPaylod()))
                   .retrieve().toBodilessEntity();
          int statusCode=response.getStatusCode().value();
          event.setLastResponseCode(statusCode);
          if(response.getStatusCode().is2xxSuccessful()){
              event.setStatus(WebhookEventStatus.DELIVERED);
              event.setDeliveredAt(LocalDateTime.now());
              webhookEventRepository.save(event);
              log.info("Got the acknowledgement from merchant for event: {}",event);
              return;
          }
          handleAttemptFailed(event,"HTTP"+statusCode);
       } catch (RestClientException e) {
           log.error("Got Rest Client Exception: ",e);
           event.setLastResponseBody(e.getMessage());
           handleAttemptFailed(event,e.getMessage());
       }

    }

    private void handleAttemptFailed(WebhookEvent event,String error){
        event.setLastResponseBody(error);
        if(event.getAttempts()>=MAX_ATTEMPTS){
            event.setStatus(WebhookEventStatus.DEAD);
            // we need to add dlq recording
            webhookDlqRecorder.recordAfterAttemptsExhausted(event,error);
            return;
        }


        Duration backoff=BACKOFF.get(event.getAttempts()-1);
        LocalDateTime nextRetry=LocalDateTime.now().plus(backoff);
        event.setStatus(WebhookEventStatus.FAILED);
        event.setNextRetryAt(nextRetry);
        webhookEventRepository.save(event);
        webhookRetryQueue.enqueue(event.getId(),nextRetry);
        log.error("Handling attempt failed for webhook event : {} with attempts {}",event,event.getAttempts());
    }


}
