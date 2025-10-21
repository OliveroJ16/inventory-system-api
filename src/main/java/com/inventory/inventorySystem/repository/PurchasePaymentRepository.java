package com.inventory.inventorySystem.repository;

import com.inventory.inventorySystem.model.PurchasePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchasePaymentRepository extends JpaRepository <PurchasePayment, UUID> {
}
