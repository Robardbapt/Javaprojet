package DAO;

import java.sql.*;
import java.util.*;
import classe.CategorieProduit;
import classe.Produit;

public class CategorieProduitDAO {

/*/ méthode permettant d'insérer une catégorie produit dans la base de données/*/
	public void insert(CategorieProduit cp) {
	    String sql =
	        "INSERT INTO CategorieProduit (nom, tauxReduction, pointNecessaire) VALUES (?, ?, ?)";
	    String linkSql =
	        "INSERT INTO Produit_Categorie (idProduit, idCategorie) VALUES (?, ?)";

	    try (Connection conn = DataBaseManager.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	         PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {

	        stmt.setString(1, cp.getNom());
	        stmt.setFloat(2, cp.getTauxReduction());
	        stmt.setInt(3, cp.getPointNecessaire());
	        stmt.executeUpdate();

	        // Récupérer l'ID généré automatiquement
	        ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()) {
	            cp.setIdCategorie(rs.getInt(1));
	        }

	        // Insérer les associations avec les produits
	        for (Produit p : cp.getProduits()) {
	            linkStmt.setInt(1, p.getIdProduit());
	            linkStmt.setInt(2, cp.getIdCategorie());
	            linkStmt.addBatch();
	        }
	        linkStmt.executeBatch();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


/*/ méthode permettant de supprimer une catégorie produit de la base de données /*/
    public void delete(int idCategorie) {
        String delLink = "DELETE FROM Produit_Categorie WHERE idCategorie = ?";
        String delCat  = "DELETE FROM CategorieProduit WHERE idCategorie = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(delLink);
             PreparedStatement stmt2 = conn.prepareStatement(delCat)) {

            stmt1.setInt(1, idCategorie);
            stmt1.executeUpdate();
            stmt2.setInt(1, idCategorie);
            stmt2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode permettant de mettre à jour une catégorie produit dans la base de données /*/
    public void update(CategorieProduit cp) {
        String sql = 
            "UPDATE CategorieProduit " +
            "SET nom = ?, tauxReduction = ?, pointNecessaire = ? " +
            "WHERE idCategorie = ?";
        String delLink = "DELETE FROM Produit_Categorie WHERE idCategorie = ?";
        String linkSql = 
            "INSERT INTO Produit_Categorie (idProduit, idCategorie) VALUES (?, ?)";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement stmtDel = conn.prepareStatement(delLink);
             PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {

            stmt.setString(1, cp.getNom());
            stmt.setFloat(2, cp.getTauxReduction());
            stmt.setInt(3, cp.getPointNecessaire());
            stmt.setInt(4, cp.getIdCategorie());
            stmt.executeUpdate();

            stmtDel.setInt(1, cp.getIdCategorie());
            stmtDel.executeUpdate();

            for (Produit p : cp.getProduits()) {
                linkStmt.setInt(1, p.getIdProduit());
                linkStmt.setInt(2, cp.getIdCategorie());
                linkStmt.addBatch();
            }
            linkStmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode récupérant une catégorie produit dans la base de données grâce à son ID /*/
    public CategorieProduit getById(int idCategorie) {
        CategorieProduit cp = null;
        String catSql = 
            "SELECT * FROM CategorieProduit WHERE idCategorie = ?";
        String prodSql = 
            "SELECT p.idProduit, p.nom, p.prix, p.dateAchat " +
            "FROM Produit p " +
            "JOIN Produit_Categorie pc ON p.idProduit = pc.idProduit " +
            "WHERE pc.idCategorie = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement catStmt = conn.prepareStatement(catSql);
             PreparedStatement prodStmt = conn.prepareStatement(prodSql)) {

            catStmt.setInt(1, idCategorie);
            ResultSet rs = catStmt.executeQuery();
            if (rs.next()) {
                cp = new CategorieProduit(
                    rs.getInt("idCategorie"),
                    rs.getString("nom"),
                    rs.getFloat("tauxReduction"),
                    rs.getInt("pointNecessaire")
                );
                prodStmt.setInt(1, idCategorie);
                ResultSet prs = prodStmt.executeQuery();
                while (prs.next()) {
                    Produit p = new Produit(
                        prs.getInt("idProduit"),
                        prs.getString("nom"),
                        prs.getFloat("prix"),
                        prs.getDate("dateAchat")
                    );
                    cp.ajouterProduit(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cp;
    }

/*/ méthode qui récupère toutes les catégories produits dans la base de données dans une liste /*/
    public List<CategorieProduit> getAll() {
        List<CategorieProduit> list = new ArrayList<>();
        String sql = "SELECT idCategorie FROM CategorieProduit";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idCategorie")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
