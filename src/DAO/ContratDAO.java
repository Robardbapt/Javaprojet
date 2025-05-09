package DAO;

import java.sql.*;
import java.util.*;
import classe.Contrat;
import classe.CategorieProduit;

/*/ classe DAO de la classe Contrat, permettant son implémentation dans une base de données/*/
public class ContratDAO {

    private final CategorieProduitDAO cpDAO = new CategorieProduitDAO();

    /*/ méthode qui insert un contrat dans la base de données /*/
    public void insert(Contrat c, int idCentreTri) {
        String sql = "INSERT INTO partenariat (dateDebut, dateFin, tauxConversion, id_centre_tri) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, new java.sql.Date(c.getDateDebut().getTime()));
            stmt.setDate(2, new java.sql.Date(c.getDateFin().getTime()));
            stmt.setInt(3, c.getTauxDeConversion());
            stmt.setInt(4, idCentreTri);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                c.setIdContrat(rs.getInt(1));
            }

            String linkSql = "INSERT INTO Contrat_Categorie (idContrat, idCategorie) VALUES (?, ?)";
            try (PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
                for (CategorieProduit cp : c.getCategoriesConcernees()) {
                    linkStmt.setInt(1, c.getIdContrat());
                    linkStmt.setInt(2, cp.getIdCategorie());
                    linkStmt.addBatch();
                }
                linkStmt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*/ méthode supprimant un contrat et ses dépendances /*/
    public void delete(int idContrat) {
        String delLink = "DELETE FROM Contrat_Categorie WHERE idContrat = ?";
        String del = "DELETE FROM Partenariat WHERE idContrat = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(delLink);
             PreparedStatement stmt2 = conn.prepareStatement(del)) {

            stmt1.setInt(1, idContrat);
            stmt1.executeUpdate();

            stmt2.setInt(1, idContrat);
            stmt2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*/ méthode supprimant tous les contrats pour un centre donné /*/
    public void deleteByCentreId(int idCentreTri) {
        String delLink =
            "DELETE FROM Contrat_Categorie WHERE idContrat IN " +
            "(SELECT idContrat FROM Partenariat WHERE id_centre_tri = ?)";
        String del = "DELETE FROM Partenariat WHERE id_centre_tri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmtLink = conn.prepareStatement(delLink);
             PreparedStatement stmt = conn.prepareStatement(del)) {

            stmtLink.setInt(1, idCentreTri);
            stmtLink.executeUpdate();

            stmt.setInt(1, idCentreTri);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*/ méthode mettant à jour un contrat dans la base SQL /*/
    public void update(Contrat c) {
        String sql = "UPDATE Partenariat SET dateDebut = ?, dateFin = ?, tauxConversion = ? WHERE idContrat = ?";
        String delLink = "DELETE FROM Contrat_Categorie WHERE idContrat = ?";
        String linkSql = "INSERT INTO Contrat_Categorie (idContrat, idCategorie) VALUES (?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement stmtDel = conn.prepareStatement(delLink);
             PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {

            stmt.setDate(1, new java.sql.Date(c.getDateDebut().getTime()));
            stmt.setDate(2, new java.sql.Date(c.getDateFin().getTime()));
            stmt.setInt(3, c.getTauxDeConversion());
            stmt.setInt(4, c.getIdContrat());
            stmt.executeUpdate();

            stmtDel.setInt(1, c.getIdContrat());
            stmtDel.executeUpdate();

            for (CategorieProduit cp : c.getCategoriesConcernees()) {
                linkStmt.setInt(1, c.getIdContrat());
                linkStmt.setInt(2, cp.getIdCategorie());
                linkStmt.addBatch();
            }
            linkStmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*/ méthode récupérant un contrat par son ID /*/
    public Contrat getById(int idContrat) {
        String sql = "SELECT * FROM Partenariat WHERE idContrat = ?";
        Contrat c = null;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idContrat);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                c = new Contrat(
                    rs.getInt("idContrat"),
                    rs.getDate("dateDebut"),
                    rs.getDate("dateFin"),
                    rs.getInt("tauxConversion")
                );
                c.setIdCentre(rs.getInt("id_centre_tri"));

                String linkSql =
                    "SELECT idCategorie FROM Contrat_Categorie WHERE idContrat = ?";
                try (PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
                    linkStmt.setInt(1, idContrat);
                    ResultSet rsLink = linkStmt.executeQuery();
                    while (rsLink.next()) {
                        CategorieProduit cp = cpDAO.getById(rsLink.getInt("idCategorie"));
                        c.ajouterCategorie(cp);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    /*/ méthode récupérant les contrats liés à un centre de tri /*/
    public List<Contrat> getByCentreId(int idCentreTri) {
        List<Contrat> list = new ArrayList<>();
        String sql = "SELECT idContrat FROM Partenariat WHERE id_centre_tri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCentreTri);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getById(rs.getInt("idContrat")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*/ méthode récupérant tous les contrats de la base SQL /*/
    public List<Contrat> getAll() {
        List<Contrat> list = new ArrayList<>();
        String sql = "SELECT idContrat FROM Partenariat";
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idContrat")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
