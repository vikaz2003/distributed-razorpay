package com.vikas.razorpay.payment_service.outbox;


import com.vikas.razorpay.commonlib.enums.OutBoxStatus;
import com.vikas.razorpay.payment_service.entity.OutBoxEvent;
import com.vikas.razorpay.payment_service.repository.OutBoxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutBoxHandler {

    private final OutBoxEventRepository repo;
    private final Integer MAX_ATTEMPTS=3;


    @Transactional
    public void handleEventPublisher(OutBoxEvent event) {
        event.setOutBoxStatus(OutBoxStatus.PUBLISHED);
        repo.save(event);
    }

    @Transactional
    public void handleFailed(OutBoxEvent event,String message) {
        event.setRetries(event.getRetries()+1);
        event.setLastError(message.length()<1000?message:message.substring(0,1000));
        if(event.getRetries() >=  MAX_ATTEMPTS){
            event.setOutBoxStatus(OutBoxStatus.FAILED);
        }
        repo.save(event);


    }
}
