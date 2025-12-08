package es.codeurjc.test.biblioteca;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

/**
 * EJEMPLO EXPLICATIVO DE MOCKITO
 * 
 * Este test demuestra los conceptos fundamentales de Mockito:
 * 
 * 1. MOCK (DOBLE): Un objeto simulado que reemplaza una dependencia real
 * 2. when().thenReturn(): Define el comportamiento del mock (qué debe devolver)
 * 3. verify(): Comprueba que se llamaron ciertos métodos del mock
 */
public class LibraryTest {

    /**
     * EJEMPLO 1: Uso básico de when() - Configurar comportamiento del mock
     * 
     * when() define QUÉ debe devolver el mock cuando se llama a un método.
     * Es como decir: "Cuando alguien pregunte X, responde Y"
     */
    @Test
    public void basicWhenExample() {
        // PASO 1: Crear un MOCK (doble) del libro
        // Un mock es un objeto "falso" que simula un Book real
        Book bookMock = mock(Book.class);
        
        // PASO 2: Configurar el comportamiento del mock con when()
        // Decimos: "Cuando alguien llame a getTitle(), devuelve 'El Quijote'"
        when(bookMock.getTitle()).thenReturn("El Quijote");
        when(bookMock.getAuthor()).thenReturn("Cervantes");
        when(bookMock.isAvailable()).thenReturn(true);
        
        // PASO 3: Usar el mock - ahora se comporta como configuramos
        assertEquals("El Quijote", bookMock.getTitle());
        assertEquals("Cervantes", bookMock.getAuthor());
        assertTrue(bookMock.isAvailable());
        
        // NOTA: Sin when(), los métodos del mock devuelven valores por defecto
        // (null para objetos, false para boolean, 0 para números)
    }

    /**
     * EJEMPLO 2: Uso básico de verify() - Comprobar que se llamó un método
     * 
     * verify() comprueba que un método del mock FUE LLAMADO durante el test.
     * Es como decir: "Verifica que se hizo tal cosa"
     */
    @Test
    public void basicVerifyExample() {
        // Crear mocks
        Book bookMock = mock(Book.class);
        NotificationService serviceMock = mock(NotificationService.class);
        
        // Configurar comportamiento
        when(bookMock.isAvailable()).thenReturn(true);
        when(bookMock.getTitle()).thenReturn("1984");
        
        // Crear biblioteca con el servicio mock
        Library library = new Library(serviceMock);
        
        // ACCIÓN: Prestar el libro
        library.borrowBook(bookMock, "usuario@email.com");
        
        // VERIFICACIÓN: Comprobar que se llamaron los métodos esperados
        verify(bookMock).isAvailable();  // ¿Se preguntó si está disponible?
        verify(bookMock).borrow();       // ¿Se prestó el libro?
        verify(serviceMock).sendEmail("usuario@email.com", 
            "Has tomado prestado: 1984");  // ¿Se envió el email?
    }

    /**
     * EJEMPLO 3: verify() con times() - Verificar CUÁNTAS VECES se llamó
     * 
     * Podemos verificar el número exacto de veces que se llamó un método.
     */
    @Test
    public void verifyWithTimesExample() {
        Book bookMock = mock(Book.class);
        NotificationService serviceMock = mock(NotificationService.class);
        
        when(bookMock.isAvailable()).thenReturn(true);
        when(bookMock.getTitle()).thenReturn("Cien años de soledad");
        
        Library library = new Library(serviceMock);
        
        // Prestar el libro DOS veces (simula dos préstamos diferentes)
        library.borrowBook(bookMock, "juan@email.com");
        library.borrowBook(bookMock, "maria@email.com");
        
        // Verificar que borrow() se llamó exactamente 2 veces
        verify(bookMock, times(2)).borrow();
        
        // Verificar que isAvailable() se llamó exactamente 2 veces
        verify(bookMock, times(2)).isAvailable();
        
        // Verificar que cada email se envió 1 vez
        verify(serviceMock, times(1)).sendEmail(eq("juan@email.com"), anyString());
        verify(serviceMock, times(1)).sendEmail(eq("maria@email.com"), anyString());
    }

