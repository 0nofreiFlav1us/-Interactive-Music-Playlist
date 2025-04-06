package com.onofreiflavius.music.model.database.repository;

import com.onofreiflavius.music.model.database.tables.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT id FROM users WHERE email = :email", nativeQuery = true)
    Integer doesUserExist(@Param("email") String email);

    @Query(value = "SELECT id FROM users WHERE email = :code", nativeQuery = true)
    Integer doesCodeExist(@Param("code") String code);

    @Query(value = "SELECT * FROM users WHERE status = :status", nativeQuery = true)
    List<User> getUsersByStatus(@Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET status = 'accepted' WHERE id = :userId", nativeQuery = true)
    void approveUserById(@Param("userId") int userId);

    @Query(value = "SELECT * FROM users WHERE code = :code;", nativeQuery = true)
    Optional<User> findByCode(@Param("code") String code);

}
