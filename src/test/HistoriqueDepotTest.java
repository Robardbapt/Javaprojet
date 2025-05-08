package test;

import java.util.Map;

import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de HistoriqueDepot 
**/

public class HistoriqueDepotTest {
    public static void main(String[] args) {
        // Création d'un historique //
        HistoriqueDepot historique = new HistoriqueDepot();

        // Création de 3 déchets //
        Dechet plastique1 = new Dechet("Bouteille", Contenu.PLASTIQUE, 4.0f);
        Dechet verre = new Dechet("Bocal", Contenu.VERRE, 2.5f);
        Dechet plastique2 = new Dechet("Barquette", Contenu.PLASTIQUE, 3.0f);

        // Création de 3 dépots //
        Depot d1 = new Depot(plastique1, plastique1.getMasse(), "PB100");
        Depot d2 = new Depot(verre, verre.getMasse(), "PB101");
        Depot d3 = new Depot(plastique2, plastique2.getMasse(), "PB102");

        // Ajout à l'historique //
        historique.ajouterDepot(d1);
        historique.ajouterDepot(d2);
        historique.ajouterDepot(d3);

        // Test 1 : Affichage de l'historique //
        System.out.println("Test 1 - Affichage de tous les dépôts :");
        historique.afficherHistorique();

        // Test 2 : Total des quantités //
        System.out.println("\nTest 2 - Total des déchets déposés : " + historique.totalDeposes() + " kg");

        // Test 3 : Moyenne //
        System.out.println("Test 3 - Moyenne par dépôt : " + historique.moyenneDepot() + " kg");

        // Test 4 : Filtrer par contenu //
        System.out.println("\nTest 4 - Dépôts contenant du plastique :");
        for (Depot d : historique.filtrerParContenu(Contenu.PLASTIQUE)) {
            System.out.println(d.afficherDescription());
        }

        // Test 5 : Statistiques par contenu //
        System.out.println("\nTest 5 - Répartition des déchets par type :");
        Map<Contenu, Float> repartition = historique.totalParContenu();
        for (Contenu c : repartition.keySet()) {
            System.out.println("- " + c + " : " + repartition.get(c) + " kg");
        }
    }
}
