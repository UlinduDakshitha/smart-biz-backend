package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.entity.Customer;
import com.smartbiz.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BusinessContextService businessContextService;

    public List<Customer> getAll() {
        return customerRepository.findByBusiness_BusinessId(businessContextService.getCurrentBusinessId());
    }

    public Customer getById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        validateOwnership(customer);
        return customer;
    }

    public Customer create(Customer customer) {
        Business business = businessContextService.getCurrentBusiness();
        customer.setBusiness(business);
        return customerRepository.save(customer);
    }

    public Customer update(Integer id, Customer updated) {
        Customer existing = getById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        return customerRepository.save(existing);
    }

    public void delete(Integer id) {
        Customer customer = getById(id);
        customerRepository.delete(customer);
    }

    private void validateOwnership(Customer customer) {
        Integer businessId = businessContextService.getCurrentBusinessId();
        if (!customer.getBusiness().getBusinessId().equals(businessId)) {
            throw new RuntimeException("Access denied");
        }
    }
}
