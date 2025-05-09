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
    private final CompteDAO compteDAO = new CompteDAO();
    private final CommerceDAO commerceDAO = new CommerceDAO();

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

    public void delete(int idCentre, boolean transfererUtilisateurs, int idCentreDestination) {
        // 1. Supprimer les poubelles du centre
        poubelleDAO.deleteByCentreId(idCentre);

        // 2. Supprimer ou transférer les comptes
        if (transfererUtilisateurs) {
            compteDAO.transfererComptesVers(idCentre, idCentreDestination);
        } else {
            compteDAO.deleteByCentre(idCentre);
        }

        // 3. Supprimer ou transférer les commerces
        if (transfererUtilisateurs) {
            commerceDAO.transfererCommercesVers(idCentre, idCentreDestination);
        } else {
            List<classe.Commerce> commerces = commerceDAO.getByCentre(idCentre);
            for (classe.Commerce c : commerces) {
                commerceDAO.delete(c.getIdCommerce());
            }
        }

        // 4. Supprimer les contrats
        contratDAO.deleteByCentreId(idCentre);

        // 5. Supprimer le centre de tri
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

    public int getIdCentreByAdmin(int idAdmin) {
        String sql = "SELECT idCentreDeTri FROM CentreDeTri WHERE id_admin = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAdmin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("idCentreDeTri");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getIdCentreByCategorie(int idCategorie) {
        String sql = """
            SELECT p.id_centre_tri
            FROM Contrat_Categorie cc
            JOIN Partenariat p ON cc.idContrat = p.idContrat
            WHERE cc.idCategorie = ?
            """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCategorie);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_centre_tri");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getIdCentreByCommerce(int idCommerce) {
        String sql = """
            SELECT cd.idCentreDeTri
            FROM commerce_contrat cc
            JOIN partenariat p ON cc.idContrat = p.idContrat
            JOIN centredetri cd ON p.id_centre_tri = cd.idCentreDeTri
            WHERE cc.idCommerce = ?
            LIMIT 1
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommerce);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCentreDeTri");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
