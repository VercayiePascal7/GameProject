package org.example;

public class HintTalkStrategy implements TalkStrategy {

    @Override
    public String getDialogue(Player player) {
        if (player.getInventory().hasItem("Schluessel")) {
            return "Du hast bereits den Schluessel. Suche den Ausgang im Osten!";
        }
        return "In der Hoehle soll ein alter Schluessel liegen...";
    }
}