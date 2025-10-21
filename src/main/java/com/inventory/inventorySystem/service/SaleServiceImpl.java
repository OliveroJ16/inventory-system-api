package com.inventory.inventorySystem.service;

import com.inventory.inventorySystem.dto.request.SaleRequest;
import com.inventory.inventorySystem.dto.response.PaginatedResponse;
import com.inventory.inventorySystem.dto.response.SaleDetailResponse;
import com.inventory.inventorySystem.dto.response.CompleteSaleResponse;
import com.inventory.inventorySystem.dto.response.SaleResponse;
import com.inventory.inventorySystem.enums.SaleStatus;
import com.inventory.inventorySystem.exceptions.ResourceNotFoundException;
import com.inventory.inventorySystem.mapper.interfaces.SaleMapper;
import com.inventory.inventorySystem.model.Customer;
import com.inventory.inventorySystem.model.Sale;
import com.inventory.inventorySystem.model.User;
import com.inventory.inventorySystem.repository.CustomerRepository;
import com.inventory.inventorySystem.repository.SaleRepository;
import com.inventory.inventorySystem.repository.UserRepository;
import com.inventory.inventorySystem.service.interfaces.SaleDetailService;
import com.inventory.inventorySystem.service.interfaces.SaleService;
import com.inventory.inventorySystem.utils.StringNormalizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailService saleDetailService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SaleMapper saleMapper;
    private final StringNormalizer stringNormalizer;

    @Override
    @Transactional
    public CompleteSaleResponse registerSale(SaleRequest saleRequest) {
        User user = userRepository.findById(saleRequest.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", saleRequest.userId()));

        Customer customer = verifyCustomer(saleRequest.customerId());

        Sale sale = saleMapper.toEntity(user, customer);
        sale.setTotalSale(BigDecimal.ZERO);
        sale.setStatus(SaleStatus.PENDING);
        sale = saleRepository.saveAndFlush(sale);

        List<SaleDetailResponse> saleDetailResponses = saleDetailService.registerSaleDetail(saleRequest.details(), sale);
        BigDecimal totalSale = calculateTotalAmount(saleDetailResponses);
        sale.setTotalSale(totalSale);

        return saleMapper.toDto(sale, saleDetailResponses);
    }

    @Override
    public PaginatedResponse<SaleResponse> getAllSales(LocalDate startDate, LocalDate endDate, String customerName, Pageable pageable){
        Page<Sale> salePage;
        if(startDate != null && endDate != null){
            salePage = saleRepository.findByDateBetween(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), pageable);
        }else if (customerName != null){
            salePage = saleRepository.findByCustomerName(stringNormalizer.toTitleCase(customerName), pageable);
        }else {
            salePage = saleRepository.findAll(pageable);
        }
        Page<SaleResponse> saleResponses = salePage.map(saleMapper::toDto);
        return new PaginatedResponse<>(saleResponses);
    }

    private BigDecimal calculateTotalAmount(List<SaleDetailResponse> saleDetailResponses){
        return saleDetailResponses.stream()
                .map(SaleDetailResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Customer can be null → indicates that the sale does not have an associated customer.
     */
    private Customer verifyCustomer(UUID customerId){
        Customer customer = null;
        if(customerId != null){
            customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        }
        return customer;
    }
}
