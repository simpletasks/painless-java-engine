package com.javadev.bod.painless;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDocument {
    private Meta meta;
    private Payload some;
    private Resolution resolution;
    private String status;
}