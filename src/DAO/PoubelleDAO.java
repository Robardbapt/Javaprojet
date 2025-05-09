package DAO;

import java.sql.*;
import java.util.*;
import classe.Poubelle;
import classe.TypePoubelle;
import classe.Depot;

/*/ classe DAO de la classe Poubelle, permettant son implémentation dans une base de données/*/
public class PoubelleDAO {

    private final DepotDAO depotDAO = new DepotDAO();

/*/ méthode qui insert une poubelle dans la base de données /*/
    public void insert(Poubelle p, int idCentreDeTri) {
        String sql = """
            INSERT INTO Poubelle
              (idPoubelle, nom, capaciteMax, capaciteActuelle,
               estPleine, adresse, typePoubelle, idCentreDeTri)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, p.getIdPoubelle());
            stmt.setString(2, p.getNom());
            stmt.setFloat(3, p.getCapaciteMax());
            stmt.setFloat(4, p.getCapaciteActuelle());
            stmt.setBoolean(5, p.isEstPleine());
            stmt.setString(6, p.getAdresse());
            stmt.setString(7, p.getType().name());
            stmt.setInt(8, idCentreDeTri);
            stmt.executeUpdate();

            for (Depot d : p.getHistoriqueDepots()) {
                d.setIdPoubelle(String.valueOf(p.getIdPoubelle()));
                depotDAO.insert(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void delete(int idPoubelle) {
        String sql = "DELETE FROM Poubelle WHERE idPoubelle = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPoubelle);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode mettant à jour une poubelle dans la base de données /*/
    public void update(Poubelle p) {
        String sql = """
            UPDATE Poubelle
               SET nom = ?, capaciteMax = ?, capaciteActuelle = ?,
                   estPleine = ?, adresse = ?, typePoubelle = ?
             WHERE idPoubelle = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNom());
            stmt.setFloat(2, p.getCapaciteMax());
            stmt.setFloat(3, p.getCapaciteActuelle());
            stmt.setBoolean(4, p.isEstPleine());
            stmt.setString(5, p.getAdresse());
            // Conversion de l'enum en String
            stmt.setString(6, p.getType().name());
            stmt.setInt(7, p.getIdPoubelle());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode supprimant toutes les poubelles liées à un centre de Tri /*/
    public void deleteByCentreId(int idCentreDeTri) {
        String sql = "DELETE FROM Poubelle WHERE idCentreDeTri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCentreDeTri);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*/ méthode récupérant une poubelle par son id dans la base de données /*/
    public Poubelle getById(int idPoubelle) {
        Poubelle p = null;
        String sql = "SELECT * FROM Poubelle WHERE idPoubelle = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPoubelle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Conversion de la chaîne en enum
                TypePoubelle type = TypePoubelle.valueOf(rs.getString("typePoubelle"));
                p = new Poubelle(
                    rs.getInt("idPoubelle"),
                    rs.getString("nom"),
                    type
                );
                p.setAdresse(rs.getString("adresse"));
                p.setCapaciteMax(rs.getFloat("capaciteMax"));
                p.setCapaciteActuelle(rs.getFloat("capaciteActuelle"));
                // On recalcule estPleine via la méthode métier
                p.verifierPleine();
                // Récupération cascade des dépôts
                List<Depot> depots = depotDAO.getByPoubelleId(
                    String.valueOf(p.getIdPoubelle())
                );
                depots.forEach(p.getHistoriqueDepots()::add);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

/*/ méthode récupérant toutes les poubelles d'un centre de Tri /*/
    public List<Poubelle> getByCentreId(int idCentreDeTri) {
        List<Poubelle> list = new ArrayList<>();
        String sql = "SELECT idPoubelle FROM Poubelle WHERE idCentreDeTri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCentreDeTri);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getById(rs.getInt("idPoubelle")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

/*/ méthode récupérant toutes les poubelles de la base de données /*/
    public List<Poubelle> getAll() {
        List<Poubelle> list = new ArrayList<>();
        String sql = "SELECT idPoubelle FROM Poubelle";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idPoubelle")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public int getCentreIdByPoubelleId(int idPoubelle) {
        String sql = "SELECT idCentreDeTri FROM Poubelle WHERE idPoubelle = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPoubelle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCentreDeTri");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
