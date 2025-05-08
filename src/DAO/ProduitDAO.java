package DAO;

import java.sql.*;
import java.util.*;
import classe.Produit;
import classe.CategorieProduit;

/*/ classe DAO de la classe Produit, permettant son implémentation dans une base de données/*/
public class ProduitDAO {

/*/ méthode qui insert un produit dans la base de données/*/
    public void insert(Produit p) {
        String sql = "INSERT INTO Produit (idProduit, nom, prix, dateAchat) VALUES (?, ?, ?, ?)";
        String linkSql = "INSERT INTO Produit_Categorie (idProduit, idCategorie) VALUES (?, ?)";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {

            stmt.setInt(1, p.getIdProduit());
            stmt.setString(2, p.getNom());
            stmt.setFloat(3, p.getPrix());
            stmt.setDate(4, new java.sql.Date(p.getDateAchat().getTime()));
            stmt.executeUpdate();

            for (CategorieProduit cat : p.getCategorieProduit()) {
                linkStmt.setInt(1, p.getIdProduit());
                linkStmt.setInt(2, cat.getIdCategorie());
                linkStmt.addBatch();
            }
            linkStmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode supprimant un produit de la base de données /*/
    public void delete(int idProduit) {
        String delLink = "DELETE FROM Produit_Categorie WHERE idProduit = ?";
        String delProd = "DELETE FROM Produit WHERE idProduit = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(delLink);
             PreparedStatement stmt2 = conn.prepareStatement(delProd)) {

            stmt1.setInt(1, idProduit);
            stmt1.executeUpdate();
            stmt2.setInt(1, idProduit);
            stmt2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode mettant à jour un produit dans la base de données /*/
    public void update(Produit p) {
        String sql = "UPDATE Produit SET nom=?, prix=?, dateAchat=? WHERE idProduit=?";
        String delLink = "DELETE FROM Produit_Categorie WHERE idProduit = ?";
        String linkSql = "INSERT INTO Produit_Categorie (idProduit, idCategorie) VALUES (?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement stmtDel = conn.prepareStatement(delLink);
             PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {

            stmt.setString(1, p.getNom());
            stmt.setFloat(2, p.getPrix());
            stmt.setDate(3, new java.sql.Date(p.getDateAchat().getTime()));
            stmt.setInt(4, p.getIdProduit());
            stmt.executeUpdate();

            stmtDel.setInt(1, p.getIdProduit());
            stmtDel.executeUpdate();

            for (CategorieProduit cat : p.getCategorieProduit()) {
                linkStmt.setInt(1, p.getIdProduit());
                linkStmt.setInt(2, cat.getIdCategorie());
                linkStmt.addBatch();
            }
            linkStmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode récupérant un produit grâce à son ID dans la base de données /*/
    public Produit getById(int idProduit) {
        Produit p = null;
        String prodSql = "SELECT * FROM Produit WHERE idProduit = ?";
        String catSql = 
            "SELECT c.idCategorie, c.nom, c.tauxReduction, c.pointNecessaire " +
            "FROM CategorieProduit c " +
            "JOIN Produit_Categorie pc ON c.idCategorie = pc.idCategorie " +
            "WHERE pc.idProduit = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement prodStmt = conn.prepareStatement(prodSql);
             PreparedStatement catStmt = conn.prepareStatement(catSql)) {

            prodStmt.setInt(1, idProduit);
            ResultSet rs = prodStmt.executeQuery();
            if (rs.next()) {
                p = new Produit(
                    rs.getInt("idProduit"),
                    rs.getString("nom"),
                    rs.getFloat("prix"),
                    rs.getDate("dateAchat")
                );
                catStmt.setInt(1, idProduit);
                ResultSet crs = catStmt.executeQuery();
                while (crs.next()) {
                    CategorieProduit cat = new CategorieProduit(
                        crs.getInt("idCategorie"),
                        crs.getString("nom"),
                        crs.getFloat("tauxReduction"),
                        crs.getInt("pointNecessaire")
                    );
                    p.ajouterCategorie(cat);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

/*/ méthode récupérant tous les produits dans la base de données /*/
    public List<Produit> getAll() {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT idProduit FROM Produit";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(getById(rs.getInt("idProduit")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

/*/ méthode récupérant tous les produits d'une catégorieProduit/*/
    public List<Produit> getByCategorieId(int idCategorie) {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT idProduit FROM Produit_Categorie WHERE idCategorie = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCategorie);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getById(rs.getInt("idProduit")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
