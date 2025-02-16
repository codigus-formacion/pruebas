package es.codeurjc.test;

public class User {

    private String name;
    private int age;
    private String lang;

    public User(String name, int age, String lang) {
        this.name = name;
        this.age = age;
        this.lang = lang;
    }

    public String greet() {
        if (lang.equals("es")) {
            return "Hola";
        } else if (lang.equals("en")) {
            return "Hello";
        } else {
            return "???";
        }
    }

    public String greet(String otherName) {
        return greet() + " " + otherName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAndIncAge() {
        return age++;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void print() {
        System.out.println("User [name=" + name + ", age=" + age + ", lang=" + lang + "]");
    }
    
    
}
