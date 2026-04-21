package org.example;

public class Quest {
    private String description;
    private boolean completed;

    public Quest(String description) {
        this.description = description;
    }

    public void complete() {
        completed = true;
        System.out.println("Quest abgeschlossen: " + description);
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getDescription() {
        return description;
    }
}