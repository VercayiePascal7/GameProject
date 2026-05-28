package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet alle Gegenstaende, die der Spieler bei sich trägt.
 *
 * Stream 6: getAllItems() gibt unveränderliche Kopie via stream().toList() zurück.
 */
public class Inventory {

    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
        System.out.println(item.getDisplayName() + " aufgenommen.");
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Item getItem(String name) {
        // Stream 7: Item nach Name suchen
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean hasItem(String name) {
        return getItem(name) != null;
    }

    /** Gibt eine unveränderliche Kopie der Item-Liste zurück (für Speichern). */
    public List<Item> getAllItems() {
        return items.stream().toList();  // Stream 6
    }

    public void showItems() {
        System.out.println();
        if (items.isEmpty()) {
            System.out.println("Dein Inventar ist leer.");
        } else {
            System.out.println("Inventar (" + items.size() + " Gegenstand/Gegenstaende):");
            // Stream 8: Items formatiert ausgeben
            items.stream()
                    .map(i -> "   - " + i.getDisplayName() + ": " + i.getDescription())
                    .forEach(System.out::println);
        }
    }
}