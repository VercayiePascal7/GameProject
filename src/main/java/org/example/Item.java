package org.example;

public class Item {
    private String name;
    private String description;

    public Item(String name, String description) {
        this.name = name.toLowerCase();
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public String getDescription() {
        return description;
    }

    public void use() {
        System.out.println("Du benutzt: " + getDisplayName());
    }
}