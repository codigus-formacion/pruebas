package es.codeurjc.test.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.Test;

/**
 * EJEMPLO DE MOCKITO - SERVICIO DE USUARIOS CON BASE DE DATOS
 * 
 * Este ejemplo muestra:
 * - Mockear una base de datos (interfaz UserDatabase)
 * - Configurar respuestas (usuario encontrado vs null)
 * - Verificar que se lanzan excepciones cuando corresponde
 * - Probar lógica de negocio sin acceder a base de datos real
 * 
 * CONCEPTOS CLAVE:
 * 1. MOCK: Objeto falso que simula UserDatabase
 * 2. when().thenReturn(): Configurar qué devuelve el mock
 * 3. when().thenReturn(null): Simular que no se encontró el usuario
 * 4. assertThrows(): Verificar que se lanza una excepción
 * 5. verify(): Verificar que se llamaron métodos del mock
 */
public class UserServiceTest {

    // ========== PARTE 1: ENTENDER EL PROBLEMA ==========
    
    /**
     * TEST 1: Por qué necesitamos mockear la base de datos
     * 
     * Si usáramos una base de datos real en los tests:
     * - Sería LENTO (conexión, queries, I/O)
     * - Necesitaríamos configurar una BD de pruebas
     * - Los tests dependerían del estado de la BD
     * - Podríamos contaminar datos reales
     * 
     * Solución: Usar un MOCK de UserDatabase
     */
    @Test
    public void understandingWhyMockDatabase() {
        // Crear un MOCK (simulación) de la base de datos
        UserDatabase databaseMock = mock(UserDatabase.class);
        
        // El mock NO es una base de datos real
        // No se conectará a MySQL, PostgreSQL, ni nada real
        
        UserService userService = new UserService(databaseMock);
        
        // Podemos usar el servicio sin preocuparnos por BD real
        assertNotNull(userService);
    }
    
    /**
     * TEST 2: Mock devuelve null por defecto
     * 
     * Por defecto, los mocks devuelven:
     * - null para objetos
     * - false para boolean
     * - 0 para números
     */
    @Test
    public void mockReturnsNullByDefault() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        
        // SIN configurar, findByName devuelve null
        User user = databaseMock.findByName("cualquier-nombre");
        assertNull(user);
        
        // Esto puede causar problemas si nuestro código no maneja null...
    }

    // ========== PARTE 2: CONFIGURAR EL MOCK CON when() ==========
    
    /**
     * TEST 3: when().thenReturn() - Simular que SÍ se encuentra el usuario
     * 
     * Configuramos el mock para devolver un usuario específico
     */
    @Test
    public void whenUserExists() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        
        // CONFIGURAR: Cuando busquen "Juan", devolver un usuario
        User juan = new User("Juan", 25);
        when(databaseMock.findByName("Juan")).thenReturn(juan);
        
        // VERIFICAR: Ahora findByName devuelve el usuario configurado
        User found = databaseMock.findByName("Juan");
        assertNotNull(found);
        assertEquals("Juan", found.getName());
        assertEquals(25, found.getAge());
    }
    
    /**
     * TEST 4: when().thenReturn(null) - Simular que NO se encuentra el usuario
     * 
     * Podemos configurar explícitamente que devuelva null
     * (aunque por defecto ya lo hace, esto es más explícito)
     */
    @Test
    public void whenUserDoesNotExist() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        
        // CONFIGURAR: Cuando busquen "Inexistente", devolver null
        when(databaseMock.findByName("Inexistente")).thenReturn(null);
        
        // VERIFICAR: findByName devuelve null
        User found = databaseMock.findByName("Inexistente");
        assertNull(found);
    }

    // ========== PARTE 3: PROBAR getUserByName() ==========
    
    /**
     * TEST 5: getUserByName() - Caso exitoso
     * 
     * Cuando el usuario SÍ existe, debe devolverlo
     */
    @Test
    public void getUserByName_UserExists_ReturnsUser() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: El usuario existe en la BD
        User maria = new User("Maria", 30);
        when(databaseMock.findByName("Maria")).thenReturn(maria);
        
        // ACTUAR: Buscar el usuario
        User result = userService.getUserByName("Maria");
        
        // VERIFICAR: Se devuelve el usuario correcto
        assertNotNull(result);
        assertEquals("Maria", result.getName());
        assertEquals(30, result.getAge());
    }
    
    /**
     * TEST 6: getUserByName() - Usuario no existe, lanza excepción
     * 
     * Cuando el usuario NO existe (null), debe lanzar UserNotFoundException
     */
    @Test
    public void getUserByName_UserNotFound_ThrowsException() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: El usuario NO existe (devuelve null)
        when(databaseMock.findByName("NoExiste")).thenReturn(null);
        
        // VERIFICAR: Se lanza la excepción esperada
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByName("NoExiste");
        });
        
        // VERIFICAR: El mensaje de la excepción es correcto
        assertEquals("Usuario no encontrado: NoExiste", exception.getMessage());
    }
    
    /**
     * TEST 7: getUserByName() - Verificar que se llamó a findByName
     * 
     * Además de verificar el resultado, podemos verificar las interacciones
     */
    @Test
    public void getUserByName_CallsFindByName() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        User pedro = new User("Pedro", 40);
        when(databaseMock.findByName("Pedro")).thenReturn(pedro);
        
        // ACTUAR
        userService.getUserByName("Pedro");
        
        // VERIFICAR: Se llamó a findByName con el parámetro correcto
        verify(databaseMock).findByName("Pedro");
    }

    // ========== PARTE 4: PROBAR createUser() ==========
    
    /**
     * TEST 8: createUser() - Usuario nuevo, se crea correctamente
     */
    @Test
    public void createUser_NewUser_Success() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: El usuario NO existe aún
        when(databaseMock.findByName("Carlos")).thenReturn(null);
        
        // CONFIGURAR: save() funciona correctamente
        when(databaseMock.save(any(User.class))).thenReturn(true);
        
        // ACTUAR: Crear usuario
        boolean result = userService.createUser("Carlos", "carlos@test.com", 28);
        
        // VERIFICAR: Se creó correctamente
        assertTrue(result);
        
        // VERIFICAR: Se llamó a findByName para verificar si existe
        verify(databaseMock).findByName("Carlos");
        
        // VERIFICAR: Se llamó a save para guardar el usuario
        verify(databaseMock).save(any(User.class));
    }
    
    /**
     * TEST 9: createUser() - Usuario ya existe, no se crea
     */
    @Test
    public void createUser_UserAlreadyExists_ReturnsFalse() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: El usuario YA existe
        User existing = new User("Ana", 22);
        when(databaseMock.findByName("Ana")).thenReturn(existing);
        
        // ACTUAR: Intentar crear usuario
        boolean result = userService.createUser("Ana", "otra@test.com", 25);
        
        // VERIFICAR: NO se creó (ya existía)
        assertFalse(result);
        
        // VERIFICAR: Se verificó si existe
        verify(databaseMock).findByName("Ana");
        
        // VERIFICAR: NO se llamó a save (porque ya existía)
        verify(databaseMock, never()).save(any(User.class));
    }

    // ========== PARTE 5: PROBAR deleteUser() ==========
    
    /**
     * TEST 10: deleteUser() - Usuario existe, se elimina
     */
    @Test
    public void deleteUser_UserExists_ReturnsTrue() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: delete() retorna true (se eliminó)
        when(databaseMock.delete("Luis")).thenReturn(true);
        
        // ACTUAR
        boolean result = userService.deleteUser("Luis");
        
        // VERIFICAR: Se eliminó correctamente
        assertTrue(result);
        
        // VERIFICAR: Se llamó a delete
        verify(databaseMock).delete("Luis");
    }
    
    /**
     * TEST 11: deleteUser() - Usuario no existe, retorna false
     */
    @Test
    public void deleteUser_UserNotFound_ReturnsFalse() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: delete() retorna false (no existía)
        when(databaseMock.delete("Fantasma")).thenReturn(false);
        
        // ACTUAR
        boolean result = userService.deleteUser("Fantasma");
        
        // VERIFICAR: No se eliminó (no existía)
        assertFalse(result);
    }

    // ========== PARTE 6: PROBAR isAdult() ==========
    
    /**
     * TEST 12: isAdult() - Usuario mayor de edad
     */
    @Test
    public void isAdult_UserIsAdult_ReturnsTrue() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: Usuario de 25 años
        User adult = new User("Sofia", 25);
        when(databaseMock.findByName("Sofia")).thenReturn(adult);
        
        // ACTUAR
        boolean result = userService.isAdult("Sofia");
        
        // VERIFICAR: Es mayor de edad
        assertTrue(result);
    }
    
    /**
     * TEST 13: isAdult() - Usuario menor de edad
     */
    @Test
    public void isAdult_UserIsMinor_ReturnsFalse() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: Usuario de 16 años
        User minor = new User("Joven", 16);
        when(databaseMock.findByName("Joven")).thenReturn(minor);
        
        // ACTUAR
        boolean result = userService.isAdult("Joven");
        
        // VERIFICAR: Es menor de edad
        assertFalse(result);
    }
    
    /**
     * TEST 14: isAdult() - Usuario justo en el límite (18 años)
     */
    @Test
    public void isAdult_UserIs18_ReturnsTrue() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: Usuario de exactamente 18 años
        User justAdult = new User("Recien", 18);
        when(databaseMock.findByName("Recien")).thenReturn(justAdult);
        
        // ACTUAR
        boolean result = userService.isAdult("Recien");
        
        // VERIFICAR: Con 18 años ya es mayor de edad
        assertTrue(result);
    }
    
    /**
     * TEST 15: isAdult() - Usuario no existe, lanza excepción
     * 
     * isAdult() usa getUserByName() internamente, que lanza excepción si no existe
     */
    @Test
    public void isAdult_UserNotFound_ThrowsException() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: Usuario no existe
        when(databaseMock.findByName("Nadie")).thenReturn(null);
        
        // VERIFICAR: Se lanza excepción
        assertThrows(UserNotFoundException.class, () -> {
            userService.isAdult("Nadie");
        });
    }

    // ========== PARTE 7: PROBAR isServiceAvailable() ==========
    
    /**
     * TEST 16: isServiceAvailable() - Base de datos conectada
     */
    @Test
    public void isServiceAvailable_DatabaseConnected_ReturnsTrue() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: BD conectada
        when(databaseMock.isConnected()).thenReturn(true);
        
        // ACTUAR
        boolean result = userService.isServiceAvailable();
        
        // VERIFICAR: Servicio disponible
        assertTrue(result);
    }
    
    /**
     * TEST 17: isServiceAvailable() - Base de datos desconectada
     */
    @Test
    public void isServiceAvailable_DatabaseDisconnected_ReturnsFalse() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: BD desconectada
        when(databaseMock.isConnected()).thenReturn(false);
        
        // ACTUAR
        boolean result = userService.isServiceAvailable();
        
        // VERIFICAR: Servicio NO disponible
        assertFalse(result);
    }

    // ========== PARTE 8: CASOS COMPLEJOS ==========
    
    /**
     * TEST 18: Escenario completo - Crear y verificar usuario
     */
    @Test
    public void completeScenario_CreateAndVerifyUser() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // ESCENARIO: El usuario no existe inicialmente
        when(databaseMock.findByName("Ricardo")).thenReturn(null);
        when(databaseMock.save(any(User.class))).thenReturn(true);
        
        // PASO 1: Crear usuario
        boolean created = userService.createUser("Ricardo", "ricardo@test.com", 35);
        assertTrue(created);
        
        // PASO 2: Ahora simular que SÍ existe (después de crearlo)
        User ricardo = new User("Ricardo", 35);
        when(databaseMock.findByName("Ricardo")).thenReturn(ricardo);
        
        // PASO 3: Obtener el usuario
        User found = userService.getUserByName("Ricardo");
        assertNotNull(found);
        assertEquals("Ricardo", found.getName());
        
        // PASO 4: Verificar que es mayor de edad
        assertTrue(userService.isAdult("Ricardo"));
        
        // VERIFICAR: Se hicieron todas las llamadas esperadas
        verify(databaseMock, times(3)).findByName("Ricardo"); // 3 veces: create, get, isAdult
        verify(databaseMock, times(1)).save(any(User.class));
    }
    
    /**
     * TEST 19: Múltiples usuarios con diferentes respuestas
     * 
     * Podemos configurar el mock para responder diferente según el parámetro
     */
    @Test
    public void multipleUsers_DifferentResponses() {
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // CONFIGURAR: Diferentes usuarios
        User user1 = new User("Alice", 20);
        User user2 = new User("Bob", 15);
        
        when(databaseMock.findByName("Alice")).thenReturn(user1);
        when(databaseMock.findByName("Bob")).thenReturn(user2);
        when(databaseMock.findByName("Charlie")).thenReturn(null);
        
        // VERIFICAR: Alice existe y es mayor de edad
        assertTrue(userService.isAdult("Alice"));
        
        // VERIFICAR: Bob existe pero es menor de edad
        assertFalse(userService.isAdult("Bob"));
        
        // VERIFICAR: Charlie no existe, lanza excepción
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByName("Charlie");
        });
    }

    // ========== RESUMEN FINAL ==========
    
    /**
     * TEST 20: Resumen de conceptos clave
     * 
     * Este test resume todo lo aprendido:
     * 1. Crear mock de la base de datos
     * 2. Configurar diferentes respuestas (objeto, null)
     * 3. Verificar que se lanzan excepciones
     * 4. Verificar interacciones con verify()
     * 5. Usar times() para contar llamadas
     * 6. Usar never() para verificar que NO se llamó
     */
    @Test
    public void summaryOfConcepts() {
        // 1. MOCK: Crear simulación de la base de datos
        UserDatabase databaseMock = mock(UserDatabase.class);
        UserService userService = new UserService(databaseMock);
        
        // 2. CONFIGURAR: Diferentes escenarios
        User existingUser = new User("Test", 30);
        when(databaseMock.findByName("Test")).thenReturn(existingUser);
        when(databaseMock.findByName("NotFound")).thenReturn(null);
        when(databaseMock.save(any(User.class))).thenReturn(true);
        when(databaseMock.delete("Test")).thenReturn(true);
        when(databaseMock.isConnected()).thenReturn(true);
        
        // 3. PROBAR: Diferentes operaciones
        assertNotNull(userService.getUserByName("Test"));
        assertThrows(UserNotFoundException.class, () -> userService.getUserByName("NotFound"));
        assertTrue(userService.isAdult("Test"));
        assertTrue(userService.deleteUser("Test"));
        assertTrue(userService.isServiceAvailable());
        
        // 4. VERIFICAR: Interacciones con el mock
        verify(databaseMock, times(2)).findByName("Test"); // getUserByName + isAdult
        verify(databaseMock, times(1)).findByName("NotFound");
        verify(databaseMock, times(1)).delete("Test");
        verify(databaseMock, times(1)).isConnected();
        verify(databaseMock, never()).save(any(User.class)); // En este test no se creó usuario
    }
}
