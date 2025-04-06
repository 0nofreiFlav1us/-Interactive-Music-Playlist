package com.onofreiflavius.music.controller;

import com.onofreiflavius.music.model.database.tables.Session;
import com.onofreiflavius.music.model.database.tables.Status;
import com.onofreiflavius.music.model.database.tables.User;
import com.onofreiflavius.music.model.services.Services;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
@RequestMapping("/playlist")
public class MusicController {

    private final Services services;

    public MusicController(Services services) {
        this.services = services;
    }


    // ENDPOINTS
    @GetMapping("/updateDB")
    public String updateDB(@CookieValue(value = "role", required = false) String role) {
        if (role != null && role.equals("admin")) {
            services.getSongsFromPlaylistAPICall();
        }

        return "redirect:/playlist/songs";
    }

    @PostMapping("/authentication/scan/go")
    public String scan(@RequestParam("code") String code, HttpServletResponse response) {

        Optional<User> userOpt = services.verifyUser(code);
        if (userOpt.isPresent() && userOpt.get().getStatus() == Status.accepted) {
            Session session = services.createSession(userOpt.get().getId());
            Cookie cookie = new Cookie("session_id", session.getId());
            long maxAge = Instant.now().until(session.getExpire_date(), ChronoUnit.SECONDS);
            cookie.setMaxAge((int) maxAge);
            cookie.setPath("/");

            response.addCookie(cookie);

            services.addScan(userOpt.get().getId());

            return "redirect:/playlist/songs";
        } else {
            return "redirect:/authentication/scan";
        }
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(@CookieValue(value = "role", required = false) String role, Model model) {
        if (role == null || ! role.equals("admin")) {
            return "redirect:/playlist/songs";
        }

        model.addAttribute("pendingUsers", services.getUsersByStatus(Status.pending));
        model.addAttribute("restrictedUsers", services.getUsersByStatus(Status.restricted));
        model.addAttribute("reportedUsers", services.getUsersByStatus(Status.reported));
        return "dashboard";
    }

    @PostMapping("/admin/approve/{userId}")
    public String approve(@PathVariable int userId) {
        services.approveUser(userId);
        return "redirect:/playlist/admin/dashboard";
    }

    @GetMapping("/authentication")
    public String authentication(@CookieValue(value = "session_id", required = false) String sessionId) {
        if (sessionId != null && services.sessionIsActive(sessionId)) {
            return "redirect:/playlist/songs";
        }

        return "authentication";
    }

    @PostMapping("/authentication/apply")
    public String go(@RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname, @RequestParam("email") String email) {
        String responseCode = services.registerUser(new User(firstname, lastname, email));
        return "redirect:/playlist/authentication/apply?respondeCode=" + responseCode;
    }

    @GetMapping("/authentication/scan")
    public String scan(@CookieValue(value = "session_id", required = false) String sessionId) {
        if (sessionId != null && services.sessionIsActive(sessionId)) {
            return "redirect:/playlist/songs";
        }
        return "scan";
    }

    @GetMapping("/songs")
    public String index(Model model, @CookieValue(value = "session_id", required = false) String sessionId) {

        if (sessionId == null || ! services.sessionIsActive(sessionId)) {
            return "redirect:/playlist/authentication/scan";
        }

        model.addAttribute("songs", services.getAllSongsIgnoreRemoved());
        model.addAttribute("dislikedSongIds", services.getDislikedSongIds(services.getUserIdBySessionId(sessionId)));
        model.addAttribute("contributions", services.getContributionsById(services.getUserIdBySessionId(sessionId)));

        services.reviewPlaylist(10);

        return "index";
    }

    @PostMapping("/add")
    public String addSong(@RequestParam("link") String link, @CookieValue(value = "session_id", required = false) String sessionId) {
        if (sessionId == null || ! services.sessionIsActive(sessionId)) {
            return "redirect:/playlist/authentication/scan";
        }

        services.addSongToPlaylist(link, services.getUserIdBySessionId(sessionId));
        return "redirect:/playlist/songs";
    }

    @GetMapping("/dj")
    public String dj(@CookieValue(value = "role") String role) {
        if (role == null || ! role.equals("admin")) {
            return "redirect:/playlist/songs";
        }

        return "dj";
    }

    @PostMapping("/dislike/{songId}")
    public String dislikeSong(@PathVariable String songId, @CookieValue(value = "session_id", required = false) String sessionId) {
        if (sessionId == null || ! services.sessionIsActive(sessionId)) {
            return "redirect:/playlist/authentication/scan";
        }

        try {
            services.dislikeSong(Integer.parseInt(songId), services.getUserIdBySessionId(sessionId));
        } catch (Exception exception) {
            System.out.println("Error while disliking song: " + exception.getMessage());
        }
        return "redirect:/playlist/songs";
    }

    @PostMapping("/undislike/{songId}")
    public String unDislikeSong(@PathVariable String songId, @CookieValue(value = "session_id", required = false) String sessionId) {
        if (sessionId == null || ! services.sessionIsActive(sessionId)) {
            return "redirect:/playlist/authentication/scan";
        }

        try {
            services.unDislikeSong(Integer.parseInt(songId), services.getUserIdBySessionId(sessionId));
        } catch (Exception exception) {
            System.out.println("Error while undisliking song: " + exception.getMessage());
        }
        return "redirect:/playlist/songs";
    }

    @GetMapping("/review")
    public String review(@CookieValue(value = "role", required = false) String role) {
        if (role != null && role.equals("admin")) {
            services.reviewPlaylist(25);
        }

        return "redirect:/playlist/songs";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

}
