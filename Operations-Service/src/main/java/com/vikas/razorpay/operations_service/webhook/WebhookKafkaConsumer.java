package com.vikas.razorpay.operations_service.webhook;


import com.vikas.razorpay.commonlib.dto.WebhookTarget;
import com.vikas.razorpay.commonlib.enums.WebhookEventStatus;
import com.vikas.razorpay.commonlib.util.SignerUtil;
import com.vikas.razorpay.operations_service.clients.MerchantServiceClient;
import com.vikas.razorpay.operations_service.entity.WebhookEvent;
import com.vikas.razorpay.operations_service.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {

    private final MerchantServiceClient merchantServiceClient;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;
    private final WebhookDlqRecorder dlqRecorder;


    @KafkaListener(topics = {
            "${app.kafka.topics.payment:payments.events}",
            "${app.kafka.topics.order:orders.events}",
            "${app.kafka.topics.refund:refund.events}",
            "${app.kafka.topics.settlement:settlements.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String,Object>> record,Acknowledgment acknowledgment ){
        try {
            Map<String, Object> envelope = record.value();
            Map<String, Object> data = (Map<String, Object>) envelope.get("data");
            String eventType = (String) envelope.get("eventType");
            Object merchantIdRaw = data.get("merchantId");
            if (merchantIdRaw == null) {
                log.warn("No Merchant Id was found,skipping the event: {}", eventType);
                acknowledgment.acknowledge();
                return;
            }
            UUID merchantId = UUID.fromString(merchantIdRaw.toString());
            List<WebhookTarget> targets = merchantServiceClient.getActiveConfigsForEvent(merchantId,eventType);
            if (targets.isEmpty()) {
                log.debug("No webhooktarget was found,skipping event: {}", eventType);
                acknowledgment.acknowledge();
                return;
            }
            Map<String, Object> signatureData = Map.of("event", eventType, "payload", data);
            String signatureJson = objectMapper.writeValueAsString(signatureData);
            for (WebhookTarget target : targets) {
                String signature = signerUtil.sign(signatureJson, target.webhookSecret());
                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .merchantId(merchantId)
                        .eventType(eventType)
                        .paylod(data)
                        .targetUrl(target.targetUrl())
                        .signature(signature)
                        .status(WebhookEventStatus.PENDING)
                        .nextRetryAt(LocalDateTime.now())
                        .build();
                webhookEvent = webhookEventRepository.save(webhookEvent);
                // redisQueue.enqueue(webhookEvent.getId)
                webhookRetryQueue.enqueue(webhookEvent.getId(), webhookEvent.getNextRetryAt());
            }
            acknowledgment.acknowledge();
        }catch(DataAccessException | CannotCreateTransactionException dbDown){
            log.error("Webhook consumer failed due to DB down, could not process the record, offset:{}",record.offset());

        }catch(Exception logicError){
            dlqRecorder.recordConsumerFailed(record,logicError.getMessage());
            acknowledgment.acknowledge();
        }
    }

}
