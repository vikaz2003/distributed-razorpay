package com.vikas.razorpay.vaultservice.repo;


import com.vikas.razorpay.vaultservice.entity.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface VaultRepo extends JpaRepository<VaultCard, UUID> {
}
