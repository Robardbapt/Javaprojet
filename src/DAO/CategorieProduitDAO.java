package DAO;

import java.sql.*;
import java.util.*;
import classe.CategorieProduit;
import classe.Produit;

public class CategorieProduitDAO {

/*/ méthode permettant d'insérer une catégorie produit dans la base de données/*/
	public void insert(CategorieProduit cp, int idCentre) {
	    String sql = """
	        INSERT INTO categorieproduit (nom, tauxReduction, pointNecessaire, id_centre_tri)
	        VALUES (?, ?, ?, ?)
	    """;
	    String linkSql = """
	        INSERT INTO produit_categorie (idProduit, idCategorie)
	        VALUES (?, ?)
	    """;

	    try (Connection conn = DataBaseManager.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, cp.getNom());
	        stmt.setFloat(2, cp.getTauxReduction());
	        stmt.setInt(3, cp.getPointNecessaire());
	        stmt.setInt(4, idCentre);
	        stmt.executeUpdate();

	        // 🔁 Récupération de l'id généré
	        ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()) {
	            int idCategorie = rs.getInt(1);
	            cp.setIdCategorie(idCategorie);

	            // 🔗 Liens vers les produits
	            try (PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
	                for (Produit p : cp.getProduits()) {
	                    linkStmt.setInt(1, p.getIdProduit());
	                    linkStmt.setInt(2, idCategorie);
	                    linkStmt.addBatch();
	                }
	                linkStmt.executeBatch();
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}



/*/ méthode permettant de supprimer une catégorie produit de la base de données /*/
	/**
	 * Supprime une catégorie produit si elle n'est plus liée à aucun commerce,
	 * en supprimant aussi les produits associés (avec toutes les contraintes).
	 */
	public void delete(int idCategorie) {
	    try (Connection conn = DataBaseManager.getConnection()) {
	        conn.setAutoCommit(false);

	        // 1. Supprimer les produits associés manuellement
	        String selectProd = "SELECT idProduit FROM produit_categorie WHERE idCategorie = ?";
	        try (PreparedStatement ps = conn.prepareStatement(selectProd)) {
	            ps.setInt(1, idCategorie);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                int idProd = rs.getInt("idProduit");

	                // Supprimer dans compte_produit
	                try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM compte_produit WHERE idProduit = ?")) {
	                    psDel.setInt(1, idProd);
	                    psDel.executeUpdate();
	                }

	                // Supprimer le produit lui-même
	                try (PreparedStatement deleteProd = conn.prepareStatement("DELETE FROM produit WHERE idProduit = ?")) {
	                    deleteProd.setInt(1, idProd);
	                    deleteProd.executeUpdate();
	                }
	            }
	        }

	        // 2. Supprimer toutes les liaisons directes
	        try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM produit_categorie WHERE idCategorie = ?");
	             PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM commerce_categorie WHERE idCategorie = ?");
	             PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM contrat_categorie WHERE idCategorie = ?");
	             PreparedStatement stmt4 = conn.prepareStatement("DELETE FROM categorieproduit WHERE idCategorie = ?")) {

	            for (PreparedStatement stmt : List.of(stmt1, stmt2, stmt3, stmt4)) {
	                stmt.setInt(1, idCategorie);
	                stmt.executeUpdate();
	            }
	        }

	        conn.commit();

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
    
    public int getIdByNomEtCentre(String nom, int idCentre) {
        String sql = "SELECT idCategorie FROM categorieproduit WHERE nom = ? AND id_centre_tri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setInt(2, idCentre);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCategorie");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si aucune catégorie trouvée
    }

}
