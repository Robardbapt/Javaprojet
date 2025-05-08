package test;

import classe.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de Dechet 
**/

public class DechetTest {
    public static void main(String[] args) {
        // Test 1 : Création simple d’un déchet //
        Dechet dechet1 = new Dechet("Bouteille", Contenu.PLASTIQUE);
        System.out.println("Test 1 - Déchet 1 : " + dechet1.afficherDescription());

        // Test 2 : Création complète d’un déchet //
        Dechet dechet2 = new Dechet("Feuille A4", Contenu.PAPIER, 0.025f);
        System.out.println("Test 2 - Déchet 2 : " + dechet2.afficherDescription());

        // Test 3 : Modification du contenu et de la masse //
        dechet1.setContenu(Contenu.METAUX);
        dechet1.setMasse(1.5f);
        System.out.println("Test 3 - Déchet 1 modifié : " + dechet1.afficherDescription());
    }
}
