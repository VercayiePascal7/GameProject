package org.example;

public class Player {
    private Room currentRoom;
    private Inventory inventory = new Inventory();
    private boolean escaped = false;

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean hasEscaped() {
        return escaped;
    }

    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("Du kannst dort nicht hingehen.");
            return;
        }

        if (nextRoom.requiresKey() && !inventory.hasItem("schluessel")) {
            System.out.println("Der Weg ist versperrt. Du brauchst einen Schluessel.");
            return;
        }

        currentRoom = nextRoom;
        currentRoom.showRoom();

        if (currentRoom.isExitRoom()) {
            escaped = true;
            System.out.println("\n🎉 Du hast den Wald verlassen und bist entkommen!");
        }
    }
}