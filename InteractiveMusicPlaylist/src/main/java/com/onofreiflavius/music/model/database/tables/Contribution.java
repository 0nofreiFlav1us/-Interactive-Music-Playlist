package com.onofreiflavius.music.model.database.tables;

import jakarta.persistence.*;

@Entity
@Table(name = "contributions")
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "song_id")
    private int songId;

    private boolean removed;

    public Contribution() {}

    public Contribution(int userId, int songId, boolean removed) {
        this.userId = userId;
        this.songId = songId;
        this.removed = removed;
    }

    // Getters and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

}
