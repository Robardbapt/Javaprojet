package test;

import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de Depot 
**/

public class DepotTest {
    public static void main(String[] args) {
        // Test 1 : Création d’un déchet //
        Dechet dechet1 = new Dechet("Bouteille", Contenu.PLASTIQUE, 5f);
        Depot depot1 = new Depot(dechet1, dechet1.getMasse(), "PB001");

        System.out.println("Test 1 - Description dépôt plastique :");
        System.out.println(depot1.afficherDescription());

        // Test 2 : Création d’un déchet différent //
        Dechet dechet2 = new Dechet("Bocal", Contenu.VERRE, 3f);
        Depot depot2 = new Depot(dechet2, dechet2.getMasse(), "PB002");

        System.out.println("\nTest 2 - Description dépôt verre :");
        System.out.println(depot2.afficherDescription());

        // Test 3 : Vérification des points //
        System.out.println("\nTest 3 - Points gagnés (plastique) : " + depot1.getPointsGagnes());
        System.out.println("Points gagnés (verre) : " + depot2.getPointsGagnes());

        // Test 4 : Changement de déchet et recalcul //
        Dechet nouveauDechet = new Dechet("Canette", Contenu.METAUX, 4f);
        depot1.setDechet(nouveauDechet);
        depot1.setQuantite(4f);
        depot1.setPointsGagnes(depot1.calculerValeurDepot());

        System.out.println("\nTest 4 - Nouvelle description après modification :");
        System.out.println(depot1.afficherDescription());
    }
}

