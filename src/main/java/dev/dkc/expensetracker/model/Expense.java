package dev.dkc.expensetracker.model;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Expense {
    @Id
    private String id;
    @Indexed
    private String userId;
    private String category;
    private String parentCategory;
    private String description;
    private double amount;
    private LocalDate date;

    public Map<String, Object> toMap() {
        return Map.of(
            "id", id,
            "userId", userId,
            "category", category,
            "parentCategory", parentCategory,
            "description", description,
            "amount", amount,
            "date", date
        );
    }
}
