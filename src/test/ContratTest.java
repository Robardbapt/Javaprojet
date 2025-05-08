package test;

import java.util.*;
import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de Contrat  
**/

public class ContratTest {
    public static void main(String[] args) {
        // Création de deux catégories //
        CategorieProduit cat1 = new CategorieProduit(1, "Électroménager", 0.10f, 8);
        CategorieProduit cat2 = new CategorieProduit(2, "High-Tech", 0.15f, 12);

        // Définition des dates du contrat //
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.MARCH, 1);
        Date dateDebut = calendar.getTime();

        calendar.set(2025, Calendar.MARCH, 1);
        Date dateFin = calendar.getTime();

        // Création du contrat //
        Contrat contrat = new Contrat(100, dateDebut, dateFin, 20);
        contrat.ajouterCategorie(cat1);
        contrat.ajouterCategorie(cat2);

        // Test 1 : affichage des règles //
        System.out.println("Test 1 - Règles d'utilisation :");
        System.out.println(contrat.definirRegleUtilisation());

        // Test 2 : afficher les catégories //
        System.out.println("\nTest 2 - Catégories concernées :");
        contrat.getCategoriesProduits();

        // Test 3 : affichage du taux //
        System.out.println("\nTest 3 - Taux de conversion :");
        contrat.getTaux();

        // Test 4 : affichage activité du contrat //
        System.out.println("\nTest 4 - Contrat actif ? " + contrat.estActif());

        // Test 5 : changement date de fin du contrat //
        Calendar newCal = Calendar.getInstance();
        newCal.set(2026, Calendar.JANUARY, 1);
        Date nouvelleFin = newCal.getTime();
        contrat.renouvelerContrat(nouvelleFin);
        System.out.println("Test 5 - Nouvelle date de fin : " + contrat.getDateFin());
    }
}
