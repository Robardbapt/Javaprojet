package test;

import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de Poubelle
**/

public class PoubelleTest {
    public static void main(String[] args) {
        // Création de la poubelle //
        Poubelle poubelle = new Poubelle(1, "Entrée", TypePoubelle.PLASTIQUE);
        poubelle.setCapaciteMax(50f); 
        poubelle.setAdresse("12 rue Verte");

        // Création utilisateur //
        Compte utilisateur = new Compte(1, "Justin", "justin@bieber.com", "azerty", "12 rue Verte");
        utilisateur.ajouterPoubelle(poubelle); 

        // Création déchet //
        Dechet bouteille = new Dechet("Bouteille", Contenu.PLASTIQUE, 10f);
        Depot depot1 = new Depot(bouteille, bouteille.getMasse(), poubelle.getIdPoubelle() + "");

        // Création déchet qui ne marche pas //
        Dechet papier = new Dechet("Feuille", Contenu.PAPIER, 5f);
        Depot depot2 = new Depot(papier, papier.getMasse(), poubelle.getIdPoubelle() + "");

        // Test 1 : vérification accès //
        System.out.println("Test 1 - Accès autorisé ? " + poubelle.identifierUtilisateur(utilisateur)); // true

        // Test 2 : dépôt compatible //
        float restant1 = poubelle.gererQuantiteDechet(depot1);
        System.out.println("Test 2 - Dépôt plastique : capacité restante = " + restant1 + " kg"); // 40 kg

        // Test 3 : dépôt incompatible //
        float restant2 = poubelle.gererQuantiteDechet(depot2);
        System.out.println("Test 3 - Dépôt papier : rejeté ? " + (restant2 == -1f)); // true

        // Test 4 : attribution de points //
        int points = poubelle.attribuerPoint();
        System.out.println("Test 4 - Points attribués : " + points); // ex : 25

        // Test 5 : remplissage de la poubelle //
        Dechet bouteille2 = new Dechet("Bouteille", Contenu.PLASTIQUE, 40f);
        Depot depot3 = new Depot(bouteille2, bouteille2.getMasse(), poubelle.getIdPoubelle() + "");
        poubelle.gererQuantiteDechet(depot3);
        System.out.println("Test 5 - Poubelle pleine ? " + poubelle.isEstPleine()); // true

        // Test 6 : notification //
        System.out.print("Test 6 - Notification : ");
        poubelle.notifierCentreTri();

        // Test 7 : vidage //
        poubelle.vider();
        System.out.println("Test 7 - Après vidage, pleine ? " + poubelle.isEstPleine()); // false
    }
}
