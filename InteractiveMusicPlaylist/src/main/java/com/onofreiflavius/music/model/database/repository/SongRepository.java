package com.onofreiflavius.music.model.database.repository;

import com.onofreiflavius.music.model.database.tables.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {

    // Get song by video_id
    @Query(value = "SELECT * FROM songs WHERE video_id = :videoId;", nativeQuery = true)
    Optional<Song> findByVideoId(@Param("videoId") String videoId);

    @Query(value = "SELECT * FROM songs WHERE removed=false;", nativeQuery = true)
    List<Song> getAllSongsIgnoreRemoved();

}
