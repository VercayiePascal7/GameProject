package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Liest Spielereingaben, wandelt sie in Command-Objekte um und führt sie aus.
 *
 * Design Pattern: Command Pattern
 * Jeder Befehl wird als eigenes Objekt gekapselt.
 */
public class CommandParser {

    private final Player player;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    private final long startTime;
    private final int timeLimitSeconds;
    private final boolean timerAktiv;
    private final String difficulty;

    private final DatabaseManager db;
    private final String playerName;

    private final Map<String, String> richtungsAliase = new HashMap<>();

    public CommandParser(Player player, int timeLimitSeconds,
                         DatabaseManager db, String playerName) {
        this.player = player;
        this.timeLimitSeconds = timeLimitSeconds;
        this.timerAktiv = timeLimitSeconds > 0;
        this.startTime = System.currentTimeMillis();
        this.db = db;
        this.playerName = playerName;
        this.difficulty = timerAktiv
                ? (timeLimitSeconds <= 90 ? "Schwer" : "Mittel")
                : "Leicht";

        richtungsAliase.put("n", "norden");
        richtungsAliase.put("norden", "norden");
        richtungsAliase.put("s", "sueden");
        richtungsAliase.put("sueden", "sueden");
        richtungsAliase.put("o", "osten");
        richtungsAliase.put("osten", "osten");
        richtungsAliase.put("w", "westen");
        richtungsAliase.put("westen", "westen");
    }

    public void start() {
        printTrenner();
        printIntro();
        printTrenner();
        player.getCurrentRoom().showRoom();

        while (running) {

            if (timerAktiv && istZeitVorbei()) {
                printNiederlage();
                break;
            }

            if (player.isDead()) {
                running = false;
                break;
            }

            if (player.hasEscaped()) {
                if (timerAktiv) {
                    db.addHighscore(playerName, difficulty, (int) getVerbleibendeSekunden());
                    zeigeHighscores();
                }
                break;
            }

            zeigeStatuszeile();
            System.out.print("\n> ");
            String eingabe = scanner.nextLine().trim().toLowerCase();
            verarbeiteBefehl(eingabe);
        }
    }

    private void verarbeiteBefehl(String eingabe) {
        if (eingabe.isEmpty()) {
            System.out.println("Bitte gib einen Befehl ein. Tipp: 'hilfe'");
            return;
        }

        String[] teile = eingabe.split(" ", 2);
        String befehl = teile[0];
        String argument = teile.length > 1 ? teile[1].trim() : "";

        if (richtungsAliase.containsKey(befehl) && argument.isEmpty()) {
            new MoveCommand(player, richtungsAliase.get(befehl)).execute();
            return;
        }

        switch (befehl) {

            case "gehe":
                if (argument.isEmpty()) {
                    System.out.println("Wohin? Beispiel: gehe norden");
                } else {
                    String richtung = richtungsAliase.get(argument);
                    if (richtung == null) {
                        System.out.println("Unbekannte Richtung. Gueltig: norden, sueden, osten, westen");
                    } else {
                        new MoveCommand(player, richtung).execute();
                    }
                }
                break;

            case "nimm":
                if (argument.isEmpty()) {
                    System.out.println("Was moechtest du nehmen? Beispiel: nimm schluessel");
                } else {
                    new TakeCommand(player, argument).execute();
                }
                break;

            case "rede":
                if (argument.isEmpty()) {
                    System.out.println("Mit wem? Beispiel: rede Eron");
                } else {
                    new TalkCommand(player, argument).execute();
                }
                break;

            case "benutze":
                if (argument.isEmpty()) {
                    System.out.println("Was? Beispiel: benutze schluessel");
                } else {
                    benutzeGegenstand(argument);
                }
                break;

            case "inventar":
            case "i":
                new InventoryCommand(player).execute();
                break;

            case "schau":
                new LookCommand(player).execute();
                break;

            case "karte":
                zeigeKarte();
                break;

            case "speichern":
                speichern(argument.isEmpty() ? "slot1" : argument);
                break;

            case "laden":
                System.out.println("Starte das Spiel neu und waehle beim Start einen Spielstand.");
                break;

            case "highscore":
                zeigeHighscores();
                break;

            case "hilfe":
                zeigeHilfe();
                break;

            case "beenden":
                System.out.println("Du verlaesst das Spiel. Auf Wiedersehen, " + playerName + "!");
                running = false;
                break;

            default:
                System.out.println("Unbekannter Befehl: '" + befehl + "'. Gib 'hilfe' ein.");
        }
    }

