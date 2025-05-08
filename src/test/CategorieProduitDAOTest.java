package test;

import DAO.CategorieProduitDAO;
import classe.CategorieProduit;

import java.util.List;

public class CategorieProduitDAOTest {
    public static void main(String[] args) {
        CategorieProduitDAO dao = new CategorieProduitDAO();

        // Nettoyage initial
        for (CategorieProduit cp : dao.getAll()) {
            dao.delete(cp.getIdCategorie());
        }

        // Test 1 : insertion d'une catégorie
        CategorieProduit cat1 = new CategorieProduit(1, "Electronique", 0.10f, 100);
        dao.insert(cat1);
        System.out.println(" Après INSERT cat1 ");
        printAll(dao);

        // Test 2 : mise à jour de la catégorie
        cat1.setNom("HighTech");
        cat1.setTauxReduction(0.15f);
        dao.update(cat1);
        System.out.println("\n Après UPDATE cat1 ");
        printAll(dao);

        // Test 3 : récupération individuelle depuis la base
        System.out.println("\n Récupération de getById(1) ");
        CategorieProduit loaded = dao.getById(1);
        if (loaded != null) {
            System.out.println("id        = " + loaded.getIdCategorie());
            System.out.println("nom       = " + loaded.getNom());
            System.out.println("tauxRed   = " + (loaded.getTauxReduction() * 100) + "%");
            System.out.println("ptsNeeded = " + loaded.getPointNecessaire());
        }

        // Test 4 : suppression
        dao.delete(1);
        System.out.println("\n Après DELETE cat1 ");
        printAll(dao);
    }

    private static void printAll(CategorieProduitDAO dao) {
        List<CategorieProduit> list = dao.getAll();
        System.out.println("Liste des catégories (" + list.size() + ") :");
        for (CategorieProduit cp : list) {
            System.out.println("  [" + cp.getIdCategorie() + "] "
                + cp.getNom()
                + " – taux=" + (cp.getTauxReduction() * 100) + "%"
                + ", ptsNec=" + cp.getPointNecessaire());
        }
    }
}
