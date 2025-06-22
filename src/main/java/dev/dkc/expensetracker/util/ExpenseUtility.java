package dev.dkc.expensetracker.util;

import java.time.LocalDate;
import java.util.Map;

import dev.dkc.expensetracker.model.Expense;
import dev.dkc.expensetracker.model.User;

public class ExpenseUtility {
    
    public static Map<?,?> isValidExpense(Expense expense, User user) {
        if (!isValidAmount(expense.getAmount())) {
            return Map.of("msg", "Amount must be greater than zero");
        }

        if (!isValidCategory(expense.getCategory())) {
            return Map.of("msg", "Category is required");
        } else if (!user.getCategories().contains(expense.getCategory())) {
            return Map.of("msg", "Invalid category");
        }

        if (!isValidDate(expense.getDate())) {
            expense.setDate(LocalDate.now());
        }

        if (!isValidParentCategory(expense.getParentCategory())) {
            expense.setParentCategory("None");
        } else if (!user.getParentCategories().contains(expense.getParentCategory())) {
            return Map.of("msg", "Invalid parent category");
        }

        if (!isValidDescription(expense.getDescription())) {
            expense.setDescription("None");
        }

        return Map.of("expense", expense);
    }

    public static boolean isValidCategory(String category) {
        return category != null && !category.isEmpty();
    }
    public static boolean isValidParentCategory(String parentCategory) {
        return parentCategory != null && !parentCategory.isEmpty();
    }
    public static boolean isValidDescription(String description) {
        return description != null && !description.isEmpty();
    }
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }
    public static boolean isValidDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }
}
