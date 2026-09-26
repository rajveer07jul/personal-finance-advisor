package com.rajveer.finance.expense.specification;

import com.rajveer.finance.expense.dto.ExpenseFilterRequest;
import com.rajveer.finance.expense.entity.Expense;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class ExpenseSpecification {

    private ExpenseSpecification() {
    }

    public static Specification<Expense> belongsToUser(
            Long userId
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    public static Specification<Expense> hasFilters(
            ExpenseFilterRequest filter
    ) {
        Specification<Expense> specification =
                Specification.unrestricted();

        if (filter.startDate() != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("expenseDate"),
                                    filter.startDate()
                            )
            );
        }

        if (filter.endDate() != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("expenseDate"),
                                    filter.endDate()
                            )
            );
        }

        if (filter.category() != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("category"),
                                    filter.category()
                            )
            );
        }

        if (filter.paymentMethod() != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("paymentMethod"),
                                    filter.paymentMethod()
                            )
            );
        }

        if (filter.minimumAmount() != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("amount"),
                                    filter.minimumAmount()
                            )
            );
        }

        if (filter.maximumAmount() != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("amount"),
                                    filter.maximumAmount()
                            )
            );
        }

        if (filter.search() != null &&
                !filter.search().isBlank()) {

            String searchValue =
                    "%"
                            + filter.search()
                            .trim()
                            .toLowerCase(Locale.ROOT)
                            + "%";

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.or(
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("title")
                                            ),
                                            searchValue
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("merchantName")
                                            ),
                                            searchValue
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("description")
                                            ),
                                            searchValue
                                    )
                            )
            );
        }

        return specification;
    }

    public static Specification<Expense> build(
            Long userId,
            ExpenseFilterRequest filter
    ) {
        return belongsToUser(userId)
                .and(hasFilters(filter));
    }
}