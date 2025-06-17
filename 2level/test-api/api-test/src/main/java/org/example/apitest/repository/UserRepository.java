package org.example.apitest.repository;

import org.example.apitest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ==================== AUTHENTICATION METHODS ====================

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find user by username for login
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email for login/reset password
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email for flexible login
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    // ==================== PROFILE MANAGEMENT METHODS ====================

    /**
     * Check if username exists excluding current user (for profile update)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :userId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("userId") Long userId);

    /**
     * Check if email exists excluding current user (for profile update)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);

    // ==================== SEARCH METHODS ====================

    /**
     * Search users by username containing keyword
     */
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Search users by email containing keyword
     */
    List<User> findByEmailContainingIgnoreCase(String email);

    /**
     * Search users by username or email containing keyword
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> findByUsernameOrEmailContaining(@Param("keyword") String keyword);

    /**
     * Search users with pagination
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> findByUsernameOrEmailContaining(@Param("keyword") String keyword, Pageable pageable);

    // ==================== ADMIN METHODS ====================

    /**
     * Get all users with pagination
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Find users by status (if you have status field)
     */
    // List<User> findByStatus(UserStatus status);

    /**
     * Count total users
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    /**
     * Find recently registered users
     */


    // ==================== SECURITY METHODS ====================

    /**
     * Find users by email for password reset
     */


    /**
     * Find users with failed login attempts (if you track this)
     */
    // @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :maxAttempts")
    // List<User> findUsersWithFailedAttempts(@Param("maxAttempts") int maxAttempts);

    // ==================== REPORTING METHODS ====================

    /**
     * Count users registered in date range
     */

    /**
     * Get user registration statistics by month
     */


    // ==================== CUSTOM QUERY METHODS ====================

    /**
     * Find top active users (if you have activity tracking)
     */
    // @Query("SELECT u FROM User u ORDER BY u.lastLoginAt DESC")
    // List<User> findTopActiveUsers(Pageable pageable);

    /**
     * Complex search with multiple criteria
     */


    // ==================== BULK OPERATIONS ====================

    /**
     * Delete users by IDs
     */
    @Query("DELETE FROM User u WHERE u.id IN :ids")
    void deleteByIdIn(@Param("ids") List<Long> ids);

    /**
     * Update user status in bulk (if you have status field)
     */
    // @Query("UPDATE User u SET u.status = :status WHERE u.id IN :ids")
    // void updateStatusByIdIn(@Param("status") UserStatus status, @Param("ids") List<Long> ids);
}
