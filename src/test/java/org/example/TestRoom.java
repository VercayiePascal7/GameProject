package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestRoom {

    @Test
    public void testSetAndGetExit() {
        Room forest = new Room("Wald", "Dunkler Wald.");
        Room cave = new Room("Hoehle", "Dunkle Hoehle.");

        forest.setExit("osten", cave);

        assertEquals(cave, forest.getExit("osten"));
    }

    @Test
    public void testTakeItem() {
        Room room = new Room("Hoehle", "Dunkle Hoehle.");
        Item key = new Item("schluessel", "Ein alter Schluessel.");

        room.addItem(key);

        Item takenItem = room.takeItem("schluessel");

        assertNotNull(takenItem);
        assertEquals("schluessel", takenItem.getName());
        assertNull(room.takeItem("schluessel"));
    }

    @Test
    public void testAddAndGetNPC() {
        Room room = new Room("Huette", "Alte Huette.");
        NPC npc = new NPC("Eron", new DefaultTalkStrategy("Hallo."));

        room.addNPC(npc);

        assertNotNull(room.getNPC("Eron"));
        assertEquals("Eron", room.getNPC("Eron").getName());
    }

    @Test
    public void testExitRoom() {
        Room exit = new Room("Waldrand", "Der Ausgang.");

        exit.setExitRoom(true);

        assertTrue(exit.isExitRoom());
    }

    @Test
    public void testRequiresKey() {
        Room exit = new Room("Waldrand", "Der Ausgang.");

        exit.setRequiresKey(true);

        assertTrue(exit.requiresKey());
    }
}