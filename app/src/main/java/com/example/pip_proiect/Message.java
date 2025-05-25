package com.example.pip_proiect;

/**
 * Clasa {@code Message} reprezintă un obiect simplu ce conține un singur mesaj de tip {@code String}.
 * <p>
 * Aceasta este folosită pentru a stoca și transfera text între componentele aplicației.
 */
public class Message {
    private final String text;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
