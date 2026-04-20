package org.example;

import java.util.*;

public class Room {
    private String name;
    private String description;
    private Map<String, Room> exits = new HashMap<>();
    private List<Item> items = new ArrayList<>();
    private List<NPC> npcs = new ArrayList<>();

    private boolean exitRoom;
    private boolean requiresKey;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public Set<String> getExitNames() {
        return exits.keySet();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Item takeItem(String name) {
        Item item = items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (item != null) {
            items.remove(item);
        }
        return item;
    }

    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    public NPC getNPC(String name) {
        return npcs.stream()
                .filter(n -> n.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void setExitRoom(boolean exitRoom) {
        this.exitRoom = exitRoom;
    }

    public boolean isExitRoom() {
        return exitRoom;
    }

    public void setRequiresKey(boolean requiresKey) {
        this.requiresKey = requiresKey;
    }

    public boolean requiresKey() {
        return requiresKey;
    }

    public String getName() {
        return name;
    }

    public void showRoom() {
        System.out.println("\n📍 " + name);
        System.out.println(description);

        if (!items.isEmpty()) {
            System.out.println("Gegenstaende in der Naehe:");
            items.forEach(i -> System.out.println("- " + i.getDisplayName()));
        }

        if (!npcs.isEmpty()) {
            System.out.println("Personen in der Naehe:");
            npcs.forEach(n -> System.out.println("- " + n.getName()));
        }

        if (!exits.isEmpty()) {
            System.out.println("Ausgaenge:");
            exits.keySet().forEach(direction -> System.out.println("- " + direction));
        }
    }
}