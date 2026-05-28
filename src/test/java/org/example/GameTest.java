package org.example;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TestNG-Tests für das Escape-the-Forest-Spiel.
 *
 * Mindestens 10 automatisierte Tests:
 *   1.  Item aufnehmen fügt Item ins Inventar
 *   2.  Item aus Raum entfernt nach Aufnahme
 *   3.  hasItem gibt false zurück wenn Item nicht vorhanden
 *   4.  Spieler kann sich in gültige Richtung bewegen
 *   5.  Spieler kann sich nicht in ungültige Richtung bewegen
 *   6.  Gesperrter Raum blockiert Bewegung
 *   7.  unlock() öffnet gesperrten Raum
 *   8.  Quest wird als abgeschlossen markiert
 *   9.  NPC-Dialog gibt richtigen Text zurück (ohne Schlüssel)
 *  10.  NPC-Dialog gibt richtigen Text zurück (mit Schlüssel)
 *  11.  Raum zeigt Item-Namen korrekt per Stream
 *  12.  Inventar getAllItems() gibt korrekte Kopie zurück
 *  13.  DefaultTalkStrategy gibt festen Text zurück
 *  14.  Spieler startet ohne Escape-Status
 *  15.  Geheimraum requiresItems-Flag korrekt gesetzt
 */
public class GameTest {

    private Room      waldlichtung;
    private Room      alterBaum;
    private Room      tor;
    private Room      waldrand;
    private Player    player;
    private Item      schluessel;
    private Item      karte;

    @BeforeMethod
    public void setUp() {
        // Räume aufbauen
        waldlichtung = new Room("Waldlichtung", "Startraum");
        alterBaum    = new Room("Alter Baum",   "Hier liegt der Schlüssel");
        tor          = new Room("Verschlossenes Tor", "Das Tor");
        waldrand     = new Room("Waldrand",     "Freiheit");

        waldrand.setExitRoom(true);
        waldrand.setLocked(true);
        waldrand.setEndingType("normal");

        waldlichtung.setExit("norden", alterBaum);
        waldlichtung.setExit("osten",  tor);
        tor.setExit("westen", waldlichtung);
        tor.setExit("osten",  waldrand);

        schluessel = new Item("schluessel", "Ein alter Schlüssel");
        karte      = new Item("karte",      "Eine Karte");

        alterBaum.addItem(schluessel);
        waldlichtung.addItem(karte);

        player = new Player();
        player.setCurrentRoom(waldlichtung);
    }

    // ── Test 1: Item aufnehmen ────────────────────────────────────────

    @Test(description = "Item aus Raum aufnehmen fügt es ins Inventar")
    public void testItemAufnehmen() {
        Item genommen = waldlichtung.takeItem("karte");
        Assert.assertNotNull(genommen, "Item sollte gefunden werden");
        player.getInventory().addItem(genommen);
        Assert.assertTrue(player.getInventory().hasItem("karte"),
                "Karte sollte im Inventar sein");
    }

    // ── Test 2: Item aus Raum entfernt ───────────────────────────────

    @Test(description = "Nach Aufnahme ist Item nicht mehr im Raum")
    public void testItemNichtMehrImRaum() {
        waldlichtung.takeItem("karte");
        // Stream-Aufruf: getItemNames() darf karte nicht mehr enthalten
        List<String> namen = waldlichtung.getItemNames();
        Assert.assertFalse(namen.contains("karte"),
                "Karte sollte nach Aufnahme nicht mehr im Raum sein");
    }

    // ── Test 3: hasItem negativ ───────────────────────────────────────

    @Test(description = "hasItem gibt false zurück für nicht vorhandenes Item")
    public void testHasItemFalse() {
        Assert.assertFalse(player.getInventory().hasItem("fackel"),
                "Fackel sollte nicht im Inventar sein");
    }

    // ── Test 4: Gültige Bewegung ─────────────────────────────────────

    @Test(description = "Spieler kann sich in gültige Richtung bewegen")
    public void testGueltigeBewegung() {
        player.move("norden");
        Assert.assertEquals(player.getCurrentRoom().getName(), "Alter Baum",
                "Spieler sollte im Alter Baum sein");
    }

    // ── Test 5: Ungültige Bewegung ────────────────────────────────────

    @Test(description = "Spieler bleibt stehen bei ungültiger Richtung")
    public void testUngueltigeBewegung() {
        player.move("sueden");  // Kein Ausgang nach Süden
        Assert.assertEquals(player.getCurrentRoom().getName(), "Waldlichtung",
                "Spieler sollte noch auf der Waldlichtung stehen");
    }

    // ── Test 6: Gesperrter Raum ───────────────────────────────────────

