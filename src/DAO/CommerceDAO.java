package DAO;

import java.sql.*;
import java.util.*;

import classe.Commerce;
import classe.Compte;
import classe.Contrat;

/*/ classe DAO de la classe Commerce, permettant son implémentation dans une base de données/*/
public class CommerceDAO {

    private final ContratDAO contratDAO = new ContratDAO();

    public void insert(Commerce c) {
        String sql = """
            INSERT INTO commerce (nom, adresse)
            VALUES (?, ?)
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getAdresse());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                c.setIdCommerce(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Commerce c) {
        String sql = """
            UPDATE Commerce
               SET nom = ?, adresse = ?
             WHERE idCommerce = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getAdresse());
            ps.setInt(3, c.getIdCommerce());
            ps.executeUpdate();

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM commerce_categorie WHERE idCommerce = " + c.getIdCommerce());
            }

            String sqlCat = "INSERT INTO commerce_categorie (idCommerce, idCategorie) VALUES (?, (SELECT idCategorie FROM categorieproduit WHERE nom = ?))";
            try (PreparedStatement psCat = conn.prepareStatement(sqlCat)) {
                for (String cat : c.getCategoriesProduits()) {
                    psCat.setInt(1, c.getIdCommerce());
                    psCat.setString(2, cat);
                    psCat.addBatch();
                }
                psCat.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int idCommerce) {
        try (Connection conn = DataBaseManager.getConnection()) {

            // Récupérer les catégories liées au commerce
            List<String> noms = getCategoriesByCommerceId(idCommerce, conn);
            CategorieProduitDAO catDAO = new CategorieProduitDAO();
            ProduitDAO prodDAO = new ProduitDAO();

            for (String nomCat : noms) {
                int idCategorie = catDAO.getIdByNomEtCentre(nomCat, getIdCentreByCommerce(idCommerce));

                // Si cette catégorie est unique au commerce
                int count = countCommercesUtilisantCategorie(idCategorie);
                if (count == 1) {
                    catDAO.delete(idCategorie); // supprime tout : produits, contrat_categorie, produit_categorie, compte_produit
                } else {
                    // Sinon, juste délier le commerce de la catégorie
                    supprimerCategorieDuCommerce(idCommerce, nomCat);
                }
            }

            // Supprimer les lignes commerce_contrat et commerce_categorie
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM commerce_contrat WHERE idCommerce = " + idCommerce);
                st.executeUpdate("DELETE FROM commerce_categorie WHERE idCommerce = " + idCommerce);
            }

            // Supprimer le commerce lui-même
            String sql = "DELETE FROM commerce WHERE idCommerce = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCommerce);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Commerce getById(int idCommerce) {
        Commerce c = null;
        String sql = "SELECT nom, adresse FROM Commerce WHERE idCommerce = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommerce);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                c = new Commerce(idCommerce, rs.getString("nom"), rs.getString("adresse"));

                List<String> categories = getCategoriesByCommerceId(idCommerce, conn);
                c.setCategoriesProduits(categories);

                try (PreparedStatement psCon = conn.prepareStatement(
                        "SELECT idContrat FROM commerce_contrat WHERE idCommerce = ?")) {
                    psCon.setInt(1, idCommerce);
                    ResultSet r2 = psCon.executeQuery();
                    while (r2.next()) {
                        Contrat ctr = contratDAO.getById(r2.getInt("idContrat"));
                        if (ctr != null) {
                            c.ajouterContrat(ctr);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public List<Commerce> getAll() {
        List<Commerce> list = new ArrayList<>();
        String sql = "SELECT idCommerce FROM Commerce";
        try (Connection conn = DataBaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(getById(rs.getInt("idCommerce")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Commerce> getByCentre(int idCentre) {
        List<Commerce> list = new ArrayList<>();
        String sql = """
            SELECT com.idCommerce, com.nom, com.adresse
            FROM Commerce com
            JOIN commerce_contrat cc ON com.idCommerce = cc.idCommerce
            JOIN partenariat p ON cc.idContrat = p.idContrat
            WHERE p.id_centre_tri = ?
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCentre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commerce c = new Commerce();
                c.setIdCommerce(rs.getInt("idCommerce"));
                c.setNom(rs.getString("nom"));
                c.setAdresse(rs.getString("adresse"));

                List<String> categories = getCategoriesByCommerceId(c.getIdCommerce(), conn);
                c.setCategoriesProduits(categories);

                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getCategoriesByCommerceId(int idCommerce, Connection conn) {
        List<String> categories = new ArrayList<>();
        String sql = """
            SELECT cp.nom
            FROM commerce_categorie cc
            JOIN categorieproduit cp ON cc.idCategorie = cp.idCategorie
            WHERE cc.idCommerce = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCommerce);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categories.add(rs.getString("nom"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public void supprimerCategorieDuCommerce(int idCommerce, String nomCategorie) {
        String getIdSql = "SELECT idCategorie FROM categorieproduit WHERE nom = ?";
        String deleteSql = "DELETE FROM commerce_categorie WHERE idCommerce = ? AND idCategorie = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement psGet = conn.prepareStatement(getIdSql)) {

            psGet.setString(1, nomCategorie);
            ResultSet rs = psGet.executeQuery();

            if (rs.next()) {
                int idCategorie = rs.getInt("idCategorie");

                try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                    psDelete.setInt(1, idCommerce);
                    psDelete.setInt(2, idCategorie);
                    psDelete.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterCategorieAuCommerce(int idCommerce, String nomCategorie) {
        String getCategorieSql = "SELECT idCategorie FROM categorieproduit WHERE nom = ? AND id_centre_tri = ("
                               + "SELECT p.id_centre_tri FROM commerce_contrat cc "
                               + "JOIN partenariat p ON cc.idContrat = p.idContrat "
                               + "WHERE cc.idCommerce = ? LIMIT 1)";
        
        String insertCommerceCatSql = "INSERT INTO commerce_categorie (idCommerce, idCategorie) VALUES (?, ?)";
        String insertContratCatSql = "INSERT IGNORE INTO contrat_categorie (idContrat, idCategorie) VALUES (?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement psGet = conn.prepareStatement(getCategorieSql)) {

            psGet.setString(1, nomCategorie);
            psGet.setInt(2, idCommerce);
            ResultSet rs = psGet.executeQuery();

            if (rs.next()) {
                int idCategorie = rs.getInt("idCategorie");

                // Insertion dans commerce_categorie
                try (PreparedStatement psInsert = conn.prepareStatement(insertCommerceCatSql)) {
                    psInsert.setInt(1, idCommerce);
                    psInsert.setInt(2, idCategorie);
                    psInsert.executeUpdate();
                }

                // Récupération du contrat
                String getContratSql = "SELECT idContrat FROM commerce_contrat WHERE idCommerce = ? LIMIT 1";
                try (PreparedStatement psContrat = conn.prepareStatement(getContratSql)) {
                    psContrat.setInt(1, idCommerce);
                    ResultSet rContrat = psContrat.executeQuery();

                    if (rContrat.next()) {
                        int idContrat = rContrat.getInt("idContrat");

                        // Insertion dans contrat_categorie
                        try (PreparedStatement psLink = conn.prepareStatement(insertContratCatSql)) {
                            psLink.setInt(1, idContrat);
                            psLink.setInt(2, idCategorie);
                            psLink.executeUpdate();
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public Contrat getContratByCommerceId(int idCommerce) {
        String sql = "SELECT p.idContrat, p.dateDebut, p.dateFin, p.tauxConversion, p.id_centre_tri " +
                     "FROM commerce_contrat cp " +
                     "JOIN partenariat p ON cp.idContrat = p.idContrat " +
                     "WHERE cp.idCommerce = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommerce);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contrat contrat = new Contrat();
                    contrat.setIdContrat(rs.getInt("idContrat"));
                    contrat.setDateDebut(rs.getDate("dateDebut"));
                    contrat.setDateFin(rs.getDate("dateFin"));
                    contrat.setTauxDeConversion(rs.getInt("tauxConversion"));
                    contrat.setIdCentre(rs.getInt("id_centre_tri"));
                    return contrat;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void lierAuContrat(int idCommerce, int idContrat) {
        String sql = "INSERT INTO commerce_contrat (idCommerce, idContrat) VALUES (?, ?)";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommerce);
            ps.setInt(2, idContrat);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Compte getCompteAdminFromCommerce(int idCommerce) {
        String sql = """
            SELECT c.idCompte, c.nom, c.email, c.motDePasse, c.adresse, c.typeUser
            FROM commerce_contrat cc
            JOIN partenariat p ON cc.idContrat = p.idContrat
            JOIN centredetri cd ON p.id_centre_tri = cd.idCentreDeTri
            JOIN compte c ON cd.id_admin = c.idCompte
            WHERE cc.idCommerce = ?
        """;
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommerce);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Compte compte = new Compte();
                compte.setIdCompte(rs.getInt("idCompte"));
                compte.setNom(rs.getString("nom"));
                compte.setEmail(rs.getString("email"));
                compte.setMotDePasse(rs.getString("motDePasse"));
                compte.setAdresse(rs.getString("adresse"));
                return compte;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int countCommercesUtilisantCategorie(int idCategorie) {
        String sql = "SELECT COUNT(*) AS total FROM commerce_categorie WHERE idCategorie = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategorie);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getIdCentreByCommerce(int idCommerce) {
        String sql = """
            SELECT p.id_centre_tri
            FROM commerce_contrat cc
            JOIN partenariat p ON cc.idContrat = p.idContrat
            WHERE cc.idCommerce = ?
            LIMIT 1
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommerce);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_centre_tri");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // valeur par défaut si non trouvé
    }
    
    public void transfererCommercesVers(int idCentreSource, int idCentreDestination) {
        String getContratsSql = "SELECT cc.idCommerce, cc.idContrat FROM commerce_contrat cc " +
                                 "JOIN partenariat p ON cc.idContrat = p.idContrat " +
                                 "WHERE p.id_centre_tri = ?";

        String updatePartenariatSql = "UPDATE partenariat SET id_centre_tri = ? WHERE idContrat = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement psGet = conn.prepareStatement(getContratsSql);
             PreparedStatement psUpdate = conn.prepareStatement(updatePartenariatSql)) {

            psGet.setInt(1, idCentreSource);
            ResultSet rs = psGet.executeQuery();

            while (rs.next()) {
                int idContrat = rs.getInt("idContrat");
                psUpdate.setInt(1, idCentreDestination);
                psUpdate.setInt(2, idContrat);
                psUpdate.addBatch();
            }
            psUpdate.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}