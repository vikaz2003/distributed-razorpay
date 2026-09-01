package com.vikas.razorpay.operations_service.repository;


import com.vikas.razorpay.commonlib.enums.OutBoxStatus;

import com.vikas.razorpay.operations_service.entity.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutBoxEventRepository extends JpaRepository<OutBoxEvent, UUID> {



    List<OutBoxEvent> findByOutBoxStatusOrderByCreatedAtAsc(OutBoxStatus status);

}
