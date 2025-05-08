package main;

import test.*;
/**
 * Fais partie du package classeMain qui représente la classe Main qui lance l'ensemble des tests
 * Main lance l'ensemble des tests  
**/

public class MainTest {
    public static void main(String[] args) {
        System.out.println("TEST CLASSE CATEGORIEPRODUIT");
        CategorieProduitTest.main(null);

        System.out.println("\nTEST CLASSE PRODUIT");
        ProduitTest.main(null);

        System.out.println("\nTEST CLASSE CONTRAT");
        ContratTest.main(null);

        System.out.println("\nTEST CLASSE COMMERCE");
        CommerceTest.main(null);

        System.out.println("\nTEST CLASSE CENTRE DE TRI");
        CentreDeTriTest.main(null);

        System.out.println("\nTEST CLASSE POUBELLE");
        PoubelleTest.main(null);

        System.out.println("\nTEST CLASSE DEPOT");
        DepotTest.main(null);

        System.out.println("\nTEST CLASSE DECHET");
        DechetTest.main(null);

        System.out.println("\nTEST CLASSE COMPTE");
        CompteTest.main(null);

        System.out.println("\nTEST CLASSE BONREDUCTION");
        BonReductionTest.main(null);

        System.out.println("\nTEST CLASSE HISTORIQUEDEPOT");
        HistoriqueDepotTest.main(null);

        System.out.println("\nTEST CLASSE STATISTIQUE");
        StatistiqueTest.main(null);
    }
}

