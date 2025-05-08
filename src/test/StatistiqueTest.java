package test;

import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de Statistique 
**/

public class StatistiqueTest {
    public static void main(String[] args) {
        // Création d'une Statistique //
        Statistique stats = new Statistique();

        // Création de 3 déchets
        Dechet plastique = new Dechet("Bouteille", Contenu.PLASTIQUE, 3.5f);
        Dechet papier = new Dechet("Papier journal", Contenu.PAPIER, 2.0f);
        Dechet verre = new Dechet("Bocal", Contenu.VERRE, 4.5f);

        // Création de 3 dépots
        Depot d1 = new Depot(plastique, plastique.getMasse(), "PB001");
        Depot d2 = new Depot(papier, papier.getMasse(), "PB002");
        Depot d3 = new Depot(verre, verre.getMasse(), "PB003");

        // Test 1 : Enregistrement des dépôts //
        stats.enregistrerDepot(d1);
        stats.enregistrerDepot(d2);
        stats.enregistrerDepot(d3);
        System.out.println("Test 1 - Dépôts enregistrés.");

        // Test 2 : Calcul total //
        System.out.println("Test 2 - Total de déchets : " + stats.calculerTotalDechets() + " kg");

        // Test 3 : Moyenne //
        System.out.println("Test 3 - Production moyenne : " + stats.productionMoyenne() + " kg");

        // Test 4 : Affichage de l'historique //
        System.out.println("Test 4 - Historique des dépôts :");
        stats.afficherHistorique();
    }
}
