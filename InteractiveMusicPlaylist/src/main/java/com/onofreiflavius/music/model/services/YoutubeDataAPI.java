package com.onofreiflavius.music.model.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class YoutubeDataAPI {

    @Value("${access.token}")
    private String accessToken;
    private final String playlistId = "";

    public String list(String pageToken) {
        String listURI = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId="
                + playlistId
                + pageToken;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(listURI))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }

        } catch (Exception exception) {
            System.out.println("~@List Exception: " + exception.getMessage());
        }

        return null;
    }

    public String insert(String videoId) {
        String insertURI = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
        String requestBody = "{"
                + "\"snippet\": {"
                + "\"playlistId\": \"" + playlistId + "\","
                + "\"resourceId\": {"
                + "\"kind\": \"youtube#video\","
                + "\"videoId\": \"" + videoId + "\""
                + "}"
                + "}"
                + "}";

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(insertURI))
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception exception) {
            System.out.println("-@Insert Exception: " + exception.getMessage());
        }

        return null;
    }

}
