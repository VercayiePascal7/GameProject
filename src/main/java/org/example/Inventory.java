package org.example;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
        System.out.println(item.getName() + " aufgenommen.");
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Item getItem(String name) {
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean hasItem(String name) {
        return getItem(name) != null;
    }

    public void showItems() {
        if (items.isEmpty()) {
            System.out.println("Inventar ist leer.");
        } else {
            System.out.println("🎒 Inventar:");
            items.forEach(i -> System.out.println("- " + i.getName() + ": " + i.getDescription()));
        }
    }
}