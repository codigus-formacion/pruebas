package es.codeurjc.test.calculadora;

/**
 * Servicio de logging que será mockeado en los tests.
 * En producción escribiría en archivos o enviaría a un servidor.
 * En tests, usaremos un mock para no escribir archivos reales.
 */
public class Logger {

    private String logLevel;

    public Logger(String logLevel) {
        this.logLevel = logLevel;
    }
    
    public void log(String message) {
        // En producción: escribir en archivo o enviar a servidor
        System.out.println("[LOG] " + message);
    }
    
    public void error(String message) {
        // En producción: escribir en archivo de errores
        System.err.println("[ERROR] " + message);
    }
    
    public boolean isEnabled() {
        // En producción: verificar si el logging está activado
        return true;
    }
    
    public String getLogLevel() {
        // En producción: retorna el nivel de log configurado
        return System.getenv().getOrDefault("LOG_LEVEL", logLevel);
    }
}
