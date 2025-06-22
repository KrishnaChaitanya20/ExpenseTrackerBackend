package dev.dkc.expensetracker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.dkc.expensetracker.model.Expense;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    
    // public Double sumByUserId(String userId);
    // public Double sumByUserIdAndCategory(String userId, String category);
    // public Double sumByUserIdAndParentCategory(String userId, String parentCategory);


    public List<Expense> findByUserId(String userId);
    public List<Expense> findByUserIdAndCategory(String userId, String category);
    public List<Expense> findByUserIdAndParentCategory(String userId, String parentCategory);
    public List<Expense> findByUserIdAndDateBetween(String userId, String startDate, String endDate);

}