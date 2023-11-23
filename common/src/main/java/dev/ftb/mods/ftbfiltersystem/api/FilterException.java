package dev.ftb.mods.ftbfiltersystem.api;

public class FilterException extends RuntimeException {
    public FilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterException(String message) {
        super(message);
    }
}
