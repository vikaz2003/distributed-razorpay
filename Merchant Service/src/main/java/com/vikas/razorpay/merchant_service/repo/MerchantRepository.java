package com.vikas.razorpay.merchant_service.repo;


import com.vikas.razorpay.commonlib.enums.MerchantStatus;
import com.vikas.razorpay.merchant_service.Entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    boolean existsByEmail( String email);

    List<Merchant> findByStatus(MerchantStatus status);
}
