package DAO;

import java.sql.*;
import java.util.*;

import classe.Commerce;
import classe.Contrat;

/*/ classe DAO de la classe Commerce, permettant son implémentation dans une base de données/*/
public class CommerceDAO {

    private final ContratDAO contratDAO = new ContratDAO();

/*/ méthode qui insert un commerce dans la base SQL /*/
    public void insert(Commerce c) {
        String sql = """
            INSERT INTO Commerce (idCommerce, nom, adresse)
            VALUES (?, ?, ?)
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, c.getIdCommerce());
            ps.setString(2, c.getNom());
            ps.setString(3, c.getAdresse());
            ps.executeUpdate();

            String sqlCat = """
                INSERT INTO Commerce_Categorie
                  (idCommerce, categorie)
                VALUES (?, ?)
            """;
            try (PreparedStatement psCat = conn.prepareStatement(sqlCat)) {
                for (String cat : c.getCategoriesProduits()) {
                    psCat.setInt(1, c.getIdCommerce());
                    psCat.setString(2, cat);
                    psCat.addBatch();
                }
                psCat.executeBatch();
            }

            String sqlContr = """
                INSERT INTO Commerce_Contrat
                  (idCommerce, idContrat)
                VALUES (?, ?)
            """;
            try (PreparedStatement psCon = conn.prepareStatement(sqlContr)) {
                for (Contrat ctr : c.getContrats()) {
                    psCon.setInt(1, c.getIdCommerce());
                    psCon.setInt(2, ctr.getIdContrat());
                    psCon.addBatch();
                }
                psCon.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode mettant à jour un commerce dans la base de données /*/
    public void update(Commerce c) {
        String sql = """
            UPDATE Commerce
               SET nom = ?, adresse = ?
             WHERE idCommerce = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getAdresse());
            ps.setInt(3, c.getIdCommerce());
            ps.executeUpdate();

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM Commerce_Categorie WHERE idCommerce = " + c.getIdCommerce());
                st.executeUpdate("DELETE FROM Commerce_Contrat    WHERE idCommerce = " + c.getIdCommerce());
            }

            insertAssociations(conn, c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertAssociations(Connection conn, Commerce c) throws SQLException {
        String sqlCat = "INSERT INTO Commerce_Categorie (idCommerce, categorie) VALUES (?, ?)";
        try (PreparedStatement psCat = conn.prepareStatement(sqlCat)) {
            for (String cat : c.getCategoriesProduits()) {
                psCat.setInt(1, c.getIdCommerce());
                psCat.setString(2, cat);
                psCat.addBatch();
            }
            psCat.executeBatch();
        }
        String sqlContr = "INSERT INTO Commerce_Contrat (idCommerce, idContrat) VALUES (?, ?)";
        try (PreparedStatement psCon = conn.prepareStatement(sqlContr)) {
            for (Contrat ctr : c.getContrats()) {
                psCon.setInt(1, c.getIdCommerce());
                psCon.setInt(2, ctr.getIdContrat());
                psCon.addBatch();
            }
            psCon.executeBatch();
        }
    }

/*/ méthode supprimant un commerce de la base SQL /*/
    public void delete(int idCommerce) {
        String sql = "DELETE FROM Commerce WHERE idCommerce = ?";
        try (Connection conn = DataBaseManager.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM Commerce_Categorie WHERE idCommerce = " + idCommerce);
                st.executeUpdate("DELETE FROM Commerce_Contrat    WHERE idCommerce = " + idCommerce);
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCommerce);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode récupérant grâce à son ID un commerce dans la base SQL /*/
    public Commerce getById(int idCommerce) {
        Commerce c = null;
        String sql = "SELECT nom, adresse FROM Commerce WHERE idCommerce = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommerce);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                c = new Commerce(idCommerce, rs.getString("nom"), rs.getString("adresse"));

                // catégories
                try (PreparedStatement psCat = conn.prepareStatement(
                        "SELECT categorie FROM Commerce_Categorie WHERE idCommerce = ?")) {
                    psCat.setInt(1, idCommerce);
                    ResultSet rc = psCat.executeQuery();
                    while (rc.next()) {
                        c.ajouterCategorieProduit(rc.getString("categorie"));
                    }
                }

                // contrats
                try (PreparedStatement psCon = conn.prepareStatement(
                        "SELECT idContrat FROM Commerce_Contrat WHERE idCommerce = ?")) {
                    psCon.setInt(1, idCommerce);
                    ResultSet r2 = psCon.executeQuery();
                    while (r2.next()) {
                        Contrat ctr = contratDAO.getById(r2.getInt("idContrat"));
                        if (ctr != null) {
                            c.ajouterContrat(ctr);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

/*/ méthode qui récupère tous les comptes de la base SQL /*/
    public List<Commerce> getAll() {
        List<Commerce> list = new ArrayList<>();
        String sql = "SELECT idCommerce FROM Commerce";
        try (Connection conn = DataBaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idCommerce")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
