package com.onofreiflavius.music.model.database.repository;

import com.onofreiflavius.music.model.database.tables.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ScanRepository extends JpaRepository<Scan, Integer> {

    @Query(value = "SELECT * FROM scans WHERE user_id = :userId AND date = :date;", nativeQuery = true)
    Optional<Scan> scannedToday(@Param("userId") int userId, @Param("date") LocalDate date);

    @Query(value = "SELECT COUNT(*) FROM scans WHERE date BETWEEN :startDate AND :endDate;", nativeQuery = true)
    int getNumberOfClientFromInterval(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
