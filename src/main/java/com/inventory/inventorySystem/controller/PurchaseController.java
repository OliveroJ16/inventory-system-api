package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.request.PurchaseRequest;
import com.inventory.inventorySystem.dto.response.CompletePurchaseResponse;
import com.inventory.inventorySystem.service.interfaces.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchases")
@Tag(
        name = "Purchases",
        description = "Purchase registration and payment management"
)
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Register purchase",
            description = "Registers a new purchase. Requires ADMIN role."
    )
    public ResponseEntity<CompletePurchaseResponse> registerPurchase(@RequestBody PurchaseRequest purchaseRequest){
        CompletePurchaseResponse completePurchaseResponse = purchaseService.registerPurchase(purchaseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(completePurchaseResponse);
    }
}
