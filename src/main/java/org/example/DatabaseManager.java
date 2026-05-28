package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet die MariaDB-Datenbankverbindung.
 *
 * Design Pattern: Singleton - es gibt immer nur eine Datenbankverbindung
 * im gesamten Programm.
 *
 * Tabellen:
 *   savegames  - gespeicherte Spielstaende
 *   inventory  - Inventar-Items pro Spielstand
 *   highscores - abgeschlossene Spiele mit verbleibender Zeit
 *
 * Voraussetzung: MariaDB laeuft lokal, Datenbank "escape_forest" existiert.
 * SQL zum Erstellen der Datenbank:
 *   CREATE DATABASE escape_forest;
 */
public class DatabaseManager {

    // ── Singleton ────────────────────────────────────────────────────
    private static DatabaseManager instance;

    // Verbindungsdaten - bei Bedarf anpassen
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "escape_forest";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123";

    private static final String DB_URL =
            "jdbc:mariadb://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                    + "?useUnicode=true&characterEncoding=UTF-8";

    private Connection connection;

    /** Privater Konstruktor - kein direktes new DatabaseManager() erlaubt. */
    private DatabaseManager() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            connection.setAutoCommit(true);
            createTables();
            System.out.println("Datenbank verbunden: " + DB_NAME);
        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB-Treiber nicht gefunden: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Datenbankfehler: " + e.getMessage());
            System.err.println("Stelle sicher dass MariaDB laeuft und die Datenbank existiert:");
            System.err.println("  CREATE DATABASE escape_forest;");
        }
    }

    /** Gibt die einzige Instanz zurueck (erstellt sie beim ersten Aufruf). */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // ── Tabellen erstellen ────────────────────────────────────────────

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS savegames (" +
                            "    id           INT PRIMARY KEY AUTO_INCREMENT," +
                            "    save_name    VARCHAR(100) NOT NULL UNIQUE," +
                            "    current_room VARCHAR(100) NOT NULL," +
                            "    elapsed_secs INT          NOT NULL DEFAULT 0," +
                            "    difficulty   VARCHAR(50)  NOT NULL DEFAULT 'Mittel'," +
                            "    saved_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS inventory (" +
                            "    id        INT PRIMARY KEY AUTO_INCREMENT," +
                            "    save_name VARCHAR(100) NOT NULL," +
                            "    item_name VARCHAR(100) NOT NULL," +
                            "    FOREIGN KEY (save_name) REFERENCES savegames(save_name) ON DELETE CASCADE" +
                            ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS highscores (" +
                            "    id            INT PRIMARY KEY AUTO_INCREMENT," +
                            "    player_name   VARCHAR(100) NOT NULL," +
                            "    difficulty    VARCHAR(50)  NOT NULL," +
                            "    remaining_sec INT          NOT NULL," +
                            "    ended_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
        }
    }

    // ── Speichern ─────────────────────────────────────────────────────

    public void saveGame(String saveName, String currentRoom,
                         int elapsedSecs, String difficulty,
                         List<String> items) {
        if (connection == null) return;
        try {
            try (PreparedStatement del = connection.prepareStatement(
                    "DELETE FROM savegames WHERE save_name = ?")) {
                del.setString(1, saveName);
                del.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO savegames (save_name, current_room, elapsed_secs, difficulty) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, saveName);
                ps.setString(2, currentRoom);
                ps.setInt(3, elapsedSecs);
                ps.setString(4, difficulty);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO inventory (save_name, item_name) VALUES (?, ?)")) {
                for (String item : items) {
                    ps.setString(1, saveName);
                    ps.setString(2, item);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            System.out.println("Spielstand '" + saveName + "' gespeichert.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Speichern: " + e.getMessage());
        }
    }

    // ── Laden ─────────────────────────────────────────────────────────

    public SaveData loadGame(String saveName) {
        if (connection == null) return null;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT current_room, elapsed_secs, difficulty FROM savegames WHERE save_name = ?")) {
            ps.setString(1, saveName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Kein Spielstand mit dem Namen '" + saveName + "' gefunden.");
                return null;
            }
            String room       = rs.getString("current_room");
            int    elapsed    = rs.getInt("elapsed_secs");
            String diff       = rs.getString("difficulty");
            List<String> items = loadInventory(saveName);
            System.out.println("Spielstand '" + saveName + "' geladen.");
            return new SaveData(saveName, room, elapsed, diff, items);
        } catch (SQLException e) {
            System.err.println("Fehler beim Laden: " + e.getMessage());
            return null;
        }
    }

    private List<String> loadInventory(String saveName) throws SQLException {
        List<String> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT item_name FROM inventory WHERE save_name = ?")) {
            ps.setString(1, saveName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("item_name"));
            }
        }
        return items;
    }

    // ── Spielstaende auflisten ────────────────────────────────────────

    public List<String> listSaveGames() {
        List<String> names = new ArrayList<>();
        if (connection == null) return names;
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(
                     "SELECT save_name, current_room, saved_at FROM savegames ORDER BY saved_at DESC")) {
            while (rs.next()) {
                names.add(String.format("  %-15s | Raum: %-25s | %s",
                        rs.getString("save_name"),
                        rs.getString("current_room"),
                        rs.getString("saved_at")));
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Auflisten: " + e.getMessage());
        }
        return names;
    }

    // ── Highscore ─────────────────────────────────────────────────────

    public void addHighscore(String playerName, String difficulty, int remainingSec) {
        if (connection == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO highscores (player_name, difficulty, remaining_sec) VALUES (?, ?, ?)")) {
            ps.setString(1, playerName);
            ps.setString(2, difficulty);
            ps.setInt(3, remainingSec);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Fehler beim Highscore-Eintrag: " + e.getMessage());
        }
    }

    public List<String> getHighscores() {
        List<String> scores = new ArrayList<>();
        if (connection == null) return scores;
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(
                     "SELECT player_name, difficulty, remaining_sec, ended_at " +
                             "FROM highscores ORDER BY remaining_sec DESC LIMIT 5")) {
            int rang = 1;
            while (rs.next()) {
                scores.add(String.format("  #%d  %-15s | %s | %d Sek. uebrig | %s",
                        rang++,
                        rs.getString("player_name"),
                        rs.getString("difficulty"),
                        rs.getInt("remaining_sec"),
                        rs.getString("ended_at")));
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei Highscores: " + e.getMessage());
        }
        return scores;
    }

    // ── Verbindung schliessen ─────────────────────────────────────────

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Schliessen: " + e.getMessage());
        }
    }
}