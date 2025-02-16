package es.codeurjc.test.ejem;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import es.codeurjc.test.Chat;
import es.codeurjc.test.User;

public class ChatTest {

    @Test
    public void testChat1() {
        Chat chat = mock(Chat.class);
        User user = mock(User.class);

        when(chat.getUser("Juan")).thenReturn(null);

        when(chat.getUser(anyString())).thenReturn(user);

    }

    @Test
    public void testChat2() {
        User user = mock(User.class);
        when(user.getName()).thenReturn("Pepe");

        Chat chat = mock(Chat.class);

        when(chat.getUser(anyString())).thenReturn(user);

        // Las siguientes comprobaciones fallarán
        verify(user).getName();
        verify(chat).getUser("Juan");
        verify(chat).getUser("Juan");

        verify(chat, times(1)).getUser("Pepe");
        verify(chat, times(2)).getUser("Juan");

        verify(chat, never()).getUser(anyString());

        verify(chat, atLeastOnce()).getUser("Bob");
        verify(chat, atLeast(2)).getUser("Susan");
        verify(chat, atMost(5)).getUser("MrX");
    }

}
