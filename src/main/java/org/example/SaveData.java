package org.example;

import java.util.List;

/**
 * Datenklasse für einen geladenen Spielstand.
 * Wird von DatabaseManager.loadGame() zurückgegeben.
 */
public class SaveData {

    private final String       saveName;
    private final String       currentRoom;
    private final int          elapsedSeconds;
    private final String       difficulty;
    private final List<String> inventoryItems;

    public SaveData(String saveName, String currentRoom,
                    int elapsedSeconds, String difficulty,
                    List<String> inventoryItems) {
        this.saveName       = saveName;
        this.currentRoom    = currentRoom;
        this.elapsedSeconds = elapsedSeconds;
        this.difficulty     = difficulty;
        this.inventoryItems = inventoryItems;
    }

    public String       getSaveName()       { return saveName; }
    public String       getCurrentRoom()    { return currentRoom; }
    public int          getElapsedSeconds() { return elapsedSeconds; }
    public String       getDifficulty()     { return difficulty; }
    public List<String> getInventoryItems() { return inventoryItems; }
}