    /**
     * EJEMPLO 4: verify() con never() - Verificar que NO se llamó
     * 
     * A veces queremos asegurar que un método NO se llamó nunca.
     */
    @Test
    public void verifyWithNeverExample() {
        Book bookMock = mock(Book.class);
        NotificationService serviceMock = mock(NotificationService.class);
        
        // El libro NO está disponible
        when(bookMock.isAvailable()).thenReturn(false);
        
        Library library = new Library(serviceMock);
        
        // Intentar prestar un libro no disponible
        boolean result = library.borrowBook(bookMock, "usuario@email.com");
        
        assertFalse(result);  // No se pudo prestar
        
        // Verificar que NO se prestó el libro (porque no estaba disponible)
        verify(bookMock, never()).borrow();
        
        // Verificar que NO se envió ningún email (porque falló el préstamo)
        verify(serviceMock, never()).sendEmail(anyString(), anyString());
    }

    /**
     * EJEMPLO 5: verify() con atLeast() y atMost() - Verificar rango de llamadas
     * 
     * Podemos verificar que se llamó "al menos" o "como máximo" cierto número de veces.
     */
    @Test
    public void verifyWithAtLeastAndAtMostExample() {
        NotificationService serviceMock = mock(NotificationService.class);
        Library library = new Library(serviceMock);
        
        // Enviar varios recordatorios
        library.sendReminder("user1@email.com", "111222333", "Libro 1");
        library.sendReminder("user2@email.com", "444555666", "Libro 2");
        library.sendReminder("user3@email.com", "777888999", "Libro 3");
        
        // Verificar que se enviaron AL MENOS 3 emails
        verify(serviceMock, atLeast(3)).sendEmail(anyString(), anyString());
        
        // Verificar que se enviaron COMO MÁXIMO 10 emails
        verify(serviceMock, atMost(10)).sendEmail(anyString(), anyString());
        
        // Verificar que se enviaron AL MENOS 1 SMS (cada recordatorio envía 1)
        verify(serviceMock, atLeastOnce()).sendSMS(anyString(), anyString());
    }

    /**
     * EJEMPLO 6: Combinando when() y verify() - Caso completo
     * 
     * Este ejemplo muestra cómo se usan juntos when() y verify() en un test real.
     */
    @Test
    public void completeBorrowAndReturnExample() {
        // ARRANGE (Preparar): Crear mocks y configurar comportamiento
        Book bookMock = mock(Book.class);
        NotificationService serviceMock = mock(NotificationService.class);
        
        // Configurar comportamiento del libro con when()
        when(bookMock.getTitle()).thenReturn("La Odisea");
        when(bookMock.isAvailable()).thenReturn(true);  // Primero está disponible
        
        Library library = new Library(serviceMock);
        
        // ACT (Actuar): Realizar las acciones que queremos probar
        boolean borrowed = library.borrowBook(bookMock, "homero@email.com");
        library.returnBook(bookMock, "homero@email.com");
        
        // ASSERT (Verificar): Comprobar resultados y verificar llamadas
        assertTrue(borrowed);
        
        // Verificar que se llamaron los métodos del libro
        verify(bookMock, times(1)).isAvailable();
        verify(bookMock, times(1)).borrow();
        verify(bookMock, times(1)).returnBook();
        verify(bookMock, times(2)).getTitle();  // Se llama en préstamo y devolución
        
        // Verificar que se enviaron las notificaciones correctas
        verify(serviceMock).sendEmail("homero@email.com", "Has tomado prestado: La Odisea");
        verify(serviceMock).sendEmail("homero@email.com", "Has devuelto: La Odisea");
        
        // Verificar que NO se enviaron SMS (este flujo no los usa)
        verify(serviceMock, never()).sendSMS(anyString(), anyString());
    }

    /**
     * EJEMPLO 7: ArgumentMatchers - Verificar con ANY
     * 
     * A veces no nos importa el valor exacto del argumento, solo que se llamó.
     */
    @Test
    public void argumentMatchersExample() {
        NotificationService serviceMock = mock(NotificationService.class);
        Library library = new Library(serviceMock);
        
        library.sendReminder("cualquier@email.com", "123456789", "Cualquier libro");
        
        // Verificar que se envió un email a CUALQUIER dirección con CUALQUIER mensaje
        verify(serviceMock).sendEmail(anyString(), anyString());
        
        // Verificar que se envió un email a una dirección específica con CUALQUIER mensaje
        verify(serviceMock).sendEmail(eq("cualquier@email.com"), anyString());
        
        // Verificar que se envió un SMS a CUALQUIER número con mensaje que contiene "Recordatorio"
        verify(serviceMock).sendSMS(anyString(), contains("Recordatorio"));
    }
}
