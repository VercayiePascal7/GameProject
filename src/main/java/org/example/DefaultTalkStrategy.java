package org.example;

// Standard-Implementierung einer Gesprächsstrategie
public class DefaultTalkStrategy implements TalkStrategy {
    private final String dialogue; // Fester Dialogtext

    // Konstruktor setzt den Dialog
    public DefaultTalkStrategy(String dialogue) {
        this.dialogue = dialogue;
    }

    // Gibt den Dialog zurück (unabhängig vom Spieler)
    @Override
    public String getDialogue(Player player) {
        return dialogue;
    }
}