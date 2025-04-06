package com.onofreiflavius.music.model.database.tables;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "video_id")
    private String videoId;
    private String title;
    private String imageUrl;
    private int dislikes;
    private boolean removed;
    private LocalDate added_at;

    // Constructors
    public Song() {}
    public Song(String video_id, String imageUrl, String title, LocalDate added_at) {
        this.videoId = video_id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.added_at = added_at;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public LocalDate getAddedAt() {
        return added_at;
    }

}

