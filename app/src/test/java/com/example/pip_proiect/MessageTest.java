package com.example.pip_proiect;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Teste unitare pentru clasa {@link Message}.
 */
public class MessageTest {

    @Test
    public void testGetText() {
        String expectedText = "Salut!";
        Message message = new Message(expectedText);

        assertEquals(expectedText, message.getText());
    }
}
