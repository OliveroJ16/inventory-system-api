package com.inventory.inventorySystem.service;

import com.inventory.inventorySystem.dto.request.PurchasePaymentRequest;
import com.inventory.inventorySystem.dto.response.PurchasePaymentResponse;
import com.inventory.inventorySystem.exceptions.ResourceNotFoundException;
import com.inventory.inventorySystem.mapper.interfaces.PurchasePaymentMapper;
import com.inventory.inventorySystem.model.Purchase;
import com.inventory.inventorySystem.model.PurchasePayment;
import com.inventory.inventorySystem.repository.PurchasePaymentRepository;
import com.inventory.inventorySystem.repository.PurchaseRepository;
import com.inventory.inventorySystem.service.interfaces.PurchasePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchasePaymentServiceImpl implements PurchasePaymentService {

    private final PurchasePaymentRepository purchasePaymentRepository;
    private final PurchasePaymentMapper purchasePaymentMapper;
    private final PurchaseRepository purchaseRepository;

    @Override
    public PurchasePaymentResponse savePurchasePayment(PurchasePaymentRequest purchasePaymentRequest, UUID idPurchase){
        Purchase purchase = purchaseRepository.findById(idPurchase)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", idPurchase));
        PurchasePayment purchasePayment = purchasePaymentMapper.toEntity(purchasePaymentRequest, purchase);
        PurchasePayment purchasePaymentSaved = purchasePaymentRepository.save(purchasePayment);
        return purchasePaymentMapper.toDto(purchasePaymentSaved);
    }
}
