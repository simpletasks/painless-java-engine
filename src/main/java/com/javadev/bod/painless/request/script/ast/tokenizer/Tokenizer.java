package com.javadev.bod.painless.request.script.ast.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple lexical tokenizer for the Painless-like scripting language.
 *
 * <p>The tokenizer converts a raw script input string into a sequence of {@link Token}
 * objects that are later consumed by the parser. It performs a single pass over the
 * input characters and emits tokens representing language constructs such as
 * identifiers, keywords, literals, and punctuation symbols.</p>
 *
 * <p>The tokenizer supports:</p>
 * <ul>
 *     <li>Whitespace skipping</li>
 *     <li>Single-line comments starting with <code>//</code></li>
 *     <li>Identifiers and keywords</li>
 *     <li>String literals enclosed in single quotes (<code>'</code>) with basic escape support</li>
 *     <li>Operators (<code>=</code>, <code>==</code>, <code>!=</code>)</li>
 *     <li>Structural symbols (<code>{ } [ ] ( ) . , ; :</code>)</li>
 *     <li>Special composite token <code>:]</code></li>
 * </ul>
 *
 * <p>Recognized keywords are:</p>
 * <ul>
 *     <li><code>if</code></li>
 *     <li><code>else</code></li>
 *     <li><code>return</code></li>
 *     <li><code>null</code></li>
 * </ul>
 *
 * <p>All other word-like sequences are emitted as {@link TokenType#IDENTIFIER}.</p>
 *
 * <p>The tokenizer always appends an {@link TokenType#EOF} token at the end of
 * the returned token stream.</p>
 *
 * <p>If an unsupported character is encountered, an {@link IllegalArgumentException}
 * is thrown.</p>
 */
public class Tokenizer {

    /**
     * Converts the provided script input into a list of tokens.
     *
     * <p>The method performs lexical analysis by scanning the input character by character
     * and grouping characters into meaningful tokens such as identifiers, operators,
     * punctuation symbols, and string literals.</p>
     *
     * <p>Whitespace and comments are ignored and do not produce tokens.</p>
     *
     * @param input raw script source
     * @return ordered list of tokens representing the lexical structure of the input
     * @throws IllegalArgumentException if an unsupported character is encountered
     */
    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                while (i < input.length() && input.charAt(i) != '\n') {
                    i++;
                }
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                int start = i;
                i++;
                while (i < input.length()) {
                    char ch = input.charAt(i);
                    if (Character.isLetterOrDigit(ch) || ch == '_') {
                        i++;
                    } else {
                        break;
                    }
                }
                String word = input.substring(start, i);
                tokens.add(keywordOrIdentifier(word));
                continue;
            }

            if (Character.isDigit(c)) {
                int start = i;
                i++;

                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    i++;
                }

                boolean decimal = false;
                if (i < input.length() && input.charAt(i) == '.'
                        && i + 1 < input.length()
                        && Character.isDigit(input.charAt(i + 1))) {
                    decimal = true;
                    i++; // consume '.'

                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        i++;
                    }
                }

                String number = input.substring(start, i);
                tokens.add(new Token(decimal ? TokenType.DECIMAL : TokenType.INTEGER, number));
                continue;
            }

            if (c == '\'' || c == '"') {
                char quote = c;
                i++; // consume opening quote

                StringBuilder sb = new StringBuilder();
                boolean closed = false;

                while (i < input.length()) {
                    char ch = input.charAt(i);

                    if (ch == '\\' && i + 1 < input.length()) {
                        char next = input.charAt(i + 1);

                        switch (next) {
                            case '\\' -> sb.append('\\');
                            case '\'' -> sb.append('\'');
                            case '"' -> sb.append('"');
                            case 'n' -> sb.append('\n');
                            case 'r' -> sb.append('\r');
                            case 't' -> sb.append('\t');
                            default -> sb.append(next);
                        }

                        i += 2;
                        continue;
                    }

                    if (ch == quote) {
                        i++;
                        closed = true;
                        break;
                    }

                    sb.append(ch);
                    i++;
                }

                if (!closed) {
                    throw new IllegalArgumentException("Unterminated string literal");
                }

                tokens.add(new Token(TokenType.STRING, sb.toString()));
                continue;
            }

            if (c == ':' && i + 1 < input.length() && input.charAt(i + 1) == ']') {
                tokens.add(new Token(TokenType.COLON_RBRACKET, ":]"));
                i += 2;
                continue;
            }

            if (c == '!' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.NE, "!="));
                i += 2;
                continue;
            }

            if (c == '!') {
                tokens.add(new Token(TokenType.BANG, "!"));
                i++;
                continue;
            }

            if (c == '=' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.EQEQ, "=="));
                i += 2;
                continue;
            }

            if (c == '>' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.GTE, ">="));
                i += 2;
                continue;
            }

            if (c == '<' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.LTE, "<="));
                i += 2;
                continue;
            }

            if (c == '|' && i + 1 < input.length() && input.charAt(i + 1) == '|') {
                tokens.add(new Token(TokenType.OR_OR, "||"));
                i += 2;
                continue;
            }

            if (c == '&' && i + 1 < input.length() && input.charAt(i + 1) == '&') {
                tokens.add(new Token(TokenType.AND_AND, "&&"));
                i += 2;
                continue;
            }

            if (c == '+' && i + 1 < input.length() && input.charAt(i + 1) == '+') {
                tokens.add(new Token(TokenType.PLUS_PLUS, "++"));
                i += 2;
                continue;
            }

            if (c == '-' && i + 1 < input.length() && input.charAt(i + 1) == '-') {
                tokens.add(new Token(TokenType.MINUS_MINUS, "--"));
                i += 2;
                continue;
            }

            if (c == '+' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.PLUS_EQ, "+="));
                i += 2;
                continue;
            }

            if (c == '-' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.MINUS_EQ, "-="));
                i += 2;
                continue;
            }

            if (c == '*' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.STAR_EQ, "*="));
                i += 2;
                continue;
            }

            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.SLASH_EQ, "/="));
                i += 2;
                continue;
            }

            if (c == '%' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                tokens.add(new Token(TokenType.PERCENT_EQ, "%="));
                i += 2;
                continue;
            }

            switch (c) {
                case '=' -> tokens.add(new Token(TokenType.ASSIGN, "="));
                case '>' -> tokens.add(new Token(TokenType.GT, ">"));
                case '<' -> tokens.add(new Token(TokenType.LT, "<"));
                case '+' -> tokens.add(new Token(TokenType.PLUS, "+"));
                case '-' -> tokens.add(new Token(TokenType.MINUS, "-"));
                case '*' -> tokens.add(new Token(TokenType.STAR, "*"));
                case '/' -> tokens.add(new Token(TokenType.SLASH, "/"));
                case '%' -> tokens.add(new Token(TokenType.PERCENT, "%"));
                case '[' -> tokens.add(new Token(TokenType.LBRACKET, "["));
                case ']' -> tokens.add(new Token(TokenType.RBRACKET, "]"));
                case '{' -> tokens.add(new Token(TokenType.LBRACE, "{"));
                case '}' -> tokens.add(new Token(TokenType.RBRACE, "}"));
                case '(' -> tokens.add(new Token(TokenType.LPAREN, "("));
                case ')' -> tokens.add(new Token(TokenType.RPAREN, ")"));
                case '.' -> tokens.add(new Token(TokenType.DOT, "."));
                case ',' -> tokens.add(new Token(TokenType.COMMA, ","));
                case ';' -> tokens.add(new Token(TokenType.SEMICOLON, ";"));
                case ':' -> tokens.add(new Token(TokenType.COLON, ":"));
                default -> throw new IllegalArgumentException("Unsupported character: " + c);
            }
            i++;
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    /**
     * Determines whether the given word represents a language keyword or a regular identifier.
     *
     * <p>If the word matches one of the reserved keywords (<code>if</code>, <code>else</code>,
     * <code>return</code>, <code>null</code>) the corresponding keyword token type is returned.
     * Otherwise the word is treated as an {@link TokenType#IDENTIFIER}.</p>
     *
     * @param word parsed word from the input stream
     * @return token representing either a keyword or an identifier
     */
    private Token keywordOrIdentifier(String word) {
        return switch (word) {
            case "if" -> new Token(TokenType.IF, word);
            case "else" -> new Token(TokenType.ELSE, word);
            case "return" -> new Token(TokenType.RETURN, word);
            case "null" -> new Token(TokenType.NULL, word);
            case "true" -> new Token(TokenType.TRUE, word);
            case "false" -> new Token(TokenType.FALSE, word);
            case "instanceof" -> new Token(TokenType.INSTANCEOF, word);
            default -> new Token(TokenType.IDENTIFIER, word);
        };
    }
}
