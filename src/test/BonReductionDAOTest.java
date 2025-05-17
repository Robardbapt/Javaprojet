package test;

import DAO.BonReductionDAO;
import classe.BonReduction;
import classe.CategorieProduit;

import java.util.List;

public class BonReductionDAOTest{
    public static void main(String[] args) {
        BonReductionDAO dao = new BonReductionDAO();
        

        // Test 1 : insertion d'un bon sans catégorie
        BonReduction bon1 = new BonReduction("Réduction générale 10%", 50);
        dao.insert(bon1, 0);
        System.out.println(" Après INSERT bon1 ");
        printAll(dao);

        // Test 2 : mise à jour du bon
        bon1.setDescription("Réduction générale 15%");
        bon1.setTauxReduction(0.15f);
        dao.update(bon1);
        System.out.println("\n Après UPDATE bon1 ");
        printAll(dao);

        // Test 3 : insertion d'un bon lié à une catégorie
        CategorieProduit cat = new CategorieProduit(1, "Alimentation", 0.10f, 20);
        BonReduction bon2 = new BonReduction("Réduc Alim 20%", 20, 0.20f, cat);
        dao.insert(bon2, 0);
        System.out.println("\n Après INSERT bon2 ");
        printAll(dao);

        // Test 4 : récupération individuelle depuis la base
        System.out.println("\n Récupération individuelle de bon2 ");
        BonReduction loaded2 = dao.getById(bon2.getIdBon());
        if (loaded2 != null) {
            System.out.println("  id = " + loaded2.getIdBon());
            System.out.println("  desc = " + loaded2.getDescription());
            System.out.println("  taux = " + (loaded2.getTauxReduction()*100) + "%");
            System.out.println("  pts  = " + loaded2.getPointsNecessaires());
            System.out.println("  cat  = " 
                + (loaded2.getCategorieLiee() != null 
                    ? loaded2.getCategorieLiee().getNom() 
                    : "aucune"));}
        }

    @SuppressWarnings("null")
	private static void printAll(BonReductionDAO dao) {
     
        BonReduction[] list = null;
		System.out.println("Liste des bons (" + list.length + ") :");
        for (BonReduction b : list) {
            System.out.println("  [" + b.getIdBon() + "] " 
                + b.getDescription() 
                + " – taux " + (b.getTauxReduction()*100) + "%, pts " 
                + b.getPointsNecessaires() 
                + ", utilisé=" + b.isEstUtilise());
        }
    }
}
