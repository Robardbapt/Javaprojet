package DAO;

import java.sql.*;
import classe.BonReduction;
import classe.CategorieProduit;

public class BonReductionDAO {

    private final CategorieProduitDAO catDao = new CategorieProduitDAO();
    private final PoubelleDAO poubelleDAO = new PoubelleDAO();

    public void insert(BonReduction b, int idCompte) {
        int idCentre = -1;

        try {
            String sql = "SELECT idPoubelle FROM Compte_Poubelle WHERE idCompte = ? LIMIT 1";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idCompte);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int idPoubelle = rs.getInt("idPoubelle");
                    idCentre = poubelleDAO.getCentreIdByPoubelleId(idPoubelle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (b.getCategorieLiee() != null && idCentre != -1) {
            catDao.insert(b.getCategorieLiee(), idCentre);
        }

        String insertSql = """
            INSERT INTO BonReduction
              (description, tauxReduction, pointsNecessaires, estUtilise, idCategorie)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, b.getDescription());
            stmt.setFloat(2, b.getTauxReduction());
            stmt.setInt(3, b.getPointsNecessaires());
            stmt.setBoolean(4, b.isEstUtilise());
            if (b.getCategorieLiee() != null) {
                stmt.setInt(5, b.getCategorieLiee().getIdCategorie());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idBon = rs.getInt(1);
                b.setIdBon(idBon);

                // ➕ Lier au compte
                try (PreparedStatement linkStmt = conn.prepareStatement(
                        "INSERT INTO compte_bonreduction (idCompte, idBon) VALUES (?, ?)")) {
                    linkStmt.setInt(1, idCompte);
                    linkStmt.setInt(2, idBon);
                    linkStmt.executeUpdate();
                }

                // ➖ Mettre à jour les points fidélité
                try (PreparedStatement ptsStmt = conn.prepareStatement(
                        "UPDATE compte SET pointFidelite = pointFidelite - ? WHERE idCompte = ?")) {
                    ptsStmt.setInt(1, b.getPointsNecessaires());
                    ptsStmt.setInt(2, idCompte);
                    ptsStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(BonReduction b) {
        String sql = """
            UPDATE BonReduction
            SET description = ?, tauxReduction = ?, pointsNecessaires = ?, estUtilise = ?, idCategorie = ?
            WHERE idBon = ?
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, b.getDescription());
            stmt.setFloat(2, b.getTauxReduction());
            stmt.setInt(3, b.getPointsNecessaires());
            stmt.setBoolean(4, b.isEstUtilise());
            if (b.getCategorieLiee() != null) {
                stmt.setInt(5, b.getCategorieLiee().getIdCategorie());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setInt(6, b.getIdBon());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BonReduction getById(int idBon) {
        String sql = """
            SELECT description, tauxReduction, pointsNecessaires,
                   estUtilise, idCategorie
              FROM BonReduction
             WHERE idBon = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String desc = rs.getString("description");
                int pts = rs.getInt("pointsNecessaires");
                float taux = rs.getFloat("tauxReduction");
                boolean utilis = rs.getBoolean("estUtilise");
                int catId = rs.getInt("idCategorie");

                CategorieProduit cat = null;
                if (!rs.wasNull()) {
                    cat = catDao.getById(catId);
                }

                BonReduction b = new BonReduction(desc, pts, taux, cat);
                b.setIdBon(idBon);
                if (utilis) {
                    b.utiliserBon();
                }
                return b;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static java.util.List<BonReduction> getBonsByCompte(int idCompte) {
        java.util.List<BonReduction> bons = new java.util.ArrayList<>();
        String sql = "SELECT idBon FROM compte_bonreduction WHERE idCompte = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCompte);
            ResultSet rs = stmt.executeQuery();
            BonReductionDAO dao = new BonReductionDAO();
            while (rs.next()) {
                BonReduction bon = dao.getById(rs.getInt("idBon"));
                if (bon != null) bons.add(bon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bons;
    }

}