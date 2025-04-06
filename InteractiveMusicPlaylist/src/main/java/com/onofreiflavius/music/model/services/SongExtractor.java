package com.onofreiflavius.music.model.services;

import com.fasterxml.jackson.databind.JsonNode;

public class SongExtractor {

    public static JsonNode getItems(JsonNode playlistDataTree) {
        JsonNode items = null;
        if (playlistDataTree != null && playlistDataTree.has("items")) {
            items = playlistDataTree.get("items");
        }

        return items;
    }

    public static String getId(JsonNode item) {
        String id = "";
        if (item.has("snippet") && item.get("snippet").has("resourceId") && item.get("snippet").get("resourceId").has("videoId")) {
            id = item.get("snippet").get("resourceId").get("videoId").asText();
        }

        return id;
    }

    public static String getImageURL(JsonNode item) {
        String imageURL = "";
        if (item.has("snippet")) {
            if (item.get("snippet").has("thumbnails") && item.get("snippet").get("thumbnails").has("medium") && item.get("snippet").get("thumbnails").get("medium").has("url")) {
                imageURL = item.get("snippet").get("thumbnails").get("medium").get("url").asText();
            }
        }

        return imageURL;
    }

    public static String getTitle(JsonNode item) {
        String title = "";
        if (item.has("snippet")) {
            if (item.get("snippet").has("title")) {
                title = item.get("snippet").get("title").asText();
            }
        }

        return title;
    }

    public static String getNextPageToken(JsonNode item) {
        String nextPageToken = "";
        if (item.has("nextPageToken")) {
            nextPageToken = "&pageToken=" + item.get("nextPageToken").asText();
        }

        return nextPageToken;
    }

    public static String extractVideoIdFromLink(String link) {
        String videoId = null;

        if (link.startsWith("https://music.youtube.com/watch?v=")) {
            String[] split = link.substring("https://music.youtube.com/watch?v=".length()).split("&");
            videoId = split[0];
        } else if (link.startsWith("https://www.youtube.com/watch?v=")) {
            String[] split = link.substring("https://www.youtube.com/watch?v=".length()).split("&");
            videoId = split[0];
        } else if (link.startsWith("https://youtu.be/")) {
            String[] split = link.substring("https://youtu.be/".length()).split("\\?");
            videoId = split[0];
        } else {
            System.out.println("Not a youtube link!");
        }

        return videoId;
    }

}
