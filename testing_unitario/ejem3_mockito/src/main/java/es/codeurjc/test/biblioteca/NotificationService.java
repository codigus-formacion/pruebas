package es.codeurjc.test.biblioteca;

/**
 * Servicio que envía notificaciones.
 * Esta clase será mockeada para verificar que se llaman sus métodos correctamente,
 * sin necesidad de enviar notificaciones reales durante los tests.
 */
public class NotificationService {
    
    public void sendEmail(String recipient, String message) {
        // En producción, aquí se enviaría un email real
        // En los tests, mockearemos este método para no enviar emails
        System.out.println("Enviando email a " + recipient + ": " + message);
    }
    
    public void sendSMS(String phone, String message) {
        // En producción, aquí se enviaría un SMS real
        System.out.println("Enviando SMS a " + phone + ": " + message);
    }
    
    public boolean verifyConnection() {
        // Simula verificar la conexión con el servidor de notificaciones
        return true;
    }
}
