package dev.dkc.expensetracker.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    private String userId;

    private String name;

    private String email;

    private String password;

    private List<String> categories;

    private List<String> parentCategories;

    public Map<String, Object> toMap() {
        return Map.of(
            "userId", userId,
            "name", name,
            "email", email,
            "categories", categories,
            "parentCategories", parentCategories
        );
    }
}
