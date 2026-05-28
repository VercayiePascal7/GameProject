package org.example;

// Strategie-Interface für Dialoge von NPCs
public interface TalkStrategy {
    String getDialogue(Player player); // Gibt einen Dialog abhängig vom Spieler zurück
}