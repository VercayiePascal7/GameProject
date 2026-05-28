package org.example;

// Nicht-Spieler-Charakter (NPC)
public class NPC {
    private String name;
    private TalkStrategy talkStrategy;

    // Konstruktor
    public NPC(String name, TalkStrategy talkStrategy) {
        this.name = name;
        this.talkStrategy = talkStrategy;
    }

    // NPC spricht mit dem Spieler
    public void talk(Player player) {
        System.out.println(name + ": " + talkStrategy.getDialogue(player));
    }

    // Namen zurückgeben
    public String getName() {
        return name;
    }
}