package dev.ftb.mods.ftbfiltersystem.util;

import com.mojang.datafixers.util.Either;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public record GlobRegexMatcher(String raw, Pattern pattern) {
    public static <T> Either<T, GlobRegexMatcher> parseWithFallback(String string, Supplier<T> fallback) {
        if (string.startsWith("/") && string.endsWith("/") && string.length() >= 3) {
            // a proper regex
            return Either.right(new GlobRegexMatcher(string, Pattern.compile(string.substring(1, string.length() - 1))));
        } else if (string.contains("*") || string.contains("?")) {
            // a glob with a * or ? wildcard
            return Either.right(new GlobRegexMatcher(string, Pattern.compile(wildcardToRegex(string))));
        } else {
            // none of the above!
            return Either.left(fallback.get());
        }
    }

    private static String wildcardToRegex(String wildcardStr) {
        StringBuilder s = new StringBuilder(wildcardStr.length());
        s.append('^');
        for (int i = 0, is = wildcardStr.length(); i < is; i++) {
            char c = wildcardStr.charAt(i);
            switch (c) {
                case '*' -> s.append(".*");
                case '?' -> s.append(".");
                case '(', ')', '[', ']', '$', '^', '.', '{', '}', '|', '\\' -> s.append("\\").append(c);
                default -> s.append(c);
            }
        }
        s.append('$');
        return (s.toString());
    }

    public boolean match(String str) {
        return pattern.matcher(str).find();
    }
}
