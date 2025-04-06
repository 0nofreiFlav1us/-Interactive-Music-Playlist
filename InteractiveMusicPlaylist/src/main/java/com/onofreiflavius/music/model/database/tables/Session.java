package com.onofreiflavius.music.model.database.tables;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    private String id;
    @Column(name = "user_id")
    private int userId;
    private Instant expire_date;

    // Constructors
    public Session() {}
    public Session(String id, int user_id, Instant expire_date) {
        this.id = id;
        this.userId = user_id;
        this.expire_date = expire_date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public Instant getExpire_date() {
        return expire_date;
    }
}
