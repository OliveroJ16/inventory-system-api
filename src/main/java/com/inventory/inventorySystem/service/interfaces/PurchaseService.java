package com.inventory.inventorySystem.service.interfaces;

import com.inventory.inventorySystem.dto.request.PurchaseRequest;
import com.inventory.inventorySystem.dto.response.CompletePurchaseResponse;

public interface PurchaseService {
    CompletePurchaseResponse registerPurchase(PurchaseRequest purchaseRequest);
}
