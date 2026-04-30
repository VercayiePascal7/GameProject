package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer {

    @Test
    public void testPlayerStartsInRoom() {
        Player player = new Player();
        Room forest = new Room("Wald", "Dunkler Wald.");

        player.setCurrentRoom(forest);

        assertEquals(forest, player.getCurrentRoom());
    }

    @Test
    public void testPlayerMoveToExistingExit() {
        Player player = new Player();

        Room forest = new Room("Wald", "Dunkler Wald.");
        Room cave = new Room("Hoehle", "Dunkle Hoehle.");

        forest.setExit("osten", cave);
        player.setCurrentRoom(forest);

        player.move("osten");

        assertEquals(cave, player.getCurrentRoom());
    }

    @Test
    public void testPlayerCannotMoveToInvalidExit() {
        Player player = new Player();

        Room forest = new Room("Wald", "Dunkler Wald.");
        player.setCurrentRoom(forest);

        player.move("norden");

        assertEquals(forest, player.getCurrentRoom());
    }

    @Test
    public void testPlayerCannotEnterExitWithoutKey() {
        Player player = new Player();

        Room cave = new Room("Hoehle", "Dunkle Hoehle.");
        Room exit = new Room("Waldrand", "Ausgang.");
        exit.setRequiresKey(true);

        cave.setExit("osten", exit);
        player.setCurrentRoom(cave);

        player.move("osten");

        assertEquals(cave, player.getCurrentRoom());
        assertFalse(player.hasEscaped());
    }

    @Test
    public void testPlayerCanEscapeWithKey() {
        Player player = new Player();

        Room cave = new Room("Hoehle", "Dunkle Hoehle.");
        Room exit = new Room("Waldrand", "Ausgang.");
        exit.setRequiresKey(true);
        exit.setExitRoom(true);

        cave.setExit("osten", exit);
        player.setCurrentRoom(cave);

        Item key = new Item("schluessel", "Ein alter Schluessel.");
        player.getInventory().addItem(key);

        player.move("osten");

        assertEquals(exit, player.getCurrentRoom());
        assertTrue(player.hasEscaped());
    }
}