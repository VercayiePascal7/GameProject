package org.example;
import java.util.List;
import java.util.Scanner;

/**
 * Einstiegspunkt des Spiels.
 *
 * Hier wird die Spielwelt aufgebaut, Schwierigkeitsgrad gewählt
 * und optional ein Spielstand geladen.
 *
 * Design Patterns im Projekt:
 *   1. Strategy Pattern – TalkStrategy / DefaultTalkStrategy / HintTalkStrategy
 *   2. Command Pattern  – Command-Interface + Commands-Klassen in package command
 *   3. Singleton Pattern – DatabaseManager
 */
public class Game {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printWelcomeBanner();

        // Spielername erfragen
        System.out.print("Dein Name, Abenteurer: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) playerName = "Unbekannter";

        // Spielstand laden oder neu starten?
        boolean geladen = false;
        DatabaseManager db = DatabaseManager.getInstance();

        List<String> saves = db.listSaveGames();
        if (!saves.isEmpty()) {
            System.out.println("\nGespeicherte Spielstaende:");
            saves.forEach(System.out::println);
            System.out.print("\nSpieltstand laden? (Name eingeben oder Enter für Neu): ");
            String saveName = scanner.nextLine().trim();

            if (!saveName.isEmpty()) {
                SaveData data = db.loadGame(saveName);
                if (data != null) {
                    geladen = true;
                    startFromSave(scanner, playerName, data, db);
                }
            }
        }

        if (!geladen) {
            int timeLimit = chooseDifficulty(scanner);
            startNewGame(scanner, playerName, timeLimit, db);
        }

        db.close();
        scanner.close();
    }

    // Neues Spiel

    private static void startNewGame(Scanner scanner, String playerName,
                                     int timeLimit, DatabaseManager db) {

        // Räume erstellen
        Room waldlichtung  = new Room("Waldlichtung",
                "Du wachst mitten im Wald auf. Feuchtes Moos unter dir, Nebel Ueberall.");
        Room nebelpfad     = new Room("Nebelpfad",
                "Der Nebel wird dichter. Kraehen kreischen ueber dir ein schlechtes Zeichen.");
        Room alterBaum     = new Room("Alter Baum",
                "Ein uralter Baum ragt vor dir auf. Zwischen seinen Wurzeln glitzert etwas.");
        Room lager         = new Room("Verlassenes Lager",
                "Ein verlassenes Lager. Die Glut im Feuer ist noch warm. Ein alter Mann sitzt daneben.");
        Room sumpf         = new Room("Sumpf",
                "Der Boden gibt nach mit jedem Schritt. Hier ist kein sicheres Weiterkommen.");
        Room tor           = new Room("Verschlossenes Tor",
                "Ein massives Holztor versperrt den Weg nach draußen. Es braucht einen Schluessel.");
        Room waldrand      = new Room("Waldrand",
                "Der Wald endet. Vor dir liegt Licht – und die Freiheit.");
        Room versteckterPfad = new Room("Geheimer Tunnel",
                "Ein unterirdischer Tunnel fuehrt aus dem Wald heraus.");

        // Normale Ausgänge
        waldlichtung.setExitRoom(false);
        waldrand.setExitRoom(true);
        waldrand.setLocked(true);
        waldrand.setEndingType("normal");

        versteckterPfad.setExitRoom(true);
        versteckterPfad.setEndingType("geheim");

        // Verbindungen
        waldlichtung.setExit("norden", nebelpfad);
        waldlichtung.setExit("westen", lager);
        waldlichtung.setExit("sueden", sumpf);
        waldlichtung.setExit("osten",  tor);

        nebelpfad.setExit("sueden", waldlichtung);
        nebelpfad.setExit("norden", alterBaum);

        alterBaum.setExit("sueden", nebelpfad);

        lager.setExit("osten", waldlichtung);


        versteckterPfad.setExitRoom(true);
        versteckterPfad.setEndingType("geheim");

        sumpf.setExit("norden", waldlichtung);

        tor.setExit("westen", waldlichtung);
        tor.setExit("osten",  waldrand);

        // Gegenstände (mindestens 4)
        Item schluessel = new Item("schluessel", "Ein alter Schluessel mit eingravierten Baumzeichen.");
        Item karte      = new Item("karte",      "Eine zerknitterte Karte des Waldes mit markierten Wegen.");
        Item fackel     = new Item("fackel",     "Eine Holzfackel. Sie wirft Licht auf verborgene Details.");
        Item schaufel   = new Item("schaufel",   "Eine rostiger Schaufel nuetzlich zum Freischlagen von Pfaden.");

        alterBaum.addItem(schluessel);
        lager.addItem(karte);
        lager.addItem(schaufel);
        sumpf.addItem(fackel);

        // NPCs
        NPC eron       = new NPC("Eron",        new HintTalkStrategy());
        NPC nebelgeist = new NPC("Nebelgeist",  new DefaultTalkStrategy(
                "Der Wald laesst dich nicht einfach gehen... Suche den alten Baum im Norden."
        ));
        NPC wanderer = new NPC(
                "Wanderer",
                new DefaultTalkStrategy(
                        "Der Boden beim alten Baum wirkt seltsam locker..."
                )
        );


        lager.addNPC(eron);
        sumpf.addNPC(nebelgeist);
        nebelpfad.addNPC(wanderer);

        Room geheimerTunnel = new Room(
                "Geheimer Tunnel",
                "Ein dunkler Tunnel fuehrt tief unter dem Wald hindurch..."
        );

        geheimerTunnel.setExitRoom(true);
        geheimerTunnel.setEndingType("geheim");

        // Quest
        Quest hauptquest = new Quest("Finde den Schluessel und verlasse den Wald.");

        // Spieler starten
        Player player = new Player(hauptquest);
        player.setName(playerName);
        player.setCurrentRoom(waldlichtung);


        CommandParser parser = new CommandParser(player, timeLimit, db, playerName);
        parser.start();
    }

