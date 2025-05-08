package test;

import DAO.DechetDAO;
import classe.Dechet;
import classe.Contenu;

import java.util.List;

public class DechetDAOTest {
    public static void main(String[] args) {
        DechetDAO dao = new DechetDAO();

        // Nettoyage initial
        for (Dechet d : dao.getAll()) {
            dao.delete(d.getNom());
        }

        // Test 1 : insertion d'un déchet
        Dechet dech1 = new Dechet("VerreBouteille", Contenu.VERRE, 2.0f);
        dao.insert(dech1);
        System.out.println(" Après INSERT dech1 ");
        printAll(dao);

        // Test 2 : mise à jour du déchet
        dech1.setMasse(3.5f);
        dao.update(dech1);
        System.out.println("\n Après UPDATE dech1 ");
        printAll(dao);

        // Test 3 : récupération individuelle depuis la base
        System.out.println("\n Récupération de getByNom(\"VerreBouteille\") ");
        Dechet loaded = dao.getByNom("VerreBouteille");
        if (loaded != null) {
            System.out.println("  nom    = " + loaded.getNom());
            System.out.println("  contenu= " + loaded.getContenu());
            System.out.println("  masse  = " + loaded.getMasse());
        }

        // Test 4 : suppression
        dao.delete("VerreBouteille");
        System.out.println("\n Après DELETE dech1 ");
        printAll(dao);
    }

    private static void printAll(DechetDAO dao) {
        List<Dechet> list = dao.getAll();
        System.out.println("Liste des déchets (" + list.size() + ") :");
        for (Dechet d : list) {
            System.out.println("  [" + d.getNom() + "] type=" 
                + d.getContenu() + ", masse=" + d.getMasse());
        }
    }
}
