package com.onofreiflavius.music.model.database.repository;

import com.onofreiflavius.music.model.database.tables.Session;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessionRepostiory extends JpaRepository<Session, Integer> {

    @Query(value = "SELECT * FROM sessions WHERE id = :sessionId", nativeQuery = true)
    Optional<Session> doesSessionExist(@Param("sessionId") String sessionId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sessions WHERE user_id = :userId", nativeQuery = true)
    void deleteSessionByUserId(@Param("userId") int userId);

    @Query(value = "SELECT user_id FROM sessions WHERE id = :id;", nativeQuery = true)
    int getUserIdBySessionId(@Param("id") String id);

}
