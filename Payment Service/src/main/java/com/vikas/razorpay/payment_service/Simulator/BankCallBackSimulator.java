package com.vikas.razorpay.payment_service.Simulator;


import com.vikas.razorpay.commonlib.enums.ChaosMode;
import com.vikas.razorpay.commonlib.enums.PaymentStatus;
import com.vikas.razorpay.commonlib.util.RandomizerUtil;
import com.vikas.razorpay.payment_service.entity.Payment;
import com.vikas.razorpay.payment_service.repository.PaymentRepository;
import com.vikas.razorpay.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallBackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;


    @Scheduled(fixedDelayString="${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks(){
        LocalDateTime globalWindow=LocalDateTime.now().minusSeconds(1);
        List<Payment> candidates=paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,globalWindow);
        if(candidates.isEmpty()) return;
        for(Payment payment:candidates){
              simulatorConfig(payment);
        }
    }



    private void simulatorConfig(Payment payment){
        SimulatorConfig.MethodSimulatorConfig methodConfig= simulatorConfig.configFor(payment.getMethod());
        LocalDateTime dueAt=dueAt(payment,methodConfig);

        if(LocalDateTime.now().isBefore(dueAt)){
            return;
        }
        ChaosMode chaosMode=simulatorConfig.getChaosMode();
        switch(chaosMode){
            case SUCCESS -> resolve(payment,true);
            case FAILURE -> resolve(payment,false);
            case TIMEOUT -> {
                log.info("BankCallback Simulator: Payment Time Out");
            }
            case NORMAL,SLOW -> {
                resolve(payment,shouldApprove(payment,methodConfig));
            }


        }


    }

    private void resolve(Payment payment,boolean approve){
             if(approve){
                String bankRef="SIM_BANK_REF"+ RandomizerUtil.randomBase64(8);
                paymentService.resolveAuthorization(payment.getId(),true,bankRef,null,null);
             }else{
                 paymentService.resolveAuthorization(payment.getId(),false,null,"SIM_BANK_ERROR_CODE","Simulation Bank Declined");
             }
    }

    private boolean shouldApprove(Payment payment,SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){
        int bucket=Math.abs(payment.getId().hashCode())%100;
        return bucket<methodSimulatorConfig.getSuccessRate();
    }

    private LocalDateTime dueAt(Payment payment,SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){
        int range=methodSimulatorConfig.getMaxDelaySeconds()- methodSimulatorConfig.getMinDelaySeconds();
        int delaySeconds=methodSimulatorConfig.getMinDelaySeconds()+Math.abs(payment.getId().hashCode())%(range+1);
        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW){
            delaySeconds*=2;
        }
        return payment.getCreatedAt().plusSeconds(delaySeconds);

    }


}