    private void benutzeGegenstand(String gegenstand) {

        if (gegenstand.equals("karte")) {
            if (player.getInventory().hasItem("karte")) {
                zeigeKarte();
            } else {
                System.out.println("Du hast keine Karte. Vielleicht liegt sie irgendwo im Wald.");
            }
            return;
        }

        if (gegenstand.equals("fackel")) {
            if (player.getInventory().hasItem("fackel")) {
                System.out.println("Du zuendest die Fackel an.");
                System.out.println("Im flackernden Licht erkennst du auf der Rueckseite deiner Karte einen Hinweis:");
                System.out.println("\"Beim alten Baum liegt unter den Wurzeln ein geheimer Tunnel.\"");
            } else {
                System.out.println("Du hast keine Fackel. Vielleicht liegt sie irgendwo im Wald.");
            }
            return;
        }

        if (gegenstand.equals("schluessel")) {
            if (!player.getInventory().hasItem("schluessel")) {
                System.out.println("Du hast keinen Schluessel. Suche ihn zuerst.");
                return;
            }

            Room current = player.getCurrentRoom();

            if (!current.getName().equalsIgnoreCase("Verschlossenes Tor")) {
                System.out.println("Den Schluessel kannst du nur am verschlossenen Tor benutzen.");
                System.out.println("Das Tor liegt oestlich der Waldlichtung.");
                return;
            }

            Room ausgang = current.getExit("osten");

            if (ausgang != null && ausgang.isLocked()) {
                ausgang.unlock();
                System.out.println("Du steckst den Schluessel ins alte Schloss...");
                System.out.println("Klick! Das Tor schwingt auf.");
                System.out.println("Gehe jetzt nach Osten, um zu entkommen!");
            } else {
                System.out.println("Das Tor ist bereits offen. Gehe nach Osten!");
            }
            return;
        }

        if (gegenstand.equals("schaufel")) {

            if (!player.getInventory().hasItem("schaufel")) {
                System.out.println("Du hast keine Schaufel.");
                return;
            }

            if (!player.getInventory().hasItem("karte")) {
                System.out.println("Du hast zwar eine Schaufel, aber ohne Karte weisst du nicht, wo du graben sollst.");
                return;
            }

            Room current = player.getCurrentRoom();

            if (!current.getName().equalsIgnoreCase("Alter Baum")) {
                System.out.println("Hier bringt dir die Schaufel nichts.");
                System.out.println("Vielleicht zeigt die Karte einen besonderen Ort...");
                return;
            }

            System.out.println();
            System.out.println("Du vergleichst die Karte mit den Wurzeln des alten Baumes.");
            System.out.println("Ein Zeichen auf der Karte passt genau zu einer Wurzel vor dir.");
            System.out.println("Der Boden wirkt locker.");
            System.out.println("Willst du hier graben? (ja/nein)");
            System.out.print("> ");

            String antwort = scanner.nextLine().trim().toLowerCase();

            if (antwort.equals("ja") || antwort.equals("j")) {
                System.out.println();
                System.out.println("Du beginnst mit der Schaufel zu graben...");
                System.out.println("Nach einigen Minuten stoesst du auf Holz.");
                System.out.println("Unter den Wurzeln oeffnet sich eine alte Luke.");
                System.out.println("Dahinter fuehrt ein geheimer Tunnel aus dem Wald.");
                System.out.println("Willst du in den Tunnel gehen? (ja/nein)");
                System.out.print("> ");

                String tunnelAntwort = scanner.nextLine().trim().toLowerCase();

                if (tunnelAntwort.equals("ja") || tunnelAntwort.equals("j")) {
                    Room geheimerTunnel = new Room(
                            "Geheimer Tunnel",
                            "Du kriechst durch den engen Tunnel. Am Ende siehst du helles Licht."
                    );

                    geheimerTunnel.setExitRoom(true);
                    geheimerTunnel.setEndingType("geheim");

                    player.escapeSecret(geheimerTunnel);
                } else {
                    System.out.println("Du entscheidest dich, den Tunnel noch nicht zu betreten.");
                }

            } else {
                System.out.println("Du laesst die Stelle vorerst unberuehrt.");
            }

            return;
        }

        System.out.println("'" + gegenstand + "' kannst du hier nicht benutzen.");
    }

    private void speichern(String saveName) {
        List<String> itemNames = player.getInventory().getAllItems()
                .stream()
                .map(Item::getName)
                .collect(Collectors.toList());

        int elapsed = (int) ((System.currentTimeMillis() - startTime) / 1000);

        db.saveGame(saveName,
                player.getCurrentRoom().getName(),
                elapsed,
                difficulty,
                itemNames);
    }

    private void printTrenner() {
        System.out.println("=".repeat(44));
    }

    private void printIntro() {
        System.out.println();
        System.out.println("Du wachst mitten im Wald auf.");
        System.out.println("Der Boden ist nass, Nebel liegt zwischen den Baeumen.");
        System.out.println("Dein Kopf schmerzt nur Bruchstuecke einer Erinnerung:");
        System.out.println("  \"Lauf... bevor es zu spaet ist...\"");
        System.out.println();
        System.out.println("Irgendwo in diesem Wald gibt es einen Ausgang vielleicht sogar zwei.");
        System.out.println("Gib 'hilfe' ein, um alle Befehle zu sehen.");
        System.out.println();
    }

