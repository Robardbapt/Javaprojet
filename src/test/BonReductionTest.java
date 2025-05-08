package test;

import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de BonReduction 
**/

public class BonReductionTest {
    public static void main(String[] args) {
    	// Création d'une catégorie produit //
        CategorieProduit categorie = new CategorieProduit(1, "Alimentation", 0.10f, 5);

        // Test 1 : Bon générique sans catégorie //
        BonReduction bon1 = new BonReduction("Réduction globale 10%", 5);
        System.out.println("Test 1 - Bon sans catégorie :");
        System.out.println("Description : " + bon1.getDescription());
        System.out.println("Taux : " + (bon1.getTauxReduction() * 100) + "%");
        System.out.println("Points requis : " + bon1.getPointsNecessaires());
        System.out.println("Utilisé ? " + bon1.isEstUtilise());

        // Test 2 : Bon lié à une catégorie //
        BonReduction bon2 = new BonReduction("Réduction spéciale Alimentation 20%", 10, 0.20f, categorie);
        System.out.println("\nTest 2 - Bon avec catégorie liée :");
        System.out.println("Description : " + bon2.getDescription());
        System.out.println("Taux : " + (bon2.getTauxReduction() * 100) + "%");
        System.out.println("Catégorie : " + bon2.getCategorieLiee().getNom());
        System.out.println("Utilisé ? " + bon2.isEstUtilise());

        // Test 3 : Utilisation du bon //
        bon1.utiliserBon();
        System.out.println("\nTest 3 - Bon 1 utilisé ? " + bon1.isEstUtilise()); 
    }
}
