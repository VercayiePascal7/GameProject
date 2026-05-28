package org.example;

/**
 * Repräsentiert den Spieler.
 *
 * Verwaltet: Position, Inventar, Quest, Fluchtstatus und Spielername.
 * Bietet zwei verschiedene Enden:
 *   1. Normales Ende  – Spieler flieht durch das Tor mit dem Schlüssel
 *   2. Geheimes Ende  – Spieler findet den versteckten Waldpfad (Schaufel + Karte)
 */
public class Player {

    private String    name;
    private Room      currentRoom;
    private Inventory inventory  = new Inventory();
    private Quest     quest;
    private boolean   escaped    = false;
    private String    endingType = "";  // "normal" oder "geheim"
    private boolean dead = false;


    //Konstruktoren

    public Player() {}

    public Player(Quest quest) {
        this.quest = quest;
        System.out.println("Quest: " + quest.getDescription());
        System.out.println();
    }

    //Getter / Setter

    public String    getName()              { return name; }
    public void      setName(String n)      { this.name = n; }
    public Room      getCurrentRoom()       { return currentRoom; }
    public void      setCurrentRoom(Room r) { this.currentRoom = r; }
    public Inventory getInventory()         { return inventory; }
    public boolean   hasEscaped()           { return escaped; }
    public String    getEndingType()        { return endingType; }
    public boolean isDead() {
        return dead;
    }


    //Bewegung

    /**
     * Bewegt den Spieler in die angegebene Richtung.
     * Prüft Existenz, Sperrstatus und Item-Anforderungen des Ausgangs.
     */
    public void move(String direction) {
        Room naechster = currentRoom.getExit(direction);

        if (naechster == null) {
            System.out.println("In diese Richtung fuehrt kein Weg.");
            return;
        }

        // Gesperrter Raum – braucht Schlüssel
        if (naechster.isLocked()) {
            System.out.println("Dieser Weg ist versperrt. Vielleicht hilft ein Schluessel?");
            return;
        }

        // Geheimausgang – braucht Schaufel + Karte
        if (naechster.requiresItems()) {
            if (!inventory.hasItem("schaufel") || !inventory.hasItem("karte")) {
                System.out.println("Dichtes Gestruepp versperrt den Weg.");
                System.out.println("Du braeuchtest etwas zum Freischlagen... und eine Karte.");
                return;
            }
            System.out.println("Du hackst dich mit der Schaufel durch das Gestruepp...");
        }

        if (naechster.getName().equalsIgnoreCase("Sumpf")) {
            System.out.println();
            System.out.println("Du betrittst den Sumpf und sinkst langsam ein!");
            System.out.println("Was tust du?");
            System.out.println("1 - Ruhig bleiben und nach einem Ast greifen");
            System.out.println("2 - Wild herumstrampeln");
            System.out.println("3 - Weiter in den Sumpf laufen");
            System.out.print("> ");

            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String antwort = scanner.nextLine().trim();

            if (antwort.equals("1")) {
                System.out.println();
                System.out.println("Du bleibst ruhig und greifst nach einem Ast.");
                System.out.println("Mit letzter Kraft ziehst du dich heraus.");
                System.out.println("Du kehrst zur Waldlichtung zurueck.");
                return;
            } else {
                dead = true;
                endingType = "tod";

                System.out.println();
                System.out.println("TODES-ENDE");

                System.out.println();
                System.out.println("Du machst eine falsche Bewegung.");
                System.out.println("Der Sumpf verschlingt dich langsam...");
                System.out.println("Der Wald hat gewonnen.");
                System.out.println();
                return;
            }
        }

        currentRoom = naechster;
        currentRoom.showRoom();

        // Siegbedingung prüfen
        if (currentRoom.isExitRoom()) {
            escaped    = true;
            endingType = currentRoom.getEndingType();

            if (quest != null && !quest.isCompleted()) {
                quest.complete();
            }

            printEnding(endingType);
        }
    }

    public void escapeSecret(Room secretRoom) {
        currentRoom = secretRoom;
        currentRoom.showRoom();

        escaped = true;
        endingType = "geheim";

        if (quest != null && !quest.isCompleted()) {
            quest.complete();
        }

        printEnding(endingType);
    }

    /** Gibt den passenden Endbildschirm aus. */
    private void printEnding(String type) {
        System.out.println();
        if ("geheim".equals(type)) {

            System.out.println("GEHEIMES ENDE ENTDECKT!");

            System.out.println();
            System.out.println("Du bahnst dir einen Weg durch verborgene Pfade...");
            System.out.println("Hinter dem Gestrüpp öffnet sich ein lichtdurchflutetes Tal.");
            System.out.println("Kein Tor, kein Schlüssel  du hast den Wald auf eigene Faust bezwungen!");
            System.out.println();
            System.out.println("Geheimesende freigeschaltet! Meisterhaft!");
        } else {

            System.out.println("ENTKOMMEN!");

            System.out.println();
            System.out.println("Du trittst durch das Tor...");
            System.out.println("Frische Luft. Sonnenlicht. Vogelgezwitscher.");
            System.out.println("Du hast den Wald verlassen du bist frei!");
        }
        System.out.println();
    }
}