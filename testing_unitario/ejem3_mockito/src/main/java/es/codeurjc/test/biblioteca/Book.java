package es.codeurjc.test.biblioteca;

/**
 * Representa un libro en una biblioteca.
 * Esta clase será mockeada en los tests para simular comportamientos.
 */
public class Book {
    
    private String title;
    private String author;
    private boolean available;
    
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void borrow() {
        if (!available) {
            throw new IllegalStateException("El libro ya está prestado");
        }
        available = false;
    }
    
    public void returnBook() {
        if (available) {
            throw new IllegalStateException("El libro no estaba prestado");
        }
        available = true;
    }
    
    public String getInfo() {
        return title + " de " + author;
    }
}
