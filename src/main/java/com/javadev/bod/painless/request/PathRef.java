package com.javadev.bod.painless.request;

import java.util.List;
import java.util.StringJoiner;

public record PathRef(List<String> segments) {
    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(".");
        segments.forEach(sj::add);
        return sj.toString();
    }
}
