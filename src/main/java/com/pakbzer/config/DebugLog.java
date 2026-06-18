package com.pakbzer.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public final class DebugLog {

    private static final Path LOG = Path.of(System.getProperty("user.dir"), "debug-a4200b.log");

    private DebugLog() {
    }

    public static void write(String hypothesisId, String location, String message, String dataJson) {
        // #region agent log
        try {
            String line = String.format(
                    "{\"sessionId\":\"a4200b\",\"hypothesisId\":\"%s\",\"location\":\"%s\",\"message\":\"%s\",\"data\":%s,\"timestamp\":%d}%n",
                    esc(hypothesisId), esc(location), esc(message), dataJson, Instant.now().toEpochMilli());
            Files.writeString(LOG, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
        // #endregion
    }

    private static String esc(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
