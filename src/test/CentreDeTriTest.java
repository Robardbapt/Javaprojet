package test;

import java.util.*;
import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de CentreDeTri 
**/

public class CentreDeTriTest {
    public static void main(String[] args) {
        // Création d’un centre de tri //
        CentreDeTri centre = new CentreDeTri(1, "Centre Nord", "12 rue Verte");

        // Création de deux poubelles //
        Poubelle p1 = new Poubelle(101, "Poubelle Plastique", TypePoubelle.PLASTIQUE);
        Poubelle p2 = new Poubelle(102, "Poubelle Papier", TypePoubelle.PAPIER);

        // Test 1 : Placer deux poubelles //
        centre.placerPoubelle(p1, "10 rue des Lilas");
        centre.placerPoubelle(p2, "10 rue des Lilas");

        // Test 2 : Retirer une poubelle //
        centre.retirerPoubelle(p1, "10 rue des Lilas");

        // Test 3 : Collecte des déchets //
        centre.collecteDechets(p2);

        // Test 4 : Affichage du contenu des poubelles //
        System.out.println("\nTest 4 - Poubelles restantes à '10 rue des Lilas' :");
        List<Poubelle> restantes = centre.getPoubellesPlacees().get("10 rue des Lilas");
        if (restantes != null) {
            for (Poubelle p : restantes) {
                System.out.println("- " + p.getNom() + " (" + p.getType() + ")");
            }
        } else {
            System.out.println("Aucune poubelle restante.");
        }
    }
}