    private void zeigeStatuszeile() {
        System.out.println();
        printTrenner();

        if (timerAktiv) {
            long sek = getVerbleibendeSekunden();
            System.out.println("Zeit: " + sek + " Sekunden");
        } else {
            System.out.println("Modus: Leicht (kein Timer)");
        }

        System.out.println("Raum: " + player.getCurrentRoom().getName());
        printTrenner();
    }

    private void zeigeKarte() {
        System.out.println();
        System.out.println("KARTE DES WALDES");
        System.out.println();
        System.out.println("                             Norden");
        System.out.println("                                Alter Baum");
        System.out.println("                                   |");
        System.out.println("                                Nebelpfad");
        System.out.println("                                   |");
        System.out.println("Westen      Verlassenes Lager - Waldlichtung - Verschlossenes Tor - Waldrand/EXIT      Osten");
        System.out.println("                                   |");
        System.out.println("                                 Sumpf");
        System.out.println("                              Sueden");
        System.out.println();
        System.out.println("Hinweis: Ein geheimer Tunnel ist nicht direkt auf der Karte eingezeichnet.");
        System.out.println("Du befindest dich: " + player.getCurrentRoom().getName());
        System.out.println("=".repeat(52));
    }

    private void zeigeHighscores() {
        System.out.println();
        System.out.println("BESTENLISTE");
        List<String> scores = db.getHighscores();

        if (scores.isEmpty()) {
            System.out.println("Noch keine Eintraege.");
        } else {
            scores.forEach(System.out::println);
        }

        System.out.println("=".repeat(44));
    }

    private void zeigeHilfe() {
        System.out.println();
        System.out.println("VERFUEGBARE BEFEHLE");
        System.out.println("Bewegung:");
        System.out.println("  gehe norden/sueden/osten/westen");
        System.out.println("  n / s / o / w");
        System.out.println();
        System.out.println("Aktionen:");
        System.out.println("  nimm <gegenstand>");
        System.out.println("  benutze <gegenstand>");
        System.out.println("  rede <name>");
        System.out.println();
        System.out.println("Information:");
        System.out.println("  inventar / i");
        System.out.println("  schau");
        System.out.println("  karte");
        System.out.println("  highscore");
        System.out.println();
        System.out.println("Speichern:");
        System.out.println("  speichern <name>");
        System.out.println();
        System.out.println("Sonstiges:");
        System.out.println("  hilfe");
        System.out.println("  beenden");
        System.out.println("=".repeat(52));
    }

    private void printNiederlage() {
        System.out.println();
        System.out.println("GAME OVER");
        System.out.println("Die Zeit ist abgelaufen!");
        System.out.println("Die Dunkelheit des Waldes verschluckt dich...");
        System.out.println();
        running = false;
    }

    private boolean istZeitVorbei() {
        return getVerbleibendeSekunden() <= 0;
    }

    private long getVerbleibendeSekunden() {
        long vergangen = (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(0, timeLimitSeconds - vergangen);
    }

    private static class MoveCommand implements Command {
        private final Player player;
        private final String direction;

        MoveCommand(Player player, String direction) {
            this.player = player;
            this.direction = direction;
        }

        @Override
        public void execute() {
            player.move(direction);
        }
    }

    private static class TakeCommand implements Command {
        private final Player player;
        private final String itemName;

        TakeCommand(Player player, String itemName) {
            this.player = player;
            this.itemName = itemName;
        }

        @Override
        public void execute() {
            Item item = player.getCurrentRoom().takeItem(itemName);

            if (item != null) {
                player.getInventory().addItem(item);

                if (item.getName().equals("schluessel")) {
                    System.out.println("Tipp: Gehe zum verschlossenen Tor und benutze den Schluessel!");
                }

                if (item.getName().equals("fackel")) {
                    System.out.println("Tipp: Benutze die Fackel, um versteckte Hinweise zu lesen.");
                }

                if (item.getName().equals("schaufel")) {
                    System.out.println("Tipp: Die Schaufel koennte bei lockerer Erde nuetzlich sein.");
                }

            } else {
                System.out.println("Diesen Gegenstand gibt es hier nicht.");
            }
        }
    }

    private static class TalkCommand implements Command {
        private final Player player;
        private final String npcName;

        TalkCommand(Player player, String npcName) {
            this.player = player;
            this.npcName = npcName;
        }

        @Override
        public void execute() {
            NPC npc = player.getCurrentRoom().getNPC(npcName);

            if (npc != null) {
                System.out.println();
                npc.talk(player);
            } else {
                System.out.println("Diese Person ist nicht hier.");
            }
        }
    }

    private static class InventoryCommand implements Command {
        private final Player player;

        InventoryCommand(Player player) {
            this.player = player;
        }

        @Override
        public void execute() {
            player.getInventory().showItems();
        }
    }

    private static class LookCommand implements Command {
        private final Player player;

        LookCommand(Player player) {
            this.player = player;
        }

        @Override
        public void execute() {
            player.getCurrentRoom().showRoom();
        }
    }
}