    // Spiel aus Spielstand laden

    private static void startFromSave(Scanner scanner, String playerName,
                                      SaveData data, DatabaseManager db) {
        System.out.println("Spielstand geladen – du stehst in: " + data.getCurrentRoom());
        // Vereinfacht: Spiel neu aufbauen und Spieler in gespeicherten Raum setzen
        // In einem vollständigen Spiel würde man die Raumreferenz direkt wiederherstellen
        int timeLimit = timeLimitForDifficulty(data.getDifficulty());
        // Restzeit berücksichtigen
        int adjusted  = Math.max(0, timeLimit - data.getElapsedSeconds());
        startNewGame(scanner, playerName, adjusted, db);
    }

    private static int timeLimitForDifficulty(String diff) {
        return switch (diff.toLowerCase()) {
            case "schwer" -> 90;
            case "leicht" -> 0;
            default       -> 280;
        };
    }



    // Hilfsmethoden

    private static void printWelcomeBanner() {

        System.out.println("ESCAPE THE FOREST");

        System.out.println();
    }

    /**
     * Fragt den Spieler nach dem Schwierigkeitsgrad.
     * Gibt das Zeitlimit in Sekunden zurück (0 = kein Timer).
     */
    private static int chooseDifficulty(Scanner scanner) {
        System.out.println("Schwierigkeitsgrad waehlen:");
        System.out.println("  1    Leicht  (kein Timer)");
        System.out.println("  2    Mittel  (280 Sekunden)");
        System.out.println("  3    Schwer  ( 90 Sekunden)");
        System.out.print("> ");

        return switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "leicht" -> { System.out.println(" Leicht\n"); yield 0;   }
            case "3", "schwer" -> { System.out.println(" Schwer\n"); yield 90;  }
            default            -> { System.out.println(" Mittel\n"); yield 280; }
        };
    }
}