package com.vikas.razorpay.operations_service.settlement;


import com.vikas.razorpay.commonlib.enums.SettlementStatus;
import com.vikas.razorpay.operations_service.entity.Settlement;
import com.vikas.razorpay.operations_service.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankSettlementCallBackSimulator {

    private SettlementRepository settlementRepository;
    private final SettlementTransactionExecutor settlementTransactionExecutor;

    @Scheduled(fixedDelayString = "5000")
    public void processCallbacks(){
        List<Settlement> settlements=settlementRepository.findByStatus(SettlementStatus.TRANSFER_PENDING);
        if(settlements.isEmpty()) return;

        for(Settlement settlement:settlements){
            simulateCallback(settlement);
        }

    }

    private void simulateCallback(Settlement settlement){
        log.info("Initiating settlement calback for settlementId : {}",settlement.getId());
        settlementTransactionExecutor.resolveTransfer(settlement.getId(),null,null);
    }
}
