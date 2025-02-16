package es.codeurjc.test.ejem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import es.codeurjc.test.User;

public class UserTest {

	@Test
	public void testUser1() {
		User user = mock(User.class);

		when(user.getName())
				.thenReturn("Pepe")
				.thenReturn("Juan");

		assertEquals("Pepe", user.getName());
		assertEquals("Juan", user.getName());
	}

	@Test
	public void testUser2() {
		User user = mock(User.class);

		when(user.getAndIncAge())
  			.thenThrow(new RuntimeException("Max age"));

		assertThrows(RuntimeException.class, () -> user.getAndIncAge());
	}

	@Test
	public void testUser3() {
		User user = mock(User.class);

		doThrow(new RuntimeException())
   			.when(user).print();

		assertThrows(RuntimeException.class, () -> user.print());
	}

	@Test
	public void testUser4() {
		User user = mock(User.class);

		when(user.greet("Juan")).thenReturn("Hola Juan");

		assertEquals("Hola Juan", user.greet("Juan"));

		when(user.greet(anyString())).thenThrow(new RuntimeException("Not value"));

		assertThrows(RuntimeException.class, () -> user.greet("Juan"));
	}

}
