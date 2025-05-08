package DAO;

import classe.Depot;
import classe.Dechet;
import classe.HistoriqueDepot;

import java.sql.*;

/**
 * DAO pour accéder à la table HistoriqueDepot (vue comme une liste de dépôts).
 */
public class HistoriqueDepotDAO {

    /**
     * Récupère tous les dépôts dans la base.
     */
    public HistoriqueDepot getAllHistory() {
        HistoriqueDepot historique = new HistoriqueDepot();
        String sql = "SELECT * FROM HistoriqueDepot";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Depot depot = new Depot();
                depot.setIdDepot(rs.getInt("idDepot"));
                depot.setIdPoubelle(rs.getString("idPoubelle"));
                depot.setDateDepot(rs.getTimestamp("dateDepot"));
                depot.setQuantite(rs.getFloat("quantite"));
                depot.setPointsGagnes(rs.getInt("pointsGagnes"));
                depot.setDechet(new Dechet(rs.getString("dechetNom")));

                historique.ajouterDepot(depot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historique;
    }

    public HistoriqueDepot getByPoubelleId(String idPoubelle) {
        HistoriqueDepot historique = new HistoriqueDepot();
        String sql = "SELECT * FROM HistoriqueDepot WHERE idPoubelle = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idPoubelle);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Depot depot = new Depot();
                depot.setIdDepot(rs.getInt("idDepot"));
                depot.setIdPoubelle(rs.getString("idPoubelle"));
                depot.setDateDepot(rs.getTimestamp("dateDepot"));
                depot.setQuantite(rs.getFloat("quantite"));
                depot.setPointsGagnes(rs.getInt("pointsGagnes"));
                depot.setDechet(new Dechet(rs.getString("dechetNom")));

                historique.ajouterDepot(depot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historique;
    }
    
    public HistoriqueDepot getByCompteId(int idCompte) {
        HistoriqueDepot historique = new HistoriqueDepot();
        String sql = "SELECT * FROM HistoriqueDepot WHERE idCompte = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCompte);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Depot depot = new Depot();
                depot.setIdDepot(rs.getInt("idDepot"));
                depot.setIdPoubelle(rs.getString("idPoubelle"));
                depot.setDateDepot(rs.getTimestamp("dateDepot")); // champ de type java.util.Date
                depot.setQuantite(rs.getFloat("quantite"));
                depot.setPointsGagnes(rs.getFloat("pointsGagnes")); // float
                depot.setDechet(new Dechet(rs.getString("dechetNom")));

                historique.ajouterDepot(depot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historique;
    }

    public HistoriqueDepot getByCentreId(int idCentre) {
        HistoriqueDepot historique = new HistoriqueDepot();
        String sql = """
            SELECT hd.* FROM HistoriqueDepot hd
            JOIN Poubelle p ON hd.idPoubelle = p.idPoubelle
            WHERE p.idCentre = ?
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCentre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Depot depot = new Depot();
                depot.setIdDepot(rs.getInt("idDepot"));
                depot.setIdPoubelle(rs.getString("idPoubelle"));
                depot.setDateDepot(rs.getTimestamp("dateDepot"));
                depot.setQuantite(rs.getFloat("quantite"));
                depot.setPointsGagnes(rs.getInt("pointsGagnes"));
                depot.setDechet(new Dechet(rs.getString("dechetNom")));

                historique.ajouterDepot(depot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historique;
    }

    /**
     * Insère un nouveau dépôt dans l'historique.
     */
    public void insertDepot(Depot d, int idCompte) {
        String sql = """
            INSERT INTO HistoriqueDepot (idDepot, idPoubelle, dateDepot, dechetNom, quantite, pointsGagnes, idCompte)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, d.getIdDepot());
            stmt.setString(2, d.getIdPoubelle());
            stmt.setTimestamp(3, new Timestamp(d.getDateDepot().getTime()));
            stmt.setString(4, d.getDechet().getNom());
            stmt.setFloat(5, d.getQuantite());
            stmt.setInt(6, Math.round(d.getPointsGagnes())); 
            stmt.setInt(7, idCompte); // 

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 }

