package dev.dkc.expensetracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dkc.expensetracker.model.Expense;
import dev.dkc.expensetracker.model.User;
import dev.dkc.expensetracker.service.ExpenseService;
import dev.dkc.expensetracker.util.ExpenseUtility;
import dev.dkc.expensetracker.util.SessionManager;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // @Autowired
    // private UserService userService;
    @GetMapping
    public ResponseEntity<?> getExpensesByUserId(@CookieValue(value = "sessionId") String sessionId) {
        String userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Unauthorized or session expired"));
        }

        List<Expense> expenses = expenseService.getExpensesByUserId(userId);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody Expense expense,
            @CookieValue(value = "sessionId") String sessionId) {

        User user = SessionManager.getUser(sessionId);
        if (user == null || user.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("msg", "Unauthorized or session expired"));
        }

        String userId = user.getUserId();
        expense.setUserId(userId);
        Map<?, ?> validation = ExpenseUtility.isValidExpense(expense, user);

        if (validation.containsKey("msg")) {
           return ResponseEntity.badRequest().body(validation);
        } else {
           expense = (Expense) validation.get("expense");
        }

        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "msg", "Expense created successfully",
                "expense", createdExpense.toMap()));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<?> updateExpense(@PathVariable String expenseId, @RequestBody Expense expense,
            @CookieValue(value = "sessionId") String sessionId) {

        User user = SessionManager.getUser(sessionId);
        if (user == null || user.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Unauthorized or session expired"));
        }

        String userId = user.getUserId();
        expense.setUserId(userId);
        expense.setId(expenseId);
        Expense updatedExpense = expenseService.updateExpense(expense, user);
        if(updatedExpense == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("msg", "Expense not found, does not belong to the user or Invalid data"));
        }
        return ResponseEntity.ok(Map.of(
                "msg", "Expense updated successfully",
                "expense", updatedExpense.toMap()));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserExpenses(@CookieValue(value = "sessionId") String sessionId) {
        String userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Unauthorized or session expired"));
        }
        boolean deleted = expenseService.deleteExpensesOfUserId(userId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", "No expenses found to delete"));
        }
        return ResponseEntity.ok(Map.of("msg", "Expenses deleted successfully"));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable String expenseId,
            @CookieValue(value = "sessionId") String sessionId) {
        String userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Unauthorized or session expired"));
        }

        boolean deleted = expenseService.deleteExpenseById(expenseId, userId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "Expense not found or does not belong to the user"));
        }
        return ResponseEntity.ok(Map.of("msg", "Expense deleted successfully"));
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotalExpenses(@CookieValue(value = "sessionId") String sessionId) {
        String userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Unauthorized or session expired"));
        }

        Double total = expenseService.getTotalExpensesOfUserId(userId);
        return ResponseEntity.ok(Map.of("total", total));
    }


    @GetMapping("/category/total")
    public ResponseEntity<?> getTotalExpensesByCategory( @CookieValue(value = "sessionId") String sessionId) {
        String userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Unauthorized or session expired"));
        }

        Map<String, Double> resMap = expenseService.getTotalExpensesOfUserByCategory(userId);
        return ResponseEntity.ok(resMap);
    }


    @GetMapping("/parentcategory/total")
    public ResponseEntity<?> getTotalExpensesByParentCategory( @CookieValue(value = "sessionId") String sessionId) {
        String userId = SessionManager.getUserId(sessionId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("msg", "Unauthorized or session expired"));
        }

        Map<String, Double> resMap = expenseService.getTotalExpensesOfUserByParentCategory(userId);
        return ResponseEntity.ok(resMap);
    }
    
}