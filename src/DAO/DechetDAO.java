package DAO;

import java.sql.*;
import java.util.*;
import classe.Dechet;
import classe.Contenu;

/*/ classe DAO de la classe Dechet, permettant son implémentation dans une base de données/*/
public class DechetDAO {

/*/ méthode insérant un déchet dans la base SQL /*/
    public void insert(Dechet d) {
        String sql = """
            INSERT INTO Dechet (nom, contenu, masse)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE contenu = VALUES(contenu), masse = VALUES(masse)
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNom());
            stmt.setString(2, d.getContenu().name());
            stmt.setFloat(3, d.getMasse());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode mettant à jour un déchet dans la base SQL /*/
    public void update(Dechet d) {
        String sql = """
            UPDATE Dechet
               SET contenu = ?, masse = ?
             WHERE nom = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getContenu().name());
            stmt.setFloat(2, d.getMasse());
            stmt.setString(3, d.getNom());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode supprimant un déchet de la base SQL /*/
    public void delete(String nom) {
        String sql = "DELETE FROM Dechet WHERE nom = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode récupérant un déchet par son nom /*/
    public Dechet getByNom(String nom) {
        String sql = "SELECT nom, contenu, masse FROM Dechet WHERE nom = ?";
        Dechet d = null;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                d = new Dechet(
                    rs.getString("nom"),
                    Contenu.valueOf(rs.getString("contenu")),
                    rs.getFloat("masse")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

/*/ méthode récupérant tous les déchets de la base de données /*/
    public List<Dechet> getAll() {
        List<Dechet> list = new ArrayList<>();
        String sql = "SELECT nom, contenu, masse FROM Dechet";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Dechet(
                    rs.getString("nom"),
                    Contenu.valueOf(rs.getString("contenu")),
                    rs.getFloat("masse")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
