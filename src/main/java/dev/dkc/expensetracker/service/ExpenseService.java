package dev.dkc.expensetracker.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dkc.expensetracker.model.Expense;
import dev.dkc.expensetracker.model.User;
import dev.dkc.expensetracker.repository.CustomExpenseRepository;
import dev.dkc.expensetracker.repository.ExpenseRepository;
import dev.dkc.expensetracker.util.ExpenseUtility;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CustomExpenseRepository customExpenseRepository;

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    public Expense updateExpense(Expense expense, User user) {

        Expense existingExpense = expenseRepository.findById(expense.getId()).orElse(null);
        if (existingExpense == null) {
            return null;
        }

        Expense copyExistingExpense = Expense.builder()
                .id(existingExpense.getId())
                .userId(existingExpense.getUserId())
                .category(existingExpense.getCategory())
                .parentCategory(existingExpense.getParentCategory())
                .description(existingExpense.getDescription())
                .amount(existingExpense.getAmount())
                .date(existingExpense.getDate())
                .build();

        if( ExpenseUtility.isValidAmount(expense.getAmount())){
            existingExpense.setAmount(expense.getAmount());
        }
        if (ExpenseUtility.isValidCategory(expense.getCategory()) &&
            (expense.getCategory().equals("None") || user.getCategories().contains(expense.getCategory()))) {
            existingExpense.setCategory(expense.getCategory());
        }
        if (ExpenseUtility.isValidParentCategory(expense.getParentCategory()) &&
            (expense.getParentCategory().equals("None") || user.getParentCategories().contains(expense.getParentCategory()))) {
            existingExpense.setParentCategory(expense.getParentCategory());
        }
        if (ExpenseUtility.isValidDescription(expense.getDescription())) {
            existingExpense.setDescription(expense.getDescription());
        }
        if (ExpenseUtility.isValidDate(expense.getDate())) {
            existingExpense.setDate(expense.getDate());
        }
        if (existingExpense.equals(copyExistingExpense)) {
            return null; // No changes made
        }
        return expenseRepository.save(existingExpense);
    }

    public List<Expense> getExpensesByUserId(String userId) {
        return expenseRepository.findByUserId(userId);
    }

    public boolean deleteExpenseById(String expenseId, String userId) {
        Expense expense = expenseRepository.findById(expenseId).orElse(null);
        if (expense == null || !expense.getUserId().equals(userId)) {
            return false;
        }
        expenseRepository.delete(expense);
        return true;
    }
    
    public boolean deleteExpensesOfUserId(String userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        if (expenses.isEmpty()) 
            return false;
        expenseRepository.deleteAll(expenses);
        return true;
    }
    
    public Double getTotalExpensesOfUserId(String userId) {
        return customExpenseRepository.sumOfExpensesByUserId(userId);
    }

    public Map<String, Double> getTotalExpensesOfUserByCategory(String userId) {
       return  customExpenseRepository.sumOfExpensesOfUserIdByCategory(userId);
    }

    public Map<String, Double> getTotalExpensesOfUserByParentCategory(String userId) {
        return  customExpenseRepository.sumOfExpensesOfUserIdByParentCategory(userId);
    }



}
