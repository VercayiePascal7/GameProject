package org.example;

import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Escape the Forest ===");
        System.out.println("Waehle einen Schwierigkeitsgrad:");
        System.out.println("1 - Leicht  (kein Timer)");
        System.out.println("2 - Mittel  (280 Sekunden)");
        System.out.println("3 - Schwer  (90 Sekunden)");
        System.out.print("> ");

        int timeLimit = 0;
        String difficultyName = "";

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
            case "leicht":
                timeLimit = 0;
                difficultyName = "Leicht";
                break;
            case "2":
            case "mittel":
                timeLimit = 280;
                difficultyName = "Mittel";
                break;
            case "3":
            case "schwer":
                timeLimit = 90;
                difficultyName = "Schwer";
                break;
            default:
                System.out.println("Ungueltige Eingabe. Es wird automatisch Mittel gewaehlt.");
                timeLimit = 280;
                difficultyName = "Mittel";
        }

        System.out.println("\nSchwierigkeitsgrad: " + difficultyName);

        // Räume
        Room forest = new Room("Waldlichtung", "Du stehst auf einer kalten Waldlichtung. Nebel zieht zwischen den Baeumen hindurch.");
        Room cave = new Room("Hoehle", "Eine dunkle Hoehle. Es riecht nach feuchter Erde und altem Stein.");
        Room hut = new Room("Huette", "Eine alte Holzhuette. Die Bretter knarren bei jedem Luftzug.");
        Room river = new Room("Flussufer", "Ein reissender Fluss versperrt dir teilweise den Weg.");
        Room exit = new Room("Waldrand", "Vor dir lichtet sich der Wald. Du siehst Freiheit.");

        // Ausgang
        exit.setExitRoom(true);
        exit.setRequiresKey(true);

        // Verbindungen
        forest.setExit("norden", hut);
        forest.setExit("osten", cave);
        forest.setExit("westen", river);

        hut.setExit("sueden", forest);

        cave.setExit("westen", forest);
        cave.setExit("osten", exit);

        river.setExit("osten", forest);

        // Items
        Item key = new Item("schluessel", "Ein alter rostiger Schluessel.");
        Item map = new Item("karte", "Eine halb zerrissene Karte mit einem markierten Ausgang.");
        cave.addItem(key);
        hut.addItem(map);

        // NPCs
        NPC oldMan = new NPC("Eron", new HintTalkStrategy());
        NPC ghost = new NPC("Nebelgeist", new DefaultTalkStrategy("Nicht jeder Weg fuehrt hinaus... aber einer fuehrt in die Freiheit."));

        hut.addNPC(oldMan);
        river.addNPC(ghost);

        // Player
        Player player = new Player();
        player.setCurrentRoom(forest);

        // Spiel starten
        CommandParser parser = new CommandParser(player, timeLimit);
        parser.start();
    }
}