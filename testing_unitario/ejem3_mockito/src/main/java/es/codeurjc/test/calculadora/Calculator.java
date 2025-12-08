package es.codeurjc.test.calculadora;

/**
 * Calculadora simple que registra operaciones en un Logger.
 * Este ejemplo es perfecto para entender Mockito porque:
 * - La lógica es muy simple
 * - Tiene una dependencia clara (Logger) que podemos mockear
 */
public class Calculator {
    
    private Logger logger;
    
    public Calculator(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Suma dos números y registra la operación
     */
    public int add(int a, int b) {
        int result = a + b;
        logger.log("Suma: " + a + " + " + b + " = " + result);
        return result;
    }
    
    /**
     * Multiplica dos números y registra la operación
     */
    public int multiply(int a, int b) {
        int result = a * b;
        logger.log("Multiplicación: " + a + " * " + b + " = " + result);
        return result;
    }
    
    /**
     * Divide dos números. Registra error si divisor es cero.
     */
    public int divide(int a, int b) {
        if (b == 0) {
            logger.error("Error: división por cero");
            throw new ArithmeticException("No se puede dividir por cero");
        }
        int result = a / b;
        logger.log("División: " + a + " / " + b + " = " + result);
        return result;
    }
    
    /**
     * Obtiene información sobre el nivel de logging
     */
    public String getLogInfo() {
        if (logger.isEnabled()) {
            return "Logging activo - Nivel: " + logger.getLogLevel();
        } else {
            return "Logging desactivado";
        }
    }
}
