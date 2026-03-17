package com.javadev.bod.painless.request.script;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ScriptFunctions {
    public Instant nowInstant() {
        return Instant.now();
    }

    public String formatUtcNow(String pattern) {
        return ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(pattern));
    }
}
