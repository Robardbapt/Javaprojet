package test;

import classe.*;
import java.util.*;

/**
 * Fais partie du package classeTest qui représente l'ensemble des classes de test
 * Classe de test de Compte 
**/

public class CompteTest {
    public static void main(String[] args) {
        // Création du compte //
        Compte utilisateur = new Compte(1, "Justin", "justin@bieber.com", "password123", "5 rue des Lilas", null);

        // Test 1 : Connexion réussie //
        System.out.println("Test 1 - Connexion réussie ? " + utilisateur.seConnecter("justin@bieber.com", "password123")); // true

        // Test 2 : Connexion échouée //
        System.out.println("Test 2 - Connexion échouée ? " + utilisateur.seConnecter("justin@bieber.com", "wrongpass")); // false

        // Création d'une poubelle //
        Poubelle poubellePlastique = new Poubelle(10, "Entrée", TypePoubelle.PLASTIQUE);
        poubellePlastique.setAdresse("5 rue des Lilas"); // même adresse que l'utilisateur
        utilisateur.ajouterPoubelle(poubellePlastique);

        // Création d’un déchet //
        Dechet bouteille = new Dechet("Bouteille", Contenu.PLASTIQUE, 5f); // masse obligatoire

        // Test 3 : Déposer un déchet dans une poubelle //
        boolean depotReussi = utilisateur.deposerDechets(poubellePlastique, bouteille);
        System.out.println("Test 3 - Dépôt dans poubelle autorisée : " + depotReussi); // true
        System.out.println("Points fidélité après dépôt : " + utilisateur.getPointFidelite());

        // Test 4 : Échange de points //
        BonReduction bon = new BonReduction("Bon 10%", 5); // 5 points requis
        utilisateur.ajouterBon(bon); // il faut d'abord le posséder
        boolean echangeOk = utilisateur.echangerPoints(bon);
        System.out.println("Test 4 - Bon échangé ? " + echangeOk);
        System.out.println("Points restants : " + utilisateur.getPointFidelite());

        // Test 5 : Achat d'un produit //
        Produit p = new Produit();
        System.out.println("Test 5 - Achat produit :");
        for (String ligne : utilisateur.acheterProduits(p)) {
            System.out.println(ligne);
        }
    }
}
