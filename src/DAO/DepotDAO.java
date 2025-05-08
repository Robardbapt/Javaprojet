package DAO;

import java.sql.*;
import java.util.*;

import classe.Depot;
import classe.Dechet;
import classe.Contenu;

/*/ classe DAO de la classe Depot, permettant son implémentation dans une base de données/*/
public class DepotDAO {

    private final DechetDAO dechetDAO = new DechetDAO();

/*/ méthode qui insert un dépot dans la base de données /*/
    public void insert(Depot d) {
        dechetDAO.insert(d.getDechet());

        String sql = """
            INSERT INTO Depot
              (idDepot, quantite, dateDepot, pointsGagnes, idPoubelle, dechetNom, contenu)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, d.getIdDepot());
            stmt.setFloat(2, d.getQuantite());
            stmt.setTimestamp(3, new Timestamp(d.getDateDepot().getTime()));
            stmt.setFloat(4, d.getPointsGagnes());
            stmt.setInt(5, Integer.parseInt(d.getIdPoubelle()));
            stmt.setString(6, d.getDechet().getNom());
            stmt.setString(7, d.getContenu().name());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode mettant à jour un dépot dans la base SQL /*/
    public void update(Depot d) {
        dechetDAO.insert(d.getDechet());  // au cas où ses infos ont changé

        String sql = """
            UPDATE Depot
               SET quantite = ?, dateDepot = ?, pointsGagnes = ?,
                   idPoubelle = ?, dechetNom = ?, contenu = ?
             WHERE idDepot = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, d.getQuantite());
            stmt.setTimestamp(2, new Timestamp(d.getDateDepot().getTime()));
            stmt.setFloat(3, d.getPointsGagnes());
            stmt.setInt(4, Integer.parseInt(d.getIdPoubelle()));
            stmt.setString(5, d.getDechet().getNom());
            stmt.setString(6, d.getContenu().name());
            stmt.setInt(7, d.getIdDepot());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode supprimant un dépot dans la base SQL/*/
    public void delete(int idDepot) {
        String sql = "DELETE FROM Depot WHERE idDepot = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDepot);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode qui récupère un dépot par son ID dans la base SQL /*/
    public Depot getById(int idDepot) {
        String sql = "SELECT * FROM Depot WHERE idDepot = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDepot);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nomDechet = rs.getString("dechetNom");
                Dechet dch = dechetDAO.getByNom(nomDechet);

                Depot d = new Depot(
                    dch,
                    rs.getFloat("quantite"),
                    String.valueOf(rs.getInt("idPoubelle"))
                );
                d.setIdDepot(idDepot);
                d.setDateDepot(rs.getTimestamp("dateDepot"));
                d.setPointsGagnes(rs.getFloat("pointsGagnes"));

                Contenu c = Contenu.valueOf(rs.getString("contenu"));
                d.getDechet().setContenu(c);

                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

/*/ méthode récupérant les dépots pour une poubelle donnée /*/
    public List<Depot> getByPoubelleId(String idPoubelle) {
        List<Depot> list = new ArrayList<>();
        String sql = "SELECT idDepot FROM Depot WHERE idPoubelle = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(idPoubelle));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getById(rs.getInt("idDepot")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

/*/ méthode récupérant tous les dépots de la base SQL/*/
    public List<Depot> getAll() {
        List<Depot> list = new ArrayList<>();
        String sql = "SELECT idDepot FROM Depot";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idDepot")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
