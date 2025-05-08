package test;

import DAO.ProduitDAO;
import DAO.CategorieProduitDAO;
import classe.Produit;
import classe.CategorieProduit;

import java.util.Date;
import java.util.List;

public class ProduitDAOTest {
    public static void main(String[] args) {
        CategorieProduitDAO catDao = new CategorieProduitDAO();
        ProduitDAO dao = new ProduitDAO();

        // Nettoyage initial
        for (Produit p : dao.getAll()) {
            dao.delete(p.getIdProduit());
        }
        for (CategorieProduit cp : catDao.getAll()) {
            catDao.delete(cp.getIdCategorie());
        }

        // Test 1 : insertion d'une catégorie et d'un produit
        CategorieProduit cat = new CategorieProduit(10, "TestCat", 0.10f, 100);
        catDao.insert(cat);
        Produit p = new Produit(100, "ProduitTest", 9.99f, new Date());
        p.ajouterCategorie(cat);
        dao.insert(p);
        System.out.println(" Après INSERT produit ");
        printAll(dao);

        // Test 2 : mise à jour du produit
        p.setPrix(19.99f);
        dao.update(p);
        System.out.println("\n Après UPDATE produit ");
        printAll(dao);

        // Test 3 : récupération individuelle depuis la base
        System.out.println("\n Récupération de getById(100) ");
        Produit loaded = dao.getById(100);
        if (loaded != null) {
            System.out.println("  id           = " + loaded.getIdProduit());
            System.out.println("  nom          = " + loaded.getNom());
            System.out.println("  prix         = " + loaded.getPrix());
            System.out.println("  dateAchat    = " + loaded.getDateAchat());
            System.out.println("  # catégories = " + loaded.getCategorieProduit().size());
        }

        // Test 4 : suppression
        dao.delete(100);
        System.out.println("\n Après DELETE produit ");
        printAll(dao);

        // Cleanup final
        catDao.delete(10);
    }

    private static void printAll(ProduitDAO dao) {
        List<Produit> list = dao.getAll();
        System.out.println("Liste des produits (" + list.size() + ") :");
        for (Produit p : list) {
            System.out.println("  [" + p.getIdProduit() + "] "
                + p.getNom()
                + " – prix=" + p.getPrix()
                + ", catégories=" + p.getCategorieProduit().size());
        }
    }
}
