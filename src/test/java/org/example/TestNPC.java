package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestNPC {

    @Test
    public void testNPCName() {
        NPC npc = new NPC("Eron", new DefaultTalkStrategy("Hallo."));

        assertEquals("Eron", npc.getName());
    }

    @Test
    public void testDefaultTalkStrategy() {
        Player player = new Player();
        TalkStrategy strategy = new DefaultTalkStrategy("Hallo Reisender.");

        assertEquals("Hallo Reisender.", strategy.getDialogue(player));
    }

    @Test
    public void testHintTalkStrategyWithoutKey() {
        Player player = new Player();
        TalkStrategy strategy = new HintTalkStrategy();

        assertEquals(
                "Ich habe gehoert, in der Hoehle liegt ein alter Schluessel...",
                strategy.getDialogue(player)
        );
    }

    @Test
    public void testHintTalkStrategyWithKey() {
        Player player = new Player();
        player.getInventory().addItem(new Item("schluessel", "Ein alter Schluessel."));

        TalkStrategy strategy = new HintTalkStrategy();

        assertEquals(
                "Du hast bereits den Schluessel. Gehe nach Osten zum Ausgang!",
                strategy.getDialogue(player)
        );
    }
}