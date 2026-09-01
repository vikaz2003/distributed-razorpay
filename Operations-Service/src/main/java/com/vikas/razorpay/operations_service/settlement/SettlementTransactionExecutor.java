package com.vikas.razorpay.operations_service.settlement;


import com.vikas.razorpay.commonlib.dto.PaymentSettlementView;
import com.vikas.razorpay.commonlib.dto.SettlementBankDetails;
import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.commonlib.enums.EventAggregateType;
import com.vikas.razorpay.commonlib.enums.SettlementStatus;
import com.vikas.razorpay.commonlib.exception.ResourceNotFoundException;
import com.vikas.razorpay.operations_service.clients.MerchantServiceClient;
import com.vikas.razorpay.operations_service.clients.PaymentServiceClient;
import com.vikas.razorpay.operations_service.entity.Settlement;
import com.vikas.razorpay.operations_service.entity.SettlementPayment;
import com.vikas.razorpay.operations_service.entity.SettlementPaymentId;
import com.vikas.razorpay.operations_service.outbox.OutBoxEventPublisher;
import com.vikas.razorpay.operations_service.repository.SettlePaymentRepository;
import com.vikas.razorpay.operations_service.repository.SettlementRepository;
import com.vikas.razorpay.operations_service.settlement.dto.BankTransferResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettlementTransactionExecutor {

    private static final double FEE_RATE=0.02;
    private static final double GST_RATE=0.18;
    private final SettlementRepository settlementRepository;
    private final SettlePaymentRepository settlePaymentRepository;
    private final MerchantServiceClient merchantServiceClient;
    private final BankTransferProcessor bankTransferProcessor;
    private final OutBoxEventPublisher outBoxEventPublisher;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public void processForMerchant(UUID merchantId, LocalDate settlementDate){




            List<PaymentSettlementView> unsettledPayments = paymentServiceClient.findUnsettledCaptured(merchantId);

            if (unsettledPayments.isEmpty()) return;

            log.info("Processing {} unsettled payments for merchantId: {} on {} date",unsettledPayments.size(),merchantId,settlementDate);


            Integer grossAmount = unsettledPayments.stream()
                    .map(paymentSettlementView -> paymentSettlementView.amountUnits())
                    .reduce(Integer::sum)
                    .orElseThrow();
            Money gross=Money.of(grossAmount,unsettledPayments.getFirst().currency());
            int fee = Math.toIntExact(Math.round(gross.getAmountUnits() * FEE_RATE));
            int gst = Math.toIntExact(Math.round(fee * GST_RATE));
            Money feeAmount = Money.of(fee, gross.getCurrency());
            Money gstAmount = Money.of(gst, gross.getCurrency());
            Money netAmount = gross.subtract(feeAmount).subtract(gstAmount);

            Settlement settlement = Settlement.builder()
                    .merchantId(merchantId)
                    .grossAmount(gross)
                    .feeAmount(feeAmount)
                    .gstAmount(gstAmount)
                    .netAmount(netAmount)
                    .status(SettlementStatus.INITIATED)
                    .build();

            settlementRepository.save(settlement);

            try{
            List<SettlementPayment> links = new ArrayList<>();
            for (PaymentSettlementView p : unsettledPayments) {
                links.add(SettlementPayment.builder()
                        .id(new SettlementPaymentId(settlement.getId(), p.paymentId()))
                        .settlement(settlement)
                        .build());
            }

            settlePaymentRepository.saveAll(links);


//
            SettlementBankDetails settlementBankDetails = merchantServiceClient.getSettlementBankDetails(merchantId);
            //call the bank transfer service to transfer the net amount to merchant settlement bank details
            BankTransferResult result = bankTransferProcessor.initiate(settlement.getId(), merchantId, netAmount, settlementBankDetails.accountNumber(), settlementBankDetails.ifsc());
            settlement.setBankReference(result.registrationRef());
            settlement.setStatus(SettlementStatus.TRANSFER_PENDING);


            settlementRepository.save(settlement);
        } catch (Exception e) {
                log.error("Settlement Failed for {} on {}",settlement.getId(),settlementDate);
            settlement.setStatus(SettlementStatus.FAILED);
            settlementRepository.save(settlement);

            throw new RuntimeException(e);
        }
    }


    @Transactional
    public void resolveTransfer(UUID settlementId,String errorCode,String errorDescription){
        Settlement settlement=settlementRepository.findById(settlementId).orElseThrow(() ->
                new ResourceNotFoundException("Settlement Not Found for id: "+settlementId,"Settlement"));
        if(settlement.getStatus()!=SettlementStatus.TRANSFER_PENDING){
            log.info("Settlement Resolved, skipping for Settlement Id:{}",settlementId);
            return;
        }

        if(errorCode==null){
             //success
            settlement.setStatus(SettlementStatus.PROCESSED);
            settlement.setProcessedAt(LocalDateTime.now());
            settlementRepository.save(settlement);
            log.info("Settlement processed successfully, settlementId: {}",settlementId);
            outBoxEventPublisher.publish(EventAggregateType.SETTLEMENT,settlementId,"SETTLEMENT_PROCESSED", Map.of(
                    "settlementId",settlementId,
                    "merchantId",settlement.getMerchantId(),
                    "status",settlement.getStatus().name(),
                    "settlementAmount",settlement.getNetAmount().getAmountUnits(),
                    "settlementCurrency",settlement.getNetAmount().getCurrency()
            ));

            List<SettlementPayment> settlementPaymentList=settlePaymentRepository.findBySettlement(settlement);
            List<UUID> paymentIds=settlementPaymentList.stream().map(SettlementPayment::getId).map(SettlementPaymentId::getPaymentId).toList();
            paymentServiceClient.markSettled(paymentIds);
        }else{
            //failed
            settlement.setStatus(SettlementStatus.FAILED);
            settlement.setFailureReason(errorCode+" : "+errorDescription);
            settlementRepository.save(settlement);
            log.warn("Settlement failed, settlementId: {}",settlementId);
            outBoxEventPublisher.publish(EventAggregateType.SETTLEMENT,settlementId,"SETTLEMENT_FAILED",Map.of(
                    "settlementId",settlementId,
                    "merchantId",settlement.getMerchantId(),
                    "status",settlement.getStatus().name(),
                    "settlementAmount",settlement.getNetAmount().getAmountUnits(),
                    "settlementCurrency",settlement.getNetAmount().getCurrency()
            ));
        }
    }
}