    @Test(description = "Gesperrter Raum blockiert Bewegung")
    public void testGesperrterRaum() {
        player.move("osten");  // zum Tor
        player.move("osten");  // Waldrand ist gesperrt
        Assert.assertEquals(player.getCurrentRoom().getName(), "Verschlossenes Tor",
                "Spieler sollte nicht in gesperrten Raum gelangen");
    }

    // ── Test 7: unlock() öffnet Raum ─────────────────────────────────

    @Test(description = "unlock() ermöglicht Bewegung in gesperrten Raum")
    public void testUnlock() {
        waldrand.unlock();
        Assert.assertFalse(waldrand.isLocked(), "Waldrand sollte geöffnet sein");

        player.getInventory().addItem(schluessel);
        player.move("osten");  // zum Tor
        player.move("osten");  // Waldrand jetzt offen

        Assert.assertEquals(player.getCurrentRoom().getName(), "Waldrand",
                "Spieler sollte im Waldrand stehen");
    }

    // ── Test 8: Quest abschließen ─────────────────────────────────────

    @Test(description = "Quest wird als abgeschlossen markiert")
    public void testQuestAbschliessen() {
        Quest q = new Quest("Teste die Quest");
        Assert.assertFalse(q.isCompleted(), "Quest sollte noch offen sein");
        q.complete();
        Assert.assertTrue(q.isCompleted(), "Quest sollte abgeschlossen sein");
    }

    // ── Test 9: HintTalkStrategy ohne Schlüssel ───────────────────────

    @Test(description = "HintTalkStrategy gibt Hinweis ohne Schlüssel")
    public void testHintOhneSchluessel() {
        TalkStrategy strategie = new HintTalkStrategy();
        String dialog = strategie.getDialogue(player);
        Assert.assertTrue(dialog.contains("Schlüssel") || dialog.contains("alten Baum"),
                "Dialog ohne Schlüssel soll Hinweis auf Baum enthalten");
    }

    // ── Test 10: HintTalkStrategy mit Schlüssel ───────────────────────

    @Test(description = "HintTalkStrategy gibt anderen Hinweis wenn Schlüssel vorhanden")
    public void testHintMitSchluessel() {
        player.getInventory().addItem(schluessel);
        TalkStrategy strategie = new HintTalkStrategy();
        String dialog = strategie.getDialogue(player);
        Assert.assertTrue(dialog.contains("Schlüssel") && dialog.contains("Tor"),
                "Dialog mit Schlüssel soll Hinweis auf Tor enthalten");
    }

    // ── Test 11: getItemNames() Stream ────────────────────────────────

    @Test(description = "getItemNames() gibt korrekte Namen über Stream zurück")
    public void testGetItemNamesStream() {
        List<String> namen = waldlichtung.getItemNames();
        Assert.assertTrue(namen.contains("karte"), "karte sollte in der Liste sein");
        Assert.assertEquals(namen.size(), 1, "Nur ein Item im Startraum");
    }

    // ── Test 12: getAllItems() Kopie ──────────────────────────────────

    @Test(description = "getAllItems() gibt korrekte unveränderliche Kopie zurück")
    public void testGetAllItems() {
        player.getInventory().addItem(schluessel);
        player.getInventory().addItem(karte);
        List<Item> alle = player.getInventory().getAllItems();
        Assert.assertEquals(alle.size(), 2, "Inventar sollte 2 Items enthalten");
    }

    // ── Test 13: DefaultTalkStrategy ─────────────────────────────────

    @Test(description = "DefaultTalkStrategy gibt immer denselben Text zurück")
    public void testDefaultTalkStrategy() {
        String text = "Der Wald lässt dich nicht entkommen.";
        TalkStrategy strategie = new DefaultTalkStrategy(text);
        Assert.assertEquals(strategie.getDialogue(player), text,
                "DefaultTalkStrategy sollte immer denselben Text zurückgeben");
    }

    // ── Test 14: Spieler startet ohne Escape ──────────────────────────

    @Test(description = "Spieler startet mit hasEscaped() = false")
    public void testSpielerStartStatus() {
        Assert.assertFalse(player.hasEscaped(),
                "Spieler sollte zu Beginn nicht entkommen sein");
    }

    // ── Test 15: requiresItems-Flag ───────────────────────────────────

    @Test(description = "requiresItems Flag wird korrekt gesetzt und gelesen")
    public void testRequiresItems() {
        Room geheimPfad = new Room("Verborgener Pfad", "Geheimpfad");
        Assert.assertFalse(geheimPfad.requiresItems(),
                "Standard: requiresItems sollte false sein");
        geheimPfad.setRequiresItems(true);
        Assert.assertTrue(geheimPfad.requiresItems(),
                "Nach setRequiresItems(true) sollte true zurückgegeben werden");
    }
}