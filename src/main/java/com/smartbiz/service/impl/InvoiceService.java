package com.smartbiz.service.impl;

import com.smartbiz.dto.request.InvoiceRequest;
import com.smartbiz.entity.*;
import com.smartbiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BusinessContextService businessContextService;

    public List<Invoice> getAll() {
        return invoiceRepository.findByBusiness_BusinessId(businessContextService.getCurrentBusinessId());
    }

    public Invoice getById(Integer id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        validateOwnership(invoice);
        return invoice;
    }

    @Transactional
    public Invoice create(InvoiceRequest request) {
        Business business = businessContextService.getCurrentBusiness();

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Invoice invoice = Invoice.builder()
                .business(business)
                .customer(customer)
                .date(LocalDate.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceRequest.InvoiceItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQty() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal itemPrice = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(itemPrice);

            InvoiceItem item = InvoiceItem.builder()
                    .invoice(savedInvoice)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .price(product.getPrice())
                    .build();
            items.add(invoiceItemRepository.save(item));

            // Deduct stock
            product.setStockQty(product.getStockQty() - itemReq.getQuantity());
            productRepository.save(product);
        }

        savedInvoice.setTotalAmount(total);
        savedInvoice.setItems(items);
        return invoiceRepository.save(savedInvoice);
    }

    public void delete(Integer id) {
        Invoice invoice = getById(id);
        invoiceRepository.delete(invoice);
    }

    private void validateOwnership(Invoice invoice) {
        Integer businessId = businessContextService.getCurrentBusinessId();
        if (!invoice.getBusiness().getBusinessId().equals(businessId)) {
            throw new RuntimeException("Access denied");
        }
    }
}
