package dev.dkc.expensetracker.repository;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import dev.dkc.expensetracker.dto.SumResultTO;

@Repository
public class CustomExpenseRepository {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public Double sumOfExpensesByUserId(String userId) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("userId").is(userId)),
            Aggregation.group("userId").sum("amount").as("total")
        );
        AggregationResults<SumResultTO> results = mongoTemplate.aggregate(aggregation, "expenses", SumResultTO.class);

        return results.getMappedResults().stream()
                .mapToDouble(SumResultTO::getTotal)
                .findFirst()
                .orElse(0.0);
    }

    public  Map<String,Double> sumOfExpensesOfUserIdByCategory(String userId) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("userId").is(userId)),
            Aggregation.group("category").sum("amount").as("total")
        );
        AggregationResults<SumResultTO> results = mongoTemplate.aggregate(aggregation, "expenses", SumResultTO.class);
        return results.getMappedResults().stream().collect(Collectors.toMap(
                SumResultTO::get_id,
                SumResultTO::getTotal
            ));
    }

    public  Map<String,Double> sumOfExpensesOfUserIdByParentCategory(String userId) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("userId").is(userId)),
            Aggregation.group("parentCategory").sum("amount").as("total")
        );
        AggregationResults<SumResultTO> results = mongoTemplate.aggregate(aggregation, "expenses", SumResultTO.class);
        return results.getMappedResults().stream().collect(Collectors.toMap(
                SumResultTO::get_id,
                SumResultTO::getTotal
            ));
    }
}
