package com.onofreiflavius.music.model.database.repository;

import com.onofreiflavius.music.model.database.tables.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Integer> {

    @Query(value = "SELECT song_id FROM contributions WHERE user_id = :userId", nativeQuery = true)
    List<Integer> getContributionsById(@Param("userId") int userId);

}
