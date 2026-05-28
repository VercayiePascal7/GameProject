package org.example;

import java.util.*;

/**
 * Repräsentiert einen Ort im Wald.
 *
 * Jeder Raum hat: Name, Beschreibung, Ausgänge, Items, NPCs,
 * einen optionalen Sperrstatus (Schlüssel nötig),
 * eine optionale Item-Anforderung (geheimer Pfad)
 * und kann als Ausgangsraum (Ziel) markiert sein.
 */
public class Room {

    private final String            name;
    private final String            description;
    private final Map<String, Room> exits    = new LinkedHashMap<>();
    private final List<Item>        items    = new ArrayList<>();
    private final List<NPC>         npcs     = new ArrayList<>();

    private boolean locked       = false;
    private boolean exitRoom     = false;
    private boolean requireItems = false;  // Geheimpfad: braucht Schaufel + Karte
    private String  endingType   = "normal";

    //Konstruktor

    public Room(String name, String description) {
        this.name        = name;
        this.description = description;
    }

    //Ausgänge

    public void setExit(String richtung, Room raum) {
        exits.put(richtung.toLowerCase(), raum);
    }

    public Room getExit(String richtung) {
        return exits.get(richtung.toLowerCase());
    }

    public Set<String> getExitNames() {
        return exits.keySet();
    }

    //Items

    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Entnimmt ein Item aus dem Raum (Streams – Anforderung 1).
     * Gibt null zurück, wenn das Item nicht vorhanden ist.
     */
    public Item takeItem(String name) {
        // Stream 1: Item nach Name suchen und entfernen
        Item gefunden = items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if (gefunden != null) {
            items.remove(gefunden);
        }
        return gefunden;
    }

    /** Gibt alle Item-Namen als Liste zurück (Stream 2). */
    public List<String> getItemNames() {
        return items.stream()
                .map(Item::getName)
                .toList();
    }

    //NPCs

    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    /**
     * Sucht einen NPC nach Name (Groß-/Kleinschreibung egal).
     * Stream 3 – in CommandParser / Room.
     */
    public NPC getNPC(String name) {
        return npcs.stream()
                .filter(n -> n.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    //Sperrstatus & Geheimpfad

    public void   setLocked(boolean locked)      { this.locked = locked; }
    public boolean isLocked()                    { return locked; }
    public void   unlock()                       { this.locked = false; }

    public void   setRequiresItems(boolean req)  { this.requireItems = req; }
    public boolean requiresItems()               { return requireItems; }

    //Ausgangsraum

    public void    setExitRoom(boolean exitRoom)  { this.exitRoom = exitRoom; }
    public boolean isExitRoom()                   { return exitRoom; }

    public void    setEndingType(String type)     { this.endingType = type; }
    public String  getEndingType()                { return endingType; }

    //Getter

    public String getName() { return name; }

    //Anzeige

    /**
     * Gibt alle Informationen zum Raum auf der Konsole aus.
     * Stream 4: Items und NPCs mit forEach ausgeben.
     */
    public void showRoom() {
        System.out.println();
        System.out.println(name.toUpperCase());
        System.out.println(description);
        System.out.println();

        if (!items.isEmpty()) {
            System.out.println("Gegenstaende:");
            // Stream 4a: Items anzeigen
            items.stream()
                    .map(i -> "   - " + i.getDisplayName() + ": " + i.getDescription())
                    .forEach(System.out::println);
        }

        if (!npcs.isEmpty()) {
            System.out.println("Personen:");
            // Stream 4b: NPCs anzeigen
            npcs.stream()
                    .map(n -> "   - " + n.getName() + "  (rede " + n.getName() + ")")
                    .forEach(System.out::println);
        }

        if (!exits.isEmpty()) {
            System.out.println("Ausgaenge: " + String.join(", ", exits.keySet()));
        }
    }
}