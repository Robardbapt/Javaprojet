package test;

import DAO.CompteDAO;
import classe.Compte;

import java.util.List;

public class CompteDAOTest {
    public static void main(String[] args) {
        CompteDAO dao = new CompteDAO();

        // Nettoyage initial
        for (Compte c : dao.getAll()) {
            dao.delete(c.getIdCompte());
        }

        // Test 1 : insertion d'un compte
        Compte compte1 = new Compte(1, "Justin", "justin@bieber.com", "mdp", "1 Rue JB", null);
        dao.insert(compte1);
        System.out.println(" Après INSERT compte1 ");
        printAll(dao);

        // Test 2 : mise à jour du compte
        compte1.setPointFidelite(42f);
        compte1.setNom("Justin B.");
        dao.update(compte1);
        System.out.println("\n Après UPDATE compte1 ");
        printAll(dao);

        // Test 3 : récupération individuelle depuis la base
        System.out.println("\n Récupération individuelle de compte1 ");
        Compte loaded = dao.getById(1);
        if (loaded != null) {
            System.out.println("  id       = " + loaded.getIdCompte());
            System.out.println("  nom      = " + loaded.getNom());
            System.out.println("  email    = " + loaded.getEmail());
            System.out.println("  adresse  = " + loaded.getAdresse());
            System.out.println("  points   = " + loaded.getPointFidelite());
        }

        // Test 4 : suppression
        dao.delete(1);
        System.out.println("\n Après DELETE compte1 ");
        printAll(dao);
    }

    private static void printAll(CompteDAO dao) {
        List<Compte> list = dao.getAll();
        System.out.println("Liste des comptes (" + list.size() + ") :");
        for (Compte c : list) {
            System.out.println("  [" + c.getIdCompte() + "] "
                + c.getNom() + " <" + c.getEmail() + "> – pts=" 
                + c.getPointFidelite() + ", addr=\"" 
                + c.getAdresse() + "\"");
        }
    }
}
