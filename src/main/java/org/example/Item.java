package org.example;

// Repräsentiert ein Item im Spiel
public class Item {
    private String name;
    private String description;

    // Konstruktor
    public Item(String name, String description) {
        this.name = name.toLowerCase();
        this.description = description;
    }

    // Name zurückgeben
    public String getName() {
        return name;
    }

    // Schön formatierter Anzeigename
    public String getDisplayName() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    // Beschreibung zurückgeben
    public String getDescription() {
        return description;
    }

    // Item benutzen
    public void use() {
        System.out.println("Du benutzt: " + getDisplayName());
    }
}