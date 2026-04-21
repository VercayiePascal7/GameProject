package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandParser {
    private Player player;
    private Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    private long startTime;
    private int timeLimitSeconds;
    private boolean timerEnabled;

    private final Map<String, String> directionAliases = new HashMap<>();

    public CommandParser(Player player, int timeLimitSeconds) {
        this.player = player;
        this.timeLimitSeconds = timeLimitSeconds;
        this.timerEnabled = timeLimitSeconds > 0;

        if (timerEnabled) {
            this.startTime = System.currentTimeMillis();
        }

        directionAliases.put("n", "norden");
        directionAliases.put("s", "sueden");
        directionAliases.put("o", "osten");
        directionAliases.put("w", "westen");

        directionAliases.put("norden", "norden");
        directionAliases.put("sueden", "sueden");
        directionAliases.put("osten", "osten");
        directionAliases.put("westen", "westen");
    }

    public void start() {
        System.out.println("\n=== Escape the Forest ===");
        printIntro();
        player.getCurrentRoom().showRoom();

        while (running) {
            if (timerEnabled && isTimeOver()) {
                System.out.println("\n⏰ Die Zeit ist abgelaufen!");
                System.out.println("Du hast den Wald nicht rechtzeitig verlassen. Du hast verloren.");
                running = false;
                break;
            }

            if (player.hasEscaped()) {
                running = false;
                break;
            }

            if (timerEnabled) {
                System.out.println("\nVerbleibende Zeit: " + getRemainingSeconds() + " Sekunden");
            } else {
                System.out.println("\nModus: Leicht (kein Timer)");
            }

            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            handleCommand(input);
        }
    }

    private void printIntro() {
        System.out.println("Du wachst mitten im Wald auf.");
        System.out.println("Der Boden ist nass, Nebel liegt zwischen den Baeumen.");
        System.out.println("Dein Kopf schmerzt. Du erinnerst dich nur an Bruchstuecke:");
        System.out.println("\"Lauf... bevor es zu spaet ist...\"");
        System.out.println("Irgendwo in diesem Wald gibt es einen Ausgang.");
        System.out.println("Gib 'hilfe' ein, um alle Befehle zu sehen.");
    }

    private void handleCommand(String input) {
        if (input.isEmpty()) {
            System.out.println("Bitte gib einen Befehl ein.");
            return;
        }

        String[] parts = input.split(" ", 2);
        String befehl = parts[0];
        String argument = parts.length > 1 ? parts[1].trim() : "";

        if (directionAliases.containsKey(befehl)) {
            player.move(directionAliases.get(befehl));
            return;
        }

        switch (befehl) {
            case "gehe":
                if (argument.isEmpty()) {
                    System.out.println("Wohin moechtest du gehen?");
                } else {
                    String direction = normalizeDirection(argument);
                    if (direction == null) {
                        System.out.println("Unbekannte Richtung. Nutze norden, sueden, osten, westen oder n/s/o/w.");
                    } else {
                        player.move(direction);
                    }
                }
                break;

            case "nimm":
                if (argument.isEmpty()) {
                    System.out.println("Was moechtest du nehmen?");
                } else {
                    Item item = player.getCurrentRoom().takeItem(argument);
                    if (item != null) {
                        player.getInventory().addItem(item);
                    } else {
                        System.out.println("Dieser Gegenstand ist hier nicht vorhanden.");
                    }
                }
                break;

            case "rede":
                if (argument.isEmpty()) {
                    System.out.println("Mit wem moechtest du reden?");
                } else {
                    NPC npc = player.getCurrentRoom().getNPC(argument);
                    if (npc != null) {
                        npc.talk(player);
                    } else {
                        System.out.println("Diese Person ist nicht hier.");
                    }
                }
                break;

            case "inventar":
            case "i":
                player.getInventory().showItems();
                break;

            case "schau":
                player.getCurrentRoom().showRoom();
                break;

            case "hilfe":
                showHelp();
                break;

            case "beenden":
                running = false;
                System.out.println("Spiel beendet.");
                break;

            default:
                System.out.println("Unbekannter Befehl. Gib 'hilfe' ein.");
        }
    }

    private String normalizeDirection(String input) {
        return directionAliases.get(input.toLowerCase());
    }

    private boolean isTimeOver() {
        return getRemainingSeconds() <= 0;
    }

    private long getRemainingSeconds() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(0, timeLimitSeconds - elapsed);
    }

    private void showHelp() {
        System.out.println("Verfuegbare Befehle:");
        System.out.println("- gehe norden/sueden/osten/westen");
        System.out.println("- n / s / o / w");
        System.out.println("- nimm <gegenstand>");
        System.out.println("- rede <name>");
        System.out.println("- inventar oder i");
        System.out.println("- schau");
        System.out.println("- hilfe");
        System.out.println("- beenden");
    }
}