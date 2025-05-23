package com.example.demo.utils;
public class YoutubeUrlUtil {
    public static String extractVideoId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String videoId = null;
        String[] parts = url.split("v=");
        if (parts.length > 1) {
            videoId = parts[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            videoId = url.substring(url.lastIndexOf("/") + 1);
        }
        return videoId;
    }
}
