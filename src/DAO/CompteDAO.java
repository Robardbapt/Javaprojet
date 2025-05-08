package DAO;

import java.sql.*;
import java.util.*;

import classe.Compte;
import classe.Poubelle;
import classe.Produit;
import classe.BonReduction;

public class CompteDAO {

    private final PoubelleDAO poubelleDAO = new PoubelleDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final BonReductionDAO bonDAO = new BonReductionDAO();

    public void insert(Compte c) {
        String sql = """
            INSERT INTO Compte
              (idCompte, nom, email, motDePasse, pointFidelite, adresse, typeUser)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, c.getIdCompte());
            stmt.setString(2, c.getNom());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getMotDePasse());
            stmt.setFloat(5, c.getPointFidelite());
            stmt.setString(6, c.getAdresse());
            stmt.setString(7, c.getTypeUser());
            stmt.executeUpdate();

            insertAssociations(conn, c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Compte c) {
        String sql = """
            UPDATE Compte
               SET nom = ?, email = ?, motDePasse = ?, pointFidelite = ?, adresse = ?, typeUser = ?
             WHERE idCompte = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNom());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getMotDePasse());
            stmt.setFloat(4, c.getPointFidelite());
            stmt.setString(5, c.getAdresse());
            stmt.setString(6, c.getTypeUser());
            stmt.setInt(7, c.getIdCompte());
            stmt.executeUpdate();

            clearAssociations(conn, c.getIdCompte());
            insertAssociations(conn, c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
    
    public List<Compte> getByCentreId(int idCentre) {
        List<Compte> comptes = new ArrayList<>();
        String sql = """
            SELECT DISTINCT c.idCompte
            FROM Compte c
            JOIN Compte_Poubelle cp ON c.idCompte = cp.idCompte
            JOIN Poubelle p ON cp.idPoubelle = p.idPoubelle
            WHERE p.idCentreDeTri = ?
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCentre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comptes.add(getById(rs.getInt("idCompte")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comptes;
    }

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
                    rs.getString("adresse"),
                    rs.getString("typeUser")
                );
                c.setPointFidelite(rs.getFloat("pointFidelite"));

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idPoubelle FROM Compte_Poubelle WHERE idCompte = ?")) {
                    ps.setInt(1, idCompte);
                    ResultSet prs = ps.executeQuery();
                    while (prs.next()) {
                        c.getPoubellesAutorisees().add(poubelleDAO.getById(prs.getInt("idPoubelle")));
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idProduit FROM Compte_Produit WHERE idCompte = ?")) {
                    ps.setInt(1, idCompte);
                    ResultSet prs = ps.executeQuery();
                    while (prs.next()) {
                        c.getProduitsPossedes().add(produitDAO.getById(prs.getInt("idProduit")));
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idBon FROM Compte_BonReduction WHERE idCompte = ?")) {
                    ps.setInt(1, idCompte);
                    ResultSet prs = ps.executeQuery();
                    while (prs.next()) {
                        c.getBonsDisponibles().add(bonDAO.getById(prs.getInt("idBon")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

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
        String sqlP = "INSERT INTO Compte_Poubelle (idCompte, idPoubelle) VALUES (?, ?)";
        String sqlPr = "INSERT INTO Compte_Produit  (idCompte, idProduit)  VALUES (?, ?)";
        String sqlB = "INSERT INTO Compte_BonReduction (idCompte, idBon) VALUES (?, ?)";
        try (PreparedStatement psP = conn.prepareStatement(sqlP);
             PreparedStatement psPr = conn.prepareStatement(sqlPr);
             PreparedStatement psB = conn.prepareStatement(sqlB)) {

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

    public Compte findByEmailAndPassword(String email, String password) {
        String sql = "SELECT idCompte FROM Compte WHERE email = ? AND motDePasse = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idCompte = rs.getInt("idCompte");
                return getById(idCompte);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updatePoints(int idCompte, float nouveauxPoints) {
        String sql = "UPDATE Compte SET pointFidelite = ? WHERE idCompte = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, nouveauxPoints);
            stmt.setInt(2, idCompte);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
