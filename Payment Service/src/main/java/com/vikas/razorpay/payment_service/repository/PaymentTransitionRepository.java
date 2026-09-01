package com.vikas.razorpay.payment_service.repository;


import com.vikas.razorpay.payment_service.entity.PaymentTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentTransitionRepository extends JpaRepository<PaymentTransitionLog, UUID> {
}
