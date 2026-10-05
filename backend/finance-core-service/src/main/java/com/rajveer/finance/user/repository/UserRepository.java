package com.rajveer.finance.user.repository;

import com.rajveer.finance.common.enums.AccountStatus;
import com.rajveer.finance.common.enums.Role;
import com.rajveer.finance.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "profile")
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByAccountStatus(
            AccountStatus accountStatus
    );

    long countByRole(
            Role role
    );

    @EntityGraph(attributePaths = "profile")
    @Query("""
            SELECT u
            FROM User u
            LEFT JOIN u.profile p
            WHERE (
                :search IS NULL
                OR :search = ''
                OR LOWER(u.email) LIKE LOWER(
                    CONCAT('%', :search, '%')
                )
                OR LOWER(p.firstName) LIKE LOWER(
                    CONCAT('%', :search, '%')
                )
                OR LOWER(p.lastName) LIKE LOWER(
                    CONCAT('%', :search, '%')
                )
            )
            AND (
                :accountStatus IS NULL
                OR u.accountStatus = :accountStatus
            )
            AND (
                :role IS NULL
                OR u.role = :role
            )
            """)
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("accountStatus")
            AccountStatus accountStatus,
            @Param("role") Role role,
            Pageable pageable
    );
}