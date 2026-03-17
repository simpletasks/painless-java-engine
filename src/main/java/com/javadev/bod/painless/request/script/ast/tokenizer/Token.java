package com.javadev.bod.painless.request.script.ast.tokenizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Token {
    private TokenType type;
    private String text;
}
