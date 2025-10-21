package com.inventory.inventorySystem.service;

import com.inventory.inventorySystem.dto.request.PurchaseDetailRequest;
import com.inventory.inventorySystem.dto.response.PurchaseDetailResponse;
import com.inventory.inventorySystem.mapper.interfaces.PurchaseDetailMapper;
import com.inventory.inventorySystem.model.Article;
import com.inventory.inventorySystem.model.Purchase;
import com.inventory.inventorySystem.model.PurchaseDetail;
import com.inventory.inventorySystem.repository.PurchaseDetailRepository;
import com.inventory.inventorySystem.service.interfaces.ArticleService;
import com.inventory.inventorySystem.service.interfaces.PurchaseDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseDetailServiceImpl implements PurchaseDetailService {

    private final ArticleService articleService;
    private final PurchaseDetailMapper purchaseDetailMapper;
    private final PurchaseDetailRepository purchaseDetailRepository;

    @Override
    @Transactional
    public List<PurchaseDetailResponse> registerPurchaseDetail(List<PurchaseDetailRequest> purchaseDetailRequest, Purchase purchase){
        var details = new ArrayList<PurchaseDetail>();

        purchaseDetailRequest.forEach( detailRequest -> {
            Article article = articleService.updateStock(detailRequest.articleId(), detailRequest.quantity());
            details.add(purchaseDetailMapper.toEntity(detailRequest, article, purchase));
        });

        purchaseDetailRepository.saveAll(details);
        return details.stream()
                .map(purchaseDetailMapper::toDto)
                .toList();
    }


}
