package DAO;

import java.sql.*;
import java.util.*;

import classe.CentreDeTri;
import classe.Contrat;
import classe.Poubelle;

public class CentreDeTriDAO {

    private final ContratDAO contratDAO = new ContratDAO();
    private final PoubelleDAO poubelleDAO = new PoubelleDAO();
    private final StatistiqueDAO statistiqueDAO = new StatistiqueDAO();

    public void insert(CentreDeTri centre) {
        String sql = "INSERT INTO CentreDeTri (idCentreDeTri, nom, adresse, id_admin) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, centre.getIdCentreDeTri());
            stmt.setString(2, centre.getNom());
            stmt.setString(3, centre.getAdresse());
            stmt.setInt(4, centre.getIdAdmin());
            stmt.executeUpdate();

            int id = centre.getIdCentreDeTri();
            for (Contrat c : centre.getPartenariats()) {
                contratDAO.insert(c, id);
            }
            for (Map.Entry<String, List<Poubelle>> e : centre.getPoubellesPlacees().entrySet()) {
                for (Poubelle p : e.getValue()) {
                    p.setAdresse(e.getKey());
                    poubelleDAO.insert(p, id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int idCentre) {
        contratDAO.deleteByCentreId(idCentre);
        poubelleDAO.deleteByCentreId(idCentre);
        String sql = "DELETE FROM CentreDeTri WHERE idCentreDeTri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCentre);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(CentreDeTri centre) {
        String sql = "UPDATE CentreDeTri SET nom = ?, adresse = ?, id_admin = ? WHERE idCentreDeTri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, centre.getNom());
            stmt.setString(2, centre.getAdresse());
            stmt.setInt(3, centre.getIdAdmin());
            stmt.setInt(4, centre.getIdCentreDeTri());
            stmt.executeUpdate();

            int id = centre.getIdCentreDeTri();
            contratDAO.deleteByCentreId(id);
            for (Contrat c : centre.getPartenariats()) {
                contratDAO.insert(c, id);
            }
            poubelleDAO.deleteByCentreId(id);
            for (Map.Entry<String, List<Poubelle>> e : centre.getPoubellesPlacees().entrySet()) {
                for (Poubelle p : e.getValue()) {
                    p.setAdresse(e.getKey());
                    poubelleDAO.insert(p, id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CentreDeTri getById(int idCentre) {
        CentreDeTri centre = null;
        String sql = "SELECT * FROM CentreDeTri WHERE idCentreDeTri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCentre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                centre = new CentreDeTri(
                    rs.getInt("idCentreDeTri"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getInt("id_admin")
                );

                centre.setPartenariats(contratDAO.getByCentreId(idCentre));
                centre.setStatistique(statistiqueDAO.getByCentreId(idCentre));

                List<Poubelle> poubelles = poubelleDAO.getByCentreId(idCentre);
                Map<String, List<Poubelle>> map = new HashMap<>();
                for (Poubelle p : poubelles) {
                    map.computeIfAbsent(p.getAdresse(), k -> new ArrayList<>()).add(p);
                }
                centre.setPoubellesPlacees(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return centre;
    }

    public List<CentreDeTri> getAll() {
        List<CentreDeTri> list = new ArrayList<>();
        String sql = "SELECT idCentreDeTri FROM CentreDeTri";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idCentreDeTri")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Méthode utile pour l’IHM : récupérer les centres d’un admin donné
     */
    public List<CentreDeTri> getByAdminId(int idAdmin) {
        List<CentreDeTri> centres = new ArrayList<>();
        String sql = "SELECT idCentreDeTri FROM CentreDeTri WHERE id_admin = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAdmin);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                centres.add(getById(rs.getInt("idCentreDeTri")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return centres;
    }
}
