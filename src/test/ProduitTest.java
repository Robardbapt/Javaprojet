package test;

import classe.*;
import java.util.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de produit 
**/

public class ProduitTest {
    public static void main(String[] args) {
        // Création de  2 catégories //
        CategorieProduit categorie1 = new CategorieProduit(1, "Électronique", 0.15f, 10);
        CategorieProduit categorie2 = new CategorieProduit(2, "Maison", 0.10f, 5);

        // Création d’un produit //
        Produit produit = new Produit(1001, "Aspirateur Robot", 199.99f, new Date());

        // Test 1 : sans catégorie //
        System.out.println("Test 1 - Description sans catégorie :\n" + produit.afficherDescription());

        // Ajout des 2 catégories au produit //
        produit.ajouterCategorie(categorie1);
        produit.ajouterCategorie(categorie2);

        // Test 2 : avec catégories //
        System.out.println("\nTest 2 - Description avec catégories :\n" + produit.afficherDescription());
    }
}
