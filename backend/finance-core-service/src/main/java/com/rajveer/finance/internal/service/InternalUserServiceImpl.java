package com.rajveer.finance.internal.service;

import com.rajveer.finance.budget.repository.BudgetRepository;
import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.GoalStatus;
import com.rajveer.finance.common.enums.Role;
import com.rajveer.finance.common.response.PagedResponse;
import com.rajveer.finance.exception.BusinessRuleException;
import com.rajveer.finance.exception.ResourceNotFoundException;
import com.rajveer.finance.expense.repository.ExpenseRepository;
import com.rajveer.finance.goal.repository.FinancialGoalRepository;
import com.rajveer.finance.internal.dto.InternalPlatformSummaryResponse;
import com.rajveer.finance.internal.dto.InternalUserResponse;
import com.rajveer.finance.internal.dto.InternalUserStatusRequest;
import com.rajveer.finance.portfolio.repository.PortfolioAssetRepository;
import com.rajveer.finance.user.entity.User;
import com.rajveer.finance.user.entity.UserProfile;
import com.rajveer.finance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternalUserServiceImpl
        implements InternalUserService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final FinancialGoalRepository goalRepository;
    private final PortfolioAssetRepository
            portfolioAssetRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InternalUserResponse> getUsers(
            String search,
            AccountStatus accountStatus,
            Role role,
            Pageable pageable
    ) {
        String normalizedSearch =
                normalizeSearch(search);

        Page<InternalUserResponse> responsePage =
                userRepository.searchUsers(
                                normalizedSearch,
                                accountStatus,
                                role,
                                pageable
                        )
                        .map(this::toResponse);

        return PagedResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public InternalUserResponse getUserById(
            Long userId
    ) {
        User user = findUserById(userId);

        return toResponse(user);
    }

    @Override
    @Transactional
    public InternalUserResponse updateUserStatus(
            Long userId,
            InternalUserStatusRequest request
    ) {
        User user = findUserById(userId);

        if (user.getAccountStatus()
                == request.accountStatus()) {
            throw new BusinessRuleException(
                    "User account already has status "
                            + request.accountStatus()
            );
        }

        if (user.getRole() == Role.ADMIN &&
                request.accountStatus()
                        != AccountStatus.ACTIVE) {
            throw new BusinessRuleException(
                    "An administrator account cannot be "
                            + "disabled through this endpoint"
            );
        }

        user.setAccountStatus(
                request.accountStatus()
        );

        User savedUser =
                userRepository.save(user);

        return toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public InternalPlatformSummaryResponse
    getPlatformSummary() {
        return new InternalPlatformSummaryResponse(
                userRepository.count(),
                userRepository.countByAccountStatus(
                        AccountStatus.ACTIVE
                ),
                userRepository.countByAccountStatus(
                        AccountStatus.INACTIVE
                ),
                userRepository.countByAccountStatus(
                        AccountStatus.SUSPENDED
                ),
                userRepository.countByAccountStatus(
                        AccountStatus.LOCKED
                ),
                userRepository.countByRole(
                        Role.ADMIN
                ),
                userRepository.countByRole(
                        Role.USER
                ),
                expenseRepository.count(),
                budgetRepository.count(),
                goalRepository.count(),
                goalRepository.countByStatus(
                        GoalStatus.COMPLETED
                ),
                portfolioAssetRepository.count()
        );
    }

    private InternalUserResponse toResponse(
            User user
    ) {
        UserProfile profile = user.getProfile();

        String firstName =
                profile == null
                        ? null
                        : profile.getFirstName();

        String lastName =
                profile == null
                        ? null
                        : profile.getLastName();

        String fullName = buildFullName(
                firstName,
                lastName
        );

        return new InternalUserResponse(
                user.getId(),
                user.getEmail(),
                firstName,
                lastName,
                fullName,
                user.getRole(),
                user.getAccountStatus(),
                user.isEmailVerified(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private String buildFullName(
            String firstName,
            String lastName
    ) {
        if (firstName == null &&
                lastName == null) {
            return null;
        }

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }

    private User findUserById(
            Long userId
    ) {
        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        )
                );
    }

    private String normalizeSearch(
            String search
    ) {
        if (search == null ||
                search.isBlank()) {
            return null;
        }

        return search.trim();
    }
}
