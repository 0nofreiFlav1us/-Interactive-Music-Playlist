package com.onofreiflavius.music.model.database.repository;

import com.onofreiflavius.music.model.database.tables.Dislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DislikeRepository extends JpaRepository<Dislike, Integer> {

    @Modifying
    @Transactional  // Make sure the operation is wrapped in a transaction
    @Query(value = "DELETE FROM dislikes WHERE song_id = :songId AND user_id = :userId;", nativeQuery = true)
    void deleteBySongAndUserId(@Param("songId") int songId, @Param("userId") int userId);

    @Query(value =  "SELECT song_id FROM dislikes WHERE user_id = :userId;", nativeQuery = true)
    List<Integer> getDislikedSongIds(@Param("userId") int userId);

}
