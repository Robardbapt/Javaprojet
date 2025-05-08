package DAO;

import java.sql.*;
import java.util.*;

import classe.Compte;
import classe.Poubelle;
import classe.Produit;
import classe.BonReduction;

/*/ classe DAO de la classe Compte, permettant son implémentation dans une base de données/*/
public class CompteDAO {

    private final PoubelleDAO poubelleDAO    = new PoubelleDAO();
    private final ProduitDAO produitDAO      = new ProduitDAO();
    private final BonReductionDAO bonDAO     = new BonReductionDAO();

/*/ méthode qui insert un compte dans la base SQL /*/
    public void insert(Compte c) {
        String sql = """
            INSERT INTO Compte
              (idCompte, nom, email, motDePasse, pointFidelite, adresse)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, c.getIdCompte());
            stmt.setString(2, c.getNom());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getMotDePasse());
            stmt.setFloat(5, c.getPointFidelite());
            stmt.setString(6, c.getAdresse());
            stmt.executeUpdate();

            insertAssociations(conn, c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode mettant à jour un compte dans la base SQL /*/
    public void update(Compte c) {
        String sql = """
            UPDATE Compte
               SET nom = ?, email = ?, motDePasse = ?, pointFidelite = ?, adresse = ?
             WHERE idCompte = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNom());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getMotDePasse());
            stmt.setFloat(4, c.getPointFidelite());
            stmt.setString(5, c.getAdresse());
            stmt.setInt(6, c.getIdCompte());
            stmt.executeUpdate();

            clearAssociations(conn, c.getIdCompte());
            insertAssociations(conn, c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode supprimant un compte de la base de données /*/
    public void delete(int idCompte) {
        try (Connection conn = DataBaseManager.getConnection()) {
            clearAssociations(conn, idCompte);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM Compte WHERE idCompte = ?")) {
                stmt.setInt(1, idCompte);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode récupérant un compte par son ID /*/
    public Compte getById(int idCompte) {
        Compte c = null;
        String sql = "SELECT * FROM Compte WHERE idCompte = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCompte);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                c = new Compte(
                    rs.getInt("idCompte"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("motDePasse"),
                    rs.getString("adresse")
                );
                c.setPointFidelite(rs.getFloat("pointFidelite"));

                /*/ Poubelles autorisées /*/
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idPoubelle FROM Compte_Poubelle WHERE idCompte = ?")) {
                    ps.setInt(1, idCompte);
                    ResultSet prs = ps.executeQuery();
                    while (prs.next()) {
                        Poubelle p = poubelleDAO.getById(prs.getInt("idPoubelle"));
                        c.getPoubellesAutorisees().add(p);
                    }
                }

                /*/ Produits possédés /*/
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idProduit FROM Compte_Produit WHERE idCompte = ?")) {
                    ps.setInt(1, idCompte);
                    ResultSet prs = ps.executeQuery();
                    while (prs.next()) {
                        c.getProduitsPossedes()
                         .add(produitDAO.getById(prs.getInt("idProduit")));
                    }
                }

                /*/ Bons disponibles /*/
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idBon FROM Compte_BonReduction WHERE idCompte = ?")) {
                    ps.setInt(1, idCompte);
                    ResultSet prs = ps.executeQuery();
                    while (prs.next()) {
                        c.getBonsDisponibles()
                         .add(bonDAO.getById(prs.getInt("idBon")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

/*/ méthode récupérant tous les comptes de la base SQL /*/
    public List<Compte> getAll() {
        List<Compte> list = new ArrayList<>();
        String sql = "SELECT idCompte FROM Compte";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idCompte")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void clearAssociations(Connection conn, int idCompte) throws SQLException {
        try (PreparedStatement ps1 = conn.prepareStatement(
                "DELETE FROM Compte_Poubelle WHERE idCompte = ?");
             PreparedStatement ps2 = conn.prepareStatement(
                "DELETE FROM Compte_Produit WHERE idCompte = ?");
             PreparedStatement ps3 = conn.prepareStatement(
                "DELETE FROM Compte_BonReduction WHERE idCompte = ?")) {

            ps1.setInt(1, idCompte); ps1.executeUpdate();
            ps2.setInt(1, idCompte); ps2.executeUpdate();
            ps3.setInt(1, idCompte); ps3.executeUpdate();
        }
    }

    private void insertAssociations(Connection conn, Compte c) throws SQLException {
        String sqlP  = "INSERT INTO Compte_Poubelle (idCompte, idPoubelle) VALUES (?, ?)";
        String sqlPr = "INSERT INTO Compte_Produit  (idCompte, idProduit)  VALUES (?, ?)";
        String sqlB  = "INSERT INTO Compte_BonReduction (idCompte, idBon) VALUES (?, ?)";
        try (PreparedStatement psP  = conn.prepareStatement(sqlP);
             PreparedStatement psPr = conn.prepareStatement(sqlPr);
             PreparedStatement psB  = conn.prepareStatement(sqlB)) {

            for (Poubelle p : c.getPoubellesAutorisees()) {
                psP.setInt(1, c.getIdCompte());
                psP.setInt(2, p.getIdPoubelle());
                psP.addBatch();
            }
            psP.executeBatch();

            for (Produit pr : c.getProduitsPossedes()) {
                psPr.setInt(1, c.getIdCompte());
                psPr.setInt(2, pr.getIdProduit());
                psPr.addBatch();
            }
            psPr.executeBatch();

            for (BonReduction b : c.getBonsDisponibles()) {
                psB.setInt(1, c.getIdCompte());
                psB.setInt(2, b.getIdBon());
                psB.addBatch();
            }
            psB.executeBatch();
        }
    }
}
