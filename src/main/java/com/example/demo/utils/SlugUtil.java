package com.example.demo.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");

    /**
     * Convert Vietnamese text to SEO-friendly slug
     * Example: "Thời Vàng Son Của Anh Đây" -> "thoi-vang-son-cua-anh-day"
     * Example: "Avengers: Endgame (2019)" -> "avengers-endgame-2019"
     */
    public static String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // Step 1: Convert Vietnamese characters to ASCII
        String slug = removeVietnameseAccents(text);
        
        // Step 2: Normalize unicode characters (for other special chars)
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        
        // Step 3: Convert to lowercase
        slug = slug.toLowerCase();
        
        // Step 4: Replace whitespace with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        
        // Step 5: Remove non-word characters except hyphens
        slug = NONLATIN.matcher(slug).replaceAll("");
        
        // Step 6: Remove multiple consecutive hyphens
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");
        
        // Step 7: Remove leading/trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }
    
    /**
     * Remove Vietnamese accents and convert to ASCII
     */
    private static String removeVietnameseAccents(String text) {
        // Vietnamese character mappings
        text = text.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        text = text.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
        text = text.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        text = text.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
        text = text.replaceAll("[ìíịỉĩ]", "i");
        text = text.replaceAll("[ÌÍỊỈĨ]", "I");
        text = text.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        text = text.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
        text = text.replaceAll("[ùúụủũưừứựửữ]", "u");
        text = text.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
        text = text.replaceAll("[ỳýỵỷỹ]", "y");
        text = text.replaceAll("[ỲÝỴỶỸ]", "Y");
        text = text.replaceAll("[đ]", "d");
        text = text.replaceAll("[Đ]", "D");
        return text;
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