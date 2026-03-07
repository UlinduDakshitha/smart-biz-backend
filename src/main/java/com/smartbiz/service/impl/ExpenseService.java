package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.entity.Expense;
import com.smartbiz.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BusinessContextService businessContextService;

    public List<Expense> getAll() {
        return expenseRepository.findByBusiness_BusinessId(businessContextService.getCurrentBusinessId());
    }

    public Expense getById(Integer id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        validateOwnership(expense);
        return expense;
    }

    public Expense create(Expense expense) {
        Business business = businessContextService.getCurrentBusiness();
        expense.setBusiness(business);
        return expenseRepository.save(expense);
    }

    public Expense update(Integer id, Expense updated) {
        Expense existing = getById(id);
        existing.setDescription(updated.getDescription());
        existing.setAmount(updated.getAmount());
        existing.setDate(updated.getDate());
        return expenseRepository.save(existing);
    }

    public void delete(Integer id) {
        Expense expense = getById(id);
        expenseRepository.delete(expense);
    }

    private void validateOwnership(Expense expense) {
        Integer businessId = businessContextService.getCurrentBusinessId();
        if (!expense.getBusiness().getBusinessId().equals(businessId)) {
            throw new RuntimeException("Access denied");
        }
    }
}
