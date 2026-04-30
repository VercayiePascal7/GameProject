package org.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TestCommandParser {

    private Player player;
    private Room room;
    private Inventory inventory;
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        player = mock(Player.class);
        room = mock(Room.class);
        inventory = mock(Inventory.class);

        when(player.getCurrentRoom()).thenReturn(room);
        when(player.getInventory()).thenReturn(inventory);
        when(player.hasEscaped()).thenReturn(false);

        parser = new CommandParser(player, 0);
    }

    @Test
    void n_sollteSpielerNachNordenBewegen() {
        parserTestHelper("n");

        verify(player).move("norden");
    }

    @Test
    void geheNorden_sollteMoveAufrufen() {
        parserTestHelper("gehe norden");

        verify(player).move("norden");
    }

    @Test
    void geheUnbekannt_sollteNichtBewegen() {
        parserTestHelper("gehe oben");

        verify(player, never()).move(anyString());
    }

    @Test
    void inventar_sollteShowItemsAufrufen() {
        parserTestHelper("inventar");

        verify(inventory).showItems();
    }

    @Test
    void i_sollteShowItemsAufrufen() {
        parserTestHelper("i");

        verify(inventory).showItems();
    }

    @Test
    void schau_sollteShowRoomAufrufen() {
        parserTestHelper("schau");

        verify(room).showRoom();
    }

    @Test
    void nimmItem_sollteItemZumInventarHinzufuegen() {
        Item item = mock(Item.class);
        when(room.takeItem("schwert")).thenReturn(item);

        parserTestHelper("nimm schwert");

        verify(inventory).addItem(item);
    }

    @Test
    void nimmUnbekanntesItem_sollteNichtHinzufuegen() {
        when(room.takeItem("stein")).thenReturn(null);

        parserTestHelper("nimm stein");

        verify(inventory, never()).addItem(any());
    }

    @Test
    void redeMitNpc_sollteTalkAufrufen() {
        NPC npc = mock(NPC.class);
        when(room.getNPC("opa")).thenReturn(npc);

        parserTestHelper("rede opa");

        verify(npc).talk(player);
    }

    @Test
    void benutzeKarte_mitKarte_sollteInventarPruefen() {
        when(inventory.hasItem("karte")).thenReturn(true);

        parserTestHelper("benutze karte");

        verify(inventory).hasItem("karte");
    }

    @Test
    void benutzeKarte_ohneKarte_sollteInventarPruefen() {
        when(inventory.hasItem("karte")).thenReturn(false);

        parserTestHelper("benutze karte");

        verify(inventory).hasItem("karte");
    }

    @Test
    void benutzeSchluessel_sollteInventarPruefen() {
        when(inventory.hasItem("schluessel")).thenReturn(true);

        parserTestHelper("benutze schluessel");

        verify(inventory).hasItem("schluessel");
    }

    private void parserTestHelper(String input) {
        try {
            var method = CommandParser.class.getDeclaredMethod("handleCommand", String.class);
            method.setAccessible(true);
            method.invoke(parser, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}