package test;

import DAO.CentreDeTriDAO;
import classe.CentreDeTri;

import java.util.List;

public class CentreDeTriDAOTest {
    public static void main(String[] args) {
        CentreDeTriDAO dao = new CentreDeTriDAO();

        // Nettoyage initial
        for (CentreDeTri ct : dao.getAll()) {
            dao.delete(ct.getIdCentreDeTri(), false, 0);
        }

        // Test 1 : insertion d'un centre
        CentreDeTri ct1 = new CentreDeTri(100, "CentreTest", "14 Avenue du Parc");
        dao.insert(ct1);
        System.out.println(" Après INSERT ct1 ");
        printAll(dao);

        // Test 2 : mise à jour du centre
        ct1.setNom("CentreTestModifié");
        ct1.setAdresse("15 Avenue du Parc");
        dao.update(ct1);
        System.out.println("\n Après UPDATE ct1 ");
        printAll(dao);

        // Test 3 : récupération individuelle depuis la base
        System.out.println("\n Récupération de getById(100) ");
        CentreDeTri loaded = dao.getById(100);
        if (loaded != null) {
            System.out.println("id      = " + loaded.getIdCentreDeTri());
            System.out.println("nom     = " + loaded.getNom());
            System.out.println("adresse = " + loaded.getAdresse());
        }

        // Test 4 : suppression
        dao.delete(100, false, 0);
        System.out.println("\n Après DELETE ct1 ");
        printAll(dao);
    }

    private static void printAll(CentreDeTriDAO dao) {
        List<CentreDeTri> list = dao.getAll();
        System.out.println("Liste des centres (" + list.size() + ") :");
        for (CentreDeTri ct : list) {
            System.out.println("  [" + ct.getIdCentreDeTri() + "] "
                + ct.getNom() + " – " + ct.getAdresse());
        }
    }
}
