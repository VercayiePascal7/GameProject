package org.example;

public class HintTalkStrategy implements TalkStrategy {

    @Override
    public String getDialogue(Player player) {
        if (player.getInventory().hasItem("schluessel")) {
            return "Gut. Du hast den Schluessel. Kehre zur Waldlichtung zurueck und gehe nach Osten zum Tor.";
        }

        return "Du bist falsch gelaufen. Der Schluessel liegt beim alten Baum. Gehe von der Waldlichtung zweimal nach Norden.";
    }
}