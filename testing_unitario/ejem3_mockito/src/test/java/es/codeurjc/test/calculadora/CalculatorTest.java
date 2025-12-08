package es.codeurjc.test.calculadora;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * EJEMPLO SIMPLE DE MOCKITO - CALCULADORA
 * 
 * Este ejemplo es MÁS SENCILLO que el de la biblioteca.
 * Usa una calculadora que registra operaciones en un Logger.
 * 
 * CONCEPTOS CLAVE:
 * 1. MOCK: Objeto falso que simula el Logger
 * 2. when(): Configura qué responde el mock
 * 3. verify(): Verifica que se llamó un método del mock
 */
public class CalculatorTest {

    // ========== PARTE 1: ENTENDER QUÉ ES UN MOCK ==========
    
    /**
     * TEST 1: Sin mock - Problema
     * 
     * Si usamos un Logger real, escribiría en archivos durante los tests.
     * ¡No queremos eso! Queremos probar solo la Calculator, no el Logger.
     */
    @Test
    public void withoutMock_NotRecommended() {
        // Usando Logger real (NO RECOMENDADO en tests)
        Logger realLogger = new Logger("INFO");
        Calculator calculator = new Calculator(realLogger);
        
        // Esto funciona, pero el Logger real escribe en consola
        int result = calculator.add(2, 3);
        
        assertEquals(5, result);
        // Problema: No podemos verificar fácilmente si se llamó a log()
    }
    
    /**
     * TEST 2: Con mock - Solución
     * 
     * Un MOCK es un objeto "falso" que reemplaza al Logger real.
     * No hace nada real, solo simula ser un Logger.
     */
    @Test
    public void withMock_Recommended() {
        // Crear un doble del Logger
        Logger loggerMock = mock(Logger.class);
        
        // El doble NO es un Logger real, es una simulación
        // No escribirá en archivos ni en consola durante el test
        
        Calculator calculator = new Calculator(loggerMock);
        
        int result = calculator.add(2, 3);
        
        assertEquals(5, result);
    }

    // ========== PARTE 2: ENTENDER when() - CONFIGURAR EL MOCK ==========
    
    /**
     * TEST 3: Problema - El mock devuelve void
     * 
     * En el test anterior, add() funcionó aunque el mock no estaba configurado.
     * ¿Por qué? Porque log() es un método void (no devuelve nada).
     * 
     * Pero si el método devolviera algo importante, necesitaríamos configurarlo.
     */
    @Test
    public void mockReturnsDefaultValues() {
        Logger loggerMock = mock(Logger.class);
        
        // Por defecto, los métodos del mock devuelven valores "vacíos":
        // - void: no hace nada (OK para nosotros)
        // - boolean: devuelve false
        // - int: devuelve 0
        // - objetos: devuelven null
        
        // Ejemplo: isEnabled() devuelve false por defecto
        assertFalse(loggerMock.isEnabled());
        
        // Si nuestra calculadora dependiera de isEnabled(), tendríamos un problema...
    }
    
    /**
     * TEST 4: when() - Configurar lo que devuelve el mock
     * 
     * when() nos permite CONFIGURAR qué debe devolver el mock.
     * Sintaxis: when(mock.metodo()).thenReturn(valor)
     * 
     * Aunque isEnabled() no se usa en Calculator, es un buen ejemplo
     * para entender el concepto.
     */
    @Test
    public void understandingWhen() {
        Logger loggerMock = mock(Logger.class);
        
        // SIN configurar: devuelve false (valor por defecto)
        assertFalse(loggerMock.isEnabled());
        
        // CONFIGURAR: "Cuando llamen a isEnabled(), devuelve true"
        when(loggerMock.isEnabled()).thenReturn(true);
        
        // AHORA devuelve true (como lo configuramos)
        assertTrue(loggerMock.isEnabled());
        
        // when() nos da CONTROL sobre lo que devuelve el mock
    }
    
    /**
     * TEST 5: doThrow() - Simular excepciones
     * 
     * doThrow() nos permite configurar el mock para que LANCE EXCEPCIONES.
     * Esto es útil para probar cómo se comporta nuestro código ante errores.
     * 
     * Sintaxis: doThrow(excepción).when(mock).metodo()
     * Nota: Se usa doThrow en lugar de when para métodos void
     */
    @Test
    public void understandingDoThrow() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // CONFIGURAR: Simular que log() lanza una excepción
        // (por ejemplo, si el disco está lleno o sin permisos)
        doThrow(new RuntimeException("Error escribiendo log"))
            .when(loggerMock).log(anyString());
        
        // VERIFICAR: add() lanzará excepción porque internamente llama a log()
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            calculator.add(2, 3);
        });
        
        assertEquals("Error escribiendo log", exception.getMessage());
        
        // Esto demuestra cómo probar el manejo de errores en dependencias
    }
    
    /**
     * TEST 6: when() - Ejemplo práctico
     * 
     * Configuramos múltiples métodos
     * que usa Calculator.getLogInfo()
     */
    @Test
    public void whenPracticalExample() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // CONFIGURAR el comportamiento del doble
        when(loggerMock.isEnabled()).thenReturn(true);
        when(loggerMock.getLogLevel()).thenReturn("DEBUG");
        
        // USAR: Llamar a un método que depende del mock
        String info = calculator.getLogInfo();
        
        // VERIFICAR: El resultado depende de lo que configuramos
        assertEquals("Logging activo - Nivel: DEBUG", info);
        
        // Si no configuráramos el doble, isEnabled() devolvería false
        // y el resultado sería diferente
    }
    
    /**
     * TEST 7: when() - Cambiar la configuración
     * 
     * Podemos reconfigurar el mock para simular diferentes situaciones
     */
    @Test
    public void whenWithDifferentConfigurations() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // ESCENARIO 1: Logging activado con nivel INFO
        when(loggerMock.isEnabled()).thenReturn(true);
        when(loggerMock.getLogLevel()).thenReturn("INFO");
        assertEquals("Logging activo - Nivel: INFO", calculator.getLogInfo());
        
        // ESCENARIO 2: Logging activado con nivel ERROR
        when(loggerMock.getLogLevel()).thenReturn("ERROR");
        assertEquals("Logging activo - Nivel: ERROR", calculator.getLogInfo());
        
        // ESCENARIO 3: Logging desactivado
        when(loggerMock.isEnabled()).thenReturn(false);
        assertEquals("Logging desactivado", calculator.getLogInfo());
        
        // El último when() para cada método es el que "gana"
    }
    
    // ========== PARTE 3: ENTENDER verify() - VERIFICAR LLAMADAS ==========    
    /**
     * TEST 8: verify() básico - Verificar que se llamó un método
     * 
     * verify() comprueba si un método del mock FUE LLAMADO.
     * Es como revisar el historial de llamadas.
     */
    @Test
    public void understandingVerify() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // ACCIÓN: Realizar una suma
        calculator.add(5, 3);
        
        // VERIFICAR: ¿Se llamó al método log() del mock?
        verify(loggerMock).log("Suma: 5 + 3 = 8");
        
        // Si add() NO hubiera llamado a log(), este test fallaría
    }
    
    /**
     * TEST 9: verify() - Verificar que NO se llamó
     * 
     * También podemos verificar que un método NO fue llamado.
     */
    @Test
    public void verifyNeverCalled() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // ACCIÓN: Solo crear la calculadora, no hacer operaciones
        // (no llamamos a add, multiply, ni divide)
        
        // VERIFICAR: El método log() NO debe haber sido llamado nunca
        verify(loggerMock, never()).log(anyString());
        
        // VERIFICAR: El método error() NO debe haber sido llamado nunca
        verify(loggerMock, never()).error(anyString());
    }

    // ========== PARTE 4: COMBINAR when() y verify() ==========
    
    /**
     * TEST 10: Caso real - Suma
     */
    @Test
    public void addNumbers() {
        // 1. PREPARAR: Crear mock
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // 2. ACTUAR: Ejecutar la operación
        int result = calculator.add(10, 20);
        
        // 3. VERIFICAR: Comprobar resultado
        assertEquals(30, result);
        
        // 4. VERIFICAR: Comprobar que se registró la operación
        verify(loggerMock).log("Suma: 10 + 20 = 30");
    }
    
    /**
     * TEST 11: Caso real - Multiplicación
     */
    @Test
    public void multiplyNumbers() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        int result = calculator.multiply(4, 5);
        
        assertEquals(20, result);
        verify(loggerMock).log("Multiplicación: 4 * 5 = 20");
    }
    
    /**
     * TEST 12: Caso real - División por cero
     * 
     * Verifica que se llama a error() cuando hay un error
     */
    @Test
    public void divideByZero() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // Verificar que lanza excepción
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });
        
        // Verificar que se registró el error
        verify(loggerMock).error("Error: división por cero");
        
        // Verificar que NO se llamó a log() normal (porque hubo error)
        verify(loggerMock, never()).log(anyString());
    }

    // ========== PARTE 5: verify() CON times() - CONTAR LLAMADAS ==========
    
    /**
     * TEST 13: times() - Verificar cuántas veces se llamó
     * 
     * Podemos verificar el número de veces que se llamó un método
     */
    @Test
    public void verifyTimes() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        // Hacer 3 operaciones
        calculator.add(1, 1);
        calculator.add(2, 2);
        calculator.add(3, 3);
        
        // Verificar que log() se llamó EXACTAMENTE 3 veces
        verify(loggerMock, times(3)).log(anyString());
        verify(loggerMock, atLeastOnce()).log(anyString());
        verify(loggerMock, atLeast(2)).log(anyString());
        verify(loggerMock, atMost(5)).log(anyString());
    }
    
    /**
     * TEST 14: times() - Verificar operaciones mixtas
     */
    @Test
    public void verifyMixedOperations() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        calculator.add(1, 1);        // 1ra llamada a log()
        calculator.multiply(2, 3);   // 2da llamada a log()
        
        // Verificar que log() se llamó 2 veces en total
        verify(loggerMock, times(2)).log(anyString());
        
        // Verificar que cada operación se registró una vez
        verify(loggerMock, times(1)).log("Suma: 1 + 1 = 2");
        verify(loggerMock, times(1)).log("Multiplicación: 2 * 3 = 6");
    }

    // ========== PARTE 6: VERIFICAR CON anyString() - ARGUMENTOS FLEXIBLES ==========
    
    /**
     * TEST 15: anyString() - No importa el mensaje exacto
     * 
     * A veces no nos importa el valor exacto del argumento
     */
    @Test
    public void verifyWithAnyString() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        calculator.add(7, 8);
        
        // Verificar que se llamó a log() con CUALQUIER texto
        verify(loggerMock).log(anyString());
        
        // Esto es útil cuando no queremos verificar el mensaje exacto
    }
    
    /**
     * TEST 16: contains() - Verificar que el mensaje contiene algo
     */
    @Test
    public void verifyMessageContains() {
        Logger loggerMock = mock(Logger.class);
        Calculator calculator = new Calculator(loggerMock);
        
        calculator.multiply(3, 4);
        
        // Verificar que el mensaje contiene "Multiplicación"
        verify(loggerMock).log(contains("Multiplicación"));
        
        // Verificar que el mensaje contiene "12" (el resultado)
        verify(loggerMock).log(contains("12"));
    }

    // ========== RESUMEN FINAL ==========
    
    /**
     * TEST 17: Todo junto - Resumen completo
     * 
     * Este test muestra todos los conceptos juntos:
     * - Crear mock
     * - Configurar con when()
     * - Verificar con verify()
     * - Usar times(), anyString(), contains()
     */
    @Test
    public void completeSummary() {
        // 1. MOCK: Crear objeto falso
        Logger loggerMock = mock(Logger.class);
        
        // 2. when(): Configurar comportamiento
        when(loggerMock.isEnabled()).thenReturn(true);
        when(loggerMock.getLogLevel()).thenReturn("INFO");
        
        Calculator calculator = new Calculator(loggerMock);
        
        // 3. Ejecutar operaciones
        calculator.add(10, 5);
        calculator.multiply(3, 7);
        String logInfo = calculator.getLogInfo();
        
        // 4. verify(): Verificar llamadas
        verify(loggerMock, times(2)).log(anyString());  // Se llamó 2 veces
        verify(loggerMock).log(contains("Suma"));       // Una contiene "Suma"
        verify(loggerMock).log(contains("Multiplicación")); // Otra contiene "Multiplicación"
        verify(loggerMock, never()).error(anyString()); // Nunca se llamó error()
        
        // 5. Verificar que when() funcionó
        assertEquals("Logging activo - Nivel: INFO", logInfo);
        verify(loggerMock).isEnabled();
        verify(loggerMock).getLogLevel();
    }
}
