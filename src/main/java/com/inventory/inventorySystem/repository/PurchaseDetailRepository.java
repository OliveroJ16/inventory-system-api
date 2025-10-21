package com.inventory.inventorySystem.repository;

import com.inventory.inventorySystem.model.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetail, UUID> {
}
