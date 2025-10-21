package com.inventory.inventorySystem.service.interfaces;

import com.inventory.inventorySystem.dto.request.PurchaseDetailRequest;
import com.inventory.inventorySystem.dto.response.PurchaseDetailResponse;
import com.inventory.inventorySystem.model.Purchase;

import java.util.List;

public interface PurchaseDetailService {
    List<PurchaseDetailResponse> registerPurchaseDetail(List<PurchaseDetailRequest> purchaseDetailRequest, Purchase purchase);
}
