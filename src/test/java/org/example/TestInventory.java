package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestInventory {

    @Test
    public void testAddItem() {
        Inventory inventory = new Inventory();
        Item key = new Item("schluessel", "Ein alter Schluessel.");

        inventory.addItem(key);

        assertTrue(inventory.hasItem("schluessel"));
    }

    @Test
    public void testGetItem() {
        Inventory inventory = new Inventory();
        Item map = new Item("karte", "Eine Karte.");

        inventory.addItem(map);

        assertNotNull(inventory.getItem("karte"));
        assertEquals("karte", inventory.getItem("karte").getName());
    }

    @Test
    public void testRemoveItem() {
        Inventory inventory = new Inventory();
        Item key = new Item("schluessel", "Ein alter Schluessel.");

        inventory.addItem(key);
        inventory.removeItem(key);

        assertFalse(inventory.hasItem("schluessel"));
    }

    @Test
    public void testInventoryIsEmptyAtStart() {
        Inventory inventory = new Inventory();

        assertFalse(inventory.hasItem("karte"));
    }
}