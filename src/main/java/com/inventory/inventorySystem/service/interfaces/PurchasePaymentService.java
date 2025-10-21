package com.inventory.inventorySystem.service.interfaces;

import com.inventory.inventorySystem.dto.request.PurchasePaymentRequest;
import com.inventory.inventorySystem.dto.response.PurchasePaymentResponse;

import java.util.UUID;

public interface PurchasePaymentService {
    PurchasePaymentResponse savePurchasePayment(PurchasePaymentRequest purchasePaymentRequest, UUID idPurchase);
}
