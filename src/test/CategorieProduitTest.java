package test;

import classe.CategorieProduit;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de CategorieProduit 
**/

public class CategorieProduitTest {
    public static void main(String[] args) {
        // Création d'une catégorie // 
        CategorieProduit categorie = new CategorieProduit(1, "Électronique", 0.20f, 10);

        // Test 1 : vérifierReduction avec points suffisants //
        int points1 = 15;
        System.out.println("Test 1 - Vérification avec 15 points : " + categorie.verifierReduction(points1)); // true

        // Test 2 : vérifierReduction sans points suffisants //
        int points2 = 5;
        System.out.println("Test 2 - Vérification avec 5 points : " + categorie.verifierReduction(points2)); // false

        // Test 3 : appliquerReduction avec points suffisants //
        float prixInitial = 100.0f;
        float prixReduit = categorie.appliquerReduction(prixInitial, points1);
        System.out.println("Test 3 - Prix réduit (avec 15 points) : " + prixReduit); // 80.0

        // Test 4 : appliquerReduction sans points suffisants //
        float prixSansReduction = categorie.appliquerReduction(prixInitial, points2);
        System.out.println("Test 4 - Prix sans réduction (avec 5 points) : " + prixSansReduction); // 100.0
    }
}
