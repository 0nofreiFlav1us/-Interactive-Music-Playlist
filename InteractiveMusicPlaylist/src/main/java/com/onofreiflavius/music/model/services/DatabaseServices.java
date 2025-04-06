package com.onofreiflavius.music.model.services;

import com.onofreiflavius.music.model.database.tables.Contribution;
import com.onofreiflavius.music.model.database.tables.Dislike;
import com.onofreiflavius.music.model.database.tables.Song;
import com.onofreiflavius.music.model.database.repository.ContributionRepository;
import com.onofreiflavius.music.model.database.repository.DislikeRepository;
import com.onofreiflavius.music.model.database.repository.SongRepository;
import com.onofreiflavius.music.model.database.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DatabaseServices {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ContributionRepository contributionRepository;
    private final DislikeRepository dislikeRepository;

    public DatabaseServices(UserRepository userRepository, SongRepository songRepository, ContributionRepository contributionRepository, DislikeRepository dislikeRepository) {
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.contributionRepository = contributionRepository;
        this.dislikeRepository = dislikeRepository;
    }

    public void saveSong(Song song) {
        songRepository.save(song);
    }

    public void dislikeSong(int id) {
        Optional<Song> songOpt = songRepository.findById(id);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            song.setDislikes(song.getDislikes() + 1);
            songRepository.save(song);
        }
    }

    public void createUserSongDislikeLink(int songId, int userId) {
        dislikeRepository.save(new Dislike(songId, userId));
    }

    public void removeUserSongDislikeLink(int songId, int userId) {
        dislikeRepository.deleteBySongAndUserId(songId, userId);
    }

    public void unDislikeSong(int id) {
        Optional<Song> songOpt = songRepository.findById(id);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            if (song.getDislikes() > 0) {
                song.setDislikes(song.getDislikes() - 1);
            }
            songRepository.save(song);
        }
    }

    public List<Integer> getDislikedSongIds(int userId) {
        return dislikeRepository.getDislikedSongIds(userId);
    }

    public void addContribution(int userId, int songId) {
         contributionRepository.save(new Contribution(userId, songId, false));
    }

    public List<Integer> getContributionsById(int userId) {
        return contributionRepository.getContributionsById(userId);
    }

    public void approveUser(int userId) {
        userRepository.approveUserById(userId);
    }



}
