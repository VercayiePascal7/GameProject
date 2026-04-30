package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestItem {


    @Test
    public void testItemName() {
        Item item = new Item("Karte", "Eine alte Karte.");

        assertEquals("karte", item.getName());
    }

    @Test
    public void testItemDisplayName() {
        Item item = new Item("karte", "Eine alte Karte.");

        assertEquals("Karte", item.getDisplayName());
    }

    @Test
    public void testItemDescription() {
        Item item = new Item("schluessel", "Ein rostiger Schluessel.");

        assertEquals("Ein rostiger Schluessel.", item.getDescription());
    }
}