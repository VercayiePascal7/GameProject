package org.example;

// Repräsentiert eine Quest im Spiel
public class Quest {
    private String description;
    private boolean completed;

    // Konstruktor
    public Quest(String description) {
        this.description = description;
    }

    // Quest abschließen
    public void complete() {
        completed = true;
        System.out.println("Quest abgeschlossen: " + description);
    }

    // Prüfen ob erledigt
    public boolean isCompleted() {
        return completed;
    }

    // Beschreibung zurückgeben
    public String getDescription() {
        return description;
    }
}