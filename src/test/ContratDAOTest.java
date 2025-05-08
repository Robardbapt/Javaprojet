package test;

import DAO.ContratDAO;
import DAO.CentreDeTriDAO;
import classe.Contrat;
import classe.CentreDeTri;

import java.util.List;
import java.util.Date;

public class ContratDAOTest {
    public static void main(String[] args) {
        CentreDeTriDAO ctDao = new CentreDeTriDAO();
        ContratDAO dao = new ContratDAO();

        //créer un centre et supprimer ses contrats
        int centreId = 500;
        ctDao.delete(centreId);
        ctDao.insert(new CentreDeTri(centreId, "CentreContrat", "AdresseTest"));

        for (Contrat c : dao.getAll()) {
            dao.delete(c.getIdContrat());
        }

        // Test 1 : insertion d'un contrat
        Date debut = new Date();
        Date fin   = new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000);
        Contrat ct = new Contrat(1000, debut, fin, 5);
        dao.insert(ct, centreId);
        System.out.println("Après INSERT contrat");
        printAll(dao);

        // Test 2 : mise à jour du taux de conversion
        ct.setTauxDeConversion(10);
        dao.update(ct);
        System.out.println("\n Après UPDATE contrat ");
        printAll(dao);

        // Test 3 : récupération individuelle depuis la base
        System.out.println("\n Récupération de getById(1000)");
        Contrat loaded = dao.getById(1000);
        if (loaded != null) {
            System.out.println("  id         = " + loaded.getIdContrat());
            System.out.println("  tauxConv   = " + loaded.getTauxDeConversion() + "%");
            System.out.println("  actif ?    = " + loaded.estActif());
        }

        // Test 4 : suppression
        dao.delete(1000);
        System.out.println("\n Après DELETE contrat ");
        printAll(dao);

        // Nettoyage final
        ctDao.delete(centreId);
    }

    private static void printAll(ContratDAO dao) {
        List<Contrat> list = dao.getAll();
        System.out.println("Liste des contrats (" + list.size() + ") :");
        for (Contrat c : list) {
            System.out.println("  [" + c.getIdContrat() + "] taux=" 
                + c.getTauxDeConversion() + "%, actif=" + c.estActif());
        }
    }
}
