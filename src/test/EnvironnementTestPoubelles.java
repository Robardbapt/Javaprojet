package test;

import DAO.*;
import classe.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class EnvironnementTestPoubelles {

    public static void main(String[] args) {
        resetDatabase();
        setupTestEnvironment();
    }

    private static void resetDatabase() {
        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("DELETE FROM compte_bonreduction");
            stmt.executeUpdate("DELETE FROM compte_produit");
            stmt.executeUpdate("DELETE FROM compte_poubelle");
            stmt.executeUpdate("DELETE FROM compte");
            stmt.executeUpdate("DELETE FROM commerce_categorie");
            stmt.executeUpdate("DELETE FROM commerce_contrat");
            stmt.executeUpdate("DELETE FROM commerce");
            stmt.executeUpdate("DELETE FROM contrat_categorie");
            stmt.executeUpdate("DELETE FROM partenariat");
            stmt.executeUpdate("DELETE FROM produit_categorie");
            stmt.executeUpdate("DELETE FROM produit");
            stmt.executeUpdate("DELETE FROM categorieproduit");
            stmt.executeUpdate("DELETE FROM bonreduction");
            stmt.executeUpdate("DELETE FROM depot");
            stmt.executeUpdate("DELETE FROM poubelle");
            stmt.executeUpdate("DELETE FROM centredetri");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("Base nettoyée avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupTestEnvironment() {
        CompteDAO compteDAO = new CompteDAO();
        CentreDeTriDAO centreDAO = new CentreDeTriDAO();
        PoubelleDAO poubelleDAO = new PoubelleDAO();
        CommerceDAO commerceDAO = new CommerceDAO();
        CategorieProduitDAO categorieDAO = new CategorieProduitDAO();
        ProduitDAO produitDAO = new ProduitDAO();
        BonReductionDAO bonDAO = new BonReductionDAO();
        ContratDAO contratDAO = new ContratDAO();

        Random rand = new Random();

        // Superadmin
        Compte superadmin = new Compte(1, "SuperAdmin", "admin@tri.fr", "admin", "Central", "admin");
        superadmin.setPointFidelite(rand.nextInt(101)); 
        compteDAO.insert(superadmin);

        List<CategorieProduit> allCategories = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            Compte admin = new Compte(100 + i, "Admin" + i, "admin" + i + "@tri.fr", "admin", "Zone" + i, "admin");
            admin.setPointFidelite(rand.nextInt(101)); 
            compteDAO.insert(admin);

            CentreDeTri centre = new CentreDeTri(200 + i, "CentreTri" + i, "Adresse" + i, admin.getIdCompte());

            for (TypePoubelle type : TypePoubelle.values()) {
                Poubelle p = new Poubelle("Poubelle_" + type + "_C" + i, type);
                p.setCapaciteActuelle(0);
                p.setCapaciteMax(100);
                centre.placerPoubelle(p, "Adresse" + i);
            }

            centreDAO.insert(centre);

            Contrat contrat = new Contrat();
            contrat.setDateDebut(Date.valueOf(LocalDate.now()));
            contrat.setDateFin(Date.valueOf(LocalDate.now().plusYears(2)));
            contrat.setTauxDeConversion(5);
            contratDAO.insert(contrat, centre.getIdCentreDeTri());
            centre.ajouterPartenariat(contrat);

            for (int j = 1; j <= 3; j++) {
                Commerce commerce = new Commerce("CommerceC" + i + "_" + j, "Rue Com" + j);
                commerceDAO.insert(commerce);
                commerceDAO.lierAuContrat(commerce.getIdCommerce(), contrat.getIdContrat());

                for (int k = 1; k <= 4; k++) {
                    CategorieProduit cat = new CategorieProduit();
                    cat.setNom("Categorie_C" + i + "_" + j + "_" + k);
                    cat.setPointNecessaire(10 + k);
                    cat.setTauxReduction(15 + k);
                    categorieDAO.insert(cat, centre.getIdCentreDeTri());
                    commerceDAO.ajouterCategorieAuCommerce(commerce.getIdCommerce(), cat.getNom());

                    allCategories.add(cat);

                    for (int l = 1; l <= 3; l++) {
                        Produit prod = new Produit("Produit_C" + i + "_" + j + "_" + k + "_" + l,
                                10f * l, Date.valueOf(LocalDate.now()));
                        prod.ajouterCategorie(cat);
                        produitDAO.insertEtLierAComptes(prod, centre.getIdCentreDeTri());
                    }
                }
            }

            for (int u = 1; u <= 4; u++) {
                Compte user = new Compte(3000 + i * 10 + u, "UserC" + i + "_" + u, "user" + i + u + "@mail.com", "user", "Adresse" + i, "user");
                user.setPointFidelite(rand.nextInt(101)); 
                compteDAO.insert(user);
                compteDAO.lierComptePoubelle(user.getIdCompte(), centre.getIdCentreDeTri());

                for (int b = 0; b < 2; b++) {
                    int index = ((i - 1) * 12 + (u - 1) * 2 + b);
                    if (index < allCategories.size()) {
                        CategorieProduit cat = allCategories.get(index);
                        BonReduction bon = new BonReduction("Bon pour " + cat.getNom(),
                                cat.getPointNecessaire(), cat.getTauxReduction(), cat);
                        bonDAO.insert(bon, user.getIdCompte());
                    }
                }
            }
        }

        System.out.println("Environnement de test créé avec succès.");
    }
}
