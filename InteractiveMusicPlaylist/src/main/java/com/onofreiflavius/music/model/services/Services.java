package com.onofreiflavius.music.model.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onofreiflavius.music.model.database.repository.ScanRepository;
import com.onofreiflavius.music.model.database.repository.SessionRepostiory;
import com.onofreiflavius.music.model.database.repository.SongRepository;
import com.onofreiflavius.music.model.database.repository.UserRepository;
import com.onofreiflavius.music.model.database.tables.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Services {

    private final YoutubeDataAPI youtubeDataAPI;
    private final DatabaseServices databaseServices;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SessionRepostiory sessionRepostiory;
    private final ScanRepository scanRepository;

    public Services(YoutubeDataAPI youtubeDataApi, DatabaseServices databaseServices, SongRepository songRepository, UserRepository userRepository, SessionRepostiory sessionRepostiory, ScanRepository scanRepository) {
        this.youtubeDataAPI = youtubeDataApi;
        this.databaseServices = databaseServices;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.sessionRepostiory = sessionRepostiory;
        this.scanRepository = scanRepository;
    }


    public void getSongsFromPlaylistAPICall() {
        List<Song> songs = new ArrayList<>();
        String nextPageToken = "";

        boolean nextPageExists = true;
        while (nextPageExists) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String playlistDataJSON = youtubeDataAPI.list(nextPageToken);
                if (playlistDataJSON != null) {
                    JsonNode playlistDataTree = objectMapper.readTree(playlistDataJSON);
                    JsonNode items = SongExtractor.getItems(playlistDataTree);
                    if (items != null) {
                        for (JsonNode item : items) {
                            String id = SongExtractor.getId(item);
                            String imageURL = SongExtractor.getImageURL(item);
                            String title = SongExtractor.getTitle(item);

                            songs.add(new Song(id, imageURL, title, LocalDate.now()));
                        }

                        nextPageToken = SongExtractor.getNextPageToken(playlistDataTree);
                    }
                }

                if (nextPageToken.isEmpty()) {
                    nextPageExists = false;
                }
            } catch (Exception exception) {
                System.out.println("ObjectMapper ERROR: " + exception.getMessage());
                break;
            }
        }

        songRepository.saveAll(songs);
    }

    public void addSongToPlaylist(String link, int userId) {
        String videoId = SongExtractor.extractVideoIdFromLink(link);
        if (videoId != null) {
            String songDataJSON = youtubeDataAPI.insert(videoId);
            if (songDataJSON != null) {
                int tries = 5;
                while (tries > 0) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode songDataTree = objectMapper.readTree(songDataJSON);
                        String id = SongExtractor.getId(songDataTree);
                        String imageURL = SongExtractor.getImageURL(songDataTree);
                        String title = SongExtractor.getTitle(songDataTree);
                        Song song = new Song(id, imageURL, title, LocalDate.now());

                        databaseServices.saveSong(song);
                        databaseServices.addContribution(userId, song.getId());

                        tries = 0;
                    } catch (Exception exception) {
                        if (tries == 5) {
                            System.out.println("ObjectMapper ERROR: " + exception.getMessage());
                        }
                        tries--;
                    }
                }
            } else {
                System.out.println("Adding song to playlist failed!");
            }
        }
    }

    public void dislikeSong(int songId, int userId) {
        databaseServices.createUserSongDislikeLink(songId, userId);
        databaseServices.dislikeSong(songId);
    }

    public void unDislikeSong(int songId, int userId) {
        databaseServices.removeUserSongDislikeLink(songId, userId);
        databaseServices.unDislikeSong(songId);
    }

    public List<Integer> getDislikedSongIds(int userId) {
        return databaseServices.getDislikedSongIds(userId);
    }

    public List<Integer> getContributionsById(int userId) {
        return databaseServices.getContributionsById(userId);
    }

    public boolean doesUserExist(User user) {
        return (userRepository.doesUserExist(user.getEmail()) != null);
    }


    public String generateCode() {
        String code = RandomStringUtils.randomAlphanumeric(25);
        while (userRepository.doesCodeExist(code) != null) {
            code = RandomStringUtils.randomAlphanumeric(25);
        }

        return code;
    }

    public String registerUser(User user) {
        System.out.println("firstname: " + user.getFirstname());
        if ( ! doesUserExist(user)) {
            String code = generateCode();
            user.setCode(code);
            user.setStatus(Status.pending);
            userRepository.save(user);
            return "allgood";
        }

        return "fail";
    }

    public List<User> getUsersByStatus(Status status) {
        return userRepository.getUsersByStatus(status.toString());
    }

    public void approveUser(int userId) {
        databaseServices.approveUser(userId);
    }

    public Optional<User> verifyUser(String code) {
        return userRepository.findByCode(code);
    }

    public Session createSession(int userId) {
        String sessionId = RandomStringUtils.randomAlphanumeric(25);;
        while (sessionRepostiory.doesSessionExist(sessionId).isPresent()) {
            sessionId = RandomStringUtils.randomAlphanumeric(25);
        }

        Session session = new Session(sessionId, userId, Instant.parse(LocalDate.now() + "T23:59:59Z"));
        sessionRepostiory.deleteSessionByUserId(userId);
        sessionRepostiory.save(session);
        return session;
    }

    public void addScan(int userId) {
        LocalDate date = LocalDate.now();
        Optional<Scan> scanToday = scanRepository.scannedToday(userId, date);
        if (scanToday.isEmpty()) {
            scanRepository.save(new Scan(userId, date, LocalTime.now()));
        }
    }

    public boolean sessionIsActive(String sessionId) {
        boolean active = false;

        Optional<Session> sessionOpt = sessionRepostiory.doesSessionExist(sessionId);
        if (sessionOpt.isPresent()) {
            String dbExpirationDateAndTime = sessionOpt.get().getExpire_date().toString().replace("T", " ").replace("Z", "");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime expirationDateAndTime = LocalDateTime.parse(dbExpirationDateAndTime, formatter);
            LocalDateTime currentDateAndTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

            active = currentDateAndTime.isBefore(expirationDateAndTime);
        }
        return active;
    }

    public int getUserIdBySessionId(String sessionId) {
        return sessionRepostiory.getUserIdBySessionId(sessionId);
    }

    public void reviewPlaylist(int clientPercentageThreshold) {
        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            int totalNumberOfClientsPerDate = scanRepository.getNumberOfClientFromInterval(song.getAddedAt(), LocalDate.now());

            int dislikePercentage = 0;
            if (totalNumberOfClientsPerDate > 0) {
                dislikePercentage = (100 * song.getDislikes()) / totalNumberOfClientsPerDate;
            }
            if (dislikePercentage >= clientPercentageThreshold) {
                song.setRemoved(true);
                songRepository.save(song);

            }
        }
    }

    public List<Song> getAllSongsIgnoreRemoved() {
        return songRepository.getAllSongsIgnoreRemoved();
    }

}
