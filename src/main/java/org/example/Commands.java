package org.example.Commands;

import org.example.Player;
import org.example.Item;
import org.example.NPC;

/**
 * Sammlung konkreter Befehlsklassen.
 *
 * Design Pattern: Command Pattern
 * Jeder Spielerbefehl wird als eigenes Objekt gekapselt.
 * Das erlaubt einfache Erweiterbarkeit (z.B. Undo) und
 * saubere Trennung der Logik vom Parser.
 */
public class Commands {

    // ── Bewegungsbefehl ───────────────────────────────────────────────

    /**
     * Bewegt den Spieler in eine bestimmte Richtung.
     */
    public static class MoveCommand implements org.example.Command {
        private final Player player;
        private final String direction;

        public MoveCommand(Player player, String direction) {
            this.player    = player;
            this.direction = direction;
        }

        @Override
        public void execute() {
            player.move(direction);
        }
    }

    // ── Aufnahme-Befehl ───────────────────────────────────────────────

    /**
     * Nimmt einen Gegenstand aus dem aktuellen Raum auf.
     */
    public static class TakeCommand implements org.example.Command {
        private final Player player;
        private final String itemName;

        public TakeCommand(Player player, String itemName) {
            this.player   = player;
            this.itemName = itemName;
        }

        @Override
        public void execute() {
            Item item = player.getCurrentRoom().takeItem(itemName);
            if (item != null) {
                player.getInventory().addItem(item);
                if (item.getName().equals("schluessel")) {
                    System.out.println("Tipp: Gehe zum verschlossenen Tor und benutze den Schlüssel!");
                }
                if (item.getName().equals("fackel")) {
                    System.out.println("Tipp: Mit der Fackel kannst du dunkle Orte beleuchten.");
                }
            } else {
                System.out.println("Diesen Gegenstand gibt es hier nicht.");
            }
        }
    }

    // ── Rede-Befehl ───────────────────────────────────────────────────

    /**
     * Spricht mit einem NPC im aktuellen Raum.
     */
    public static class TalkCommand implements org.example.Command {
        private final Player player;
        private final String npcName;

        public TalkCommand(Player player, String npcName) {
            this.player  = player;
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

    // ── Inventar-Befehl ───────────────────────────────────────────────

    /**
     * Zeigt das aktuelle Inventar an.
     */
    public static class InventoryCommand implements org.example.Command {
        private final Player player;

        public InventoryCommand(Player player) {
            this.player = player;
        }

        @Override
        public void execute() {
            player.getInventory().showItems();
        }
    }

    // ── Schau-Befehl ──────────────────────────────────────────────────

    /**
     * Beschreibt den aktuellen Raum erneut.
     */
    public static class LookCommand implements org.example.Command {
        private final Player player;

        public LookCommand(Player player) {
            this.player = player;
        }

        @Override
        public void execute() {
            player.getCurrentRoom().showRoom();
        }
    }
}