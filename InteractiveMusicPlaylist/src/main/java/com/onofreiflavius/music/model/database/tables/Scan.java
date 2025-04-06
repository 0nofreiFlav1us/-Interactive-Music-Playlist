package com.onofreiflavius.music.model.database.tables;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "scans")
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int userId;
    private LocalDate date;
    private LocalTime time;

    // Constructors
    public Scan() {}
    public Scan(int user_id, LocalDate date, LocalTime time) {
        this.userId = user_id;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

}
