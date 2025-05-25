package com.example.pip_proiect;


import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;
/**
 * Clasă de test pentru {@link MessageAdapter}.
 * Această clasă conține teste unitare care verifică funcționalitatea adapterului
 * ce gestionează o listă de mesaje afișate într-un RecyclerView.
 */
public class MessageAdapterTest {

    @Test
    public void testGetItemCount() {
        Message msg1 = new Message("Salut");
        Message msg2 = new Message("Ce faci?");
        MessageAdapter adapter = new MessageAdapter(Arrays.asList(msg1, msg2));

        assertEquals(2, adapter.getItemCount());
    }
}
