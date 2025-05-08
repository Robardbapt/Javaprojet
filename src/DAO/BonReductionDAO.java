package DAO;

import java.sql.*;
import java.util.*;
import classe.BonReduction;
import classe.CategorieProduit;

/*/ classe DAO de la classe BonRéduction, permettant l'insertion dans la base de données /*/
public class BonReductionDAO {

    private final CategorieProduitDAO catDao = new CategorieProduitDAO();

/*/ méthode qui insert un bon de réduction dans la base de données /*/
    public void insert(BonReduction b) {
    	
        if (b.getCategorieLiee() != null) {
            catDao.insert(b.getCategorieLiee());
        }

        String sql = """
            INSERT INTO BonReduction
              (idBon, description, tauxReduction, pointsNecessaires, estUtilise, idCategorie)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, b.getIdBon());
            stmt.setString(2, b.getDescription());
            stmt.setFloat(3, b.getTauxReduction());
            stmt.setInt(4, b.getPointsNecessaires());
            stmt.setBoolean(5, b.isEstUtilise());
            if (b.getCategorieLiee() != null) {
                stmt.setInt(6, b.getCategorieLiee().getIdCategorie());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode qui met à jour un bon de réduction dans la base de données /*/
    public void update(BonReduction b) {
        if (b.getCategorieLiee() != null) {
            catDao.insert(b.getCategorieLiee());
        }

        String sql = """
            UPDATE BonReduction
               SET description = ?, tauxReduction = ?, pointsNecessaires = ?,
                   estUtilise = ?, idCategorie = ?
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

/*/ méthode supprimant un bon de réduction grâce à son identifiant /*/
    public void delete(int idBon) {
        String sql = "DELETE FROM BonReduction WHERE idBon = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBon);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode récupérant un bon de réduction grâce à son identifiant dans la base de données /*/
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

/*/ méthode récupérant tous les bons de réduction /*/
    public List<BonReduction> getAll() {
        List<BonReduction> list = new ArrayList<>();
        String sql = "SELECT idBon FROM BonReduction";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("idBon");
                BonReduction b = getById(id);
                if (b != null) {
                    list.add(b);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
