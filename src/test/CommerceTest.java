package test;

import java.util.*;
import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de Commerce
**/

public class CommerceTest {
    public static void main(String[] args) {
        // Création de deux catégories //
        CategorieProduit cat1 = new CategorieProduit(1, "Électroménager", 0.10f, 5);
        CategorieProduit cat2 = new CategorieProduit(2, "High-Tech", 0.20f, 10);

        // Création d'un contrat //
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1);
        Date debut1 = cal1.getTime();

        cal1.set(2025, Calendar.DECEMBER, 31);
        Date fin1 = cal1.getTime();

        Contrat contrat1 = new Contrat(1, debut1, fin1, 30);
        contrat1.ajouterCategorie(cat1);
        contrat1.ajouterCategorie(cat2);

        // Création d’un commerce //
        Commerce commerce = new Commerce(1001, "TechWorld", "123 Rue des Inventions");
        commerce.ajouterContrat(contrat1);
        commerce.ajouterCategorieProduit("Électroménager");

        // Test 1 : Vérifie si le commerce à un contrat actif //
        System.out.println("Test 1 - Commerce en contrat actif ? " + commerce.estEnContrat()); // true

        // Test 2 : Applique une réduction de 8 points //
        float reduction1 = commerce.appliquerReduction(8);
        System.out.println("Test 2 - Réduction appliquée avec 8 points : " + reduction1); // 0.10

        // Test 3 : Applique une réduction de 12 points //
        float reduction2 = commerce.appliquerReduction(12);
        System.out.println("Test 3 - Réduction appliquée avec 12 points : " + reduction2); // 0.20

        // Test 4 : Supprime un contrat //
        boolean removed = commerce.supprimerContrat();
        System.out.println("Test 4 - Contrat supprimé ? " + removed); // true

        // Test 5 : Vérifie qu'il n'y a plus de contrat //
        System.out.println("Test 5 - Commerce en contrat actif ? " + commerce.estEnContrat()); // false
    }
}
