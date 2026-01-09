package com.example.demo.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * Convert text to slug format
     * Example: "Avengers: Endgame (2019)" -> "avengers-endgame-2019"
     */
    public static String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // Normalize unicode characters
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        // Remove accents and special characters
        String slug = NONLATIN.matcher(normalized).replaceAll("");

        // Convert to lowercase
        slug = slug.toLowerCase();

        // Replace whitespace with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Remove multiple consecutive hyphens
        slug = slug.replaceAll("-+", "-");

        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }

    /**
     * Generate unique slug by appending number if needed
     */
    public static String generateUniqueSlug(String baseSlug, java.util.function.Function<String, Boolean> existsChecker) {
        String slug = baseSlug;
        int counter = 1;

        while (existsChecker.apply(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}