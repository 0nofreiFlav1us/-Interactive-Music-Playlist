package com.onofreiflavius.music.model.database.tables;

import jakarta.persistence.*;

@Entity
@Table(name = "dislikes")
public class Dislike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "song_id")
    private int songId;
    @Column(name = "user_id")
    private int userId;

    // Constructors
    public Dislike() {}
    public Dislike(int song_id, int user_id) {
        this.songId = song_id;
        this.userId = user_id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
