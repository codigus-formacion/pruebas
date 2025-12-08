package es.codeurjc.test.biblioteca;

/**
 * Gestiona los préstamos de libros y notificaciones.
 * Esta clase usa dependencias (Book y NotificationService) que mockearemos en los tests.
 */
public class Library {
    
    private NotificationService notificationService;
    
    public Library(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Presta un libro y notifica al usuario
     */
    public boolean borrowBook(Book book, String userEmail) {
        if (book.isAvailable()) {
            book.borrow();
            notificationService.sendEmail(userEmail, 
                "Has tomado prestado: " + book.getTitle());
            return true;
        }
        return false;
    }
    
    /**
     * Devuelve un libro y notifica al usuario
     */
    public void returnBook(Book book, String userEmail) {
        book.returnBook();
        notificationService.sendEmail(userEmail, 
            "Has devuelto: " + book.getTitle());
    }
    
    /**
     * Envía un recordatorio por SMS y email
     */
    public void sendReminder(String email, String phone, String title) {
        notificationService.sendEmail(email, "Recordatorio: debes devolver " + title);
        notificationService.sendSMS(phone, "Recordatorio: debes devolver " + title);
    }
}
