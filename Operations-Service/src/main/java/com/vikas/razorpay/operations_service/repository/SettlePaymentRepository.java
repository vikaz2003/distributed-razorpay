package com.vikas.razorpay.operations_service.repository;


import com.vikas.razorpay.operations_service.entity.Settlement;
import com.vikas.razorpay.operations_service.entity.SettlementPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlePaymentRepository extends JpaRepository<SettlementPayment, UUID> {


    List<SettlementPayment> findBySettlement(Settlement settlement);
}
