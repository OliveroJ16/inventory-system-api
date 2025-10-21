package com.inventory.inventorySystem.service;

import com.inventory.inventorySystem.dto.request.PurchaseRequest;
import com.inventory.inventorySystem.dto.response.CompletePurchaseResponse;
import com.inventory.inventorySystem.dto.response.PurchaseDetailResponse;
import com.inventory.inventorySystem.enums.PaymentType;
import com.inventory.inventorySystem.enums.SaleStatus;
import com.inventory.inventorySystem.exceptions.ResourceNotFoundException;
import com.inventory.inventorySystem.mapper.interfaces.PurchaseMapper;
import com.inventory.inventorySystem.model.Purchase;
import com.inventory.inventorySystem.model.Supplier;
import com.inventory.inventorySystem.model.User;
import com.inventory.inventorySystem.repository.PurchaseRepository;
import com.inventory.inventorySystem.repository.SupplierRepository;
import com.inventory.inventorySystem.repository.UserRepository;
import com.inventory.inventorySystem.service.interfaces.PurchaseDetailService;
import com.inventory.inventorySystem.service.interfaces.PurchaseService;
import com.inventory.inventorySystem.utils.StringNormalizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final SupplierRepository supplierRepository;
    private final PurchaseMapper purchaseMapper;
    private final PurchaseDetailService purchaseDetailService;
    private final PurchaseRepository purchaseRepository;
    private final StringNormalizer stringNormalizer;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CompletePurchaseResponse registerPurchase(PurchaseRequest purchaseRequest){
        User user = userRepository.findById(purchaseRequest.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", purchaseRequest.userId()));

        Supplier supplier = supplierRepository.findById(purchaseRequest.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", purchaseRequest.supplierId()));

        Purchase purchase = purchaseMapper.toEntity(user, supplier);
        purchase.setTotalAmount(BigDecimal.ZERO);
        purchase.setStatus(SaleStatus.PAID);
        purchase = purchaseRepository.saveAndFlush(purchase);

        List<PurchaseDetailResponse> purchaseDetailResponses = purchaseDetailService.registerPurchaseDetail(purchaseRequest.details(), purchase);
        BigDecimal totalSale = calculateTotalAmount(purchaseDetailResponses);
        purchase.setTotalAmount(totalSale);

        return purchaseMapper.toDto(purchase, purchaseDetailResponses);
    }

    private BigDecimal calculateTotalAmount(List<PurchaseDetailResponse> detailResponses){
        return detailResponses.stream()
                .map(PurchaseDetailResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
