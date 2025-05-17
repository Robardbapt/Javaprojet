package test;

import DAO.DepotDAO;
import DAO.PoubelleDAO;
import DAO.CentreDeTriDAO;
import classe.Depot;
import classe.Dechet;
import classe.Contenu;
import classe.Poubelle;
import classe.CentreDeTri;
import classe.TypePoubelle;

import java.util.List;

public class DepotDAOTest {
    public static void main(String[] args) {
        CentreDeTriDAO ctDao = new CentreDeTriDAO();
        PoubelleDAO pDao = new PoubelleDAO();
        DepotDAO dao = new DepotDAO();

        // Nettoyage initial des centres (cascade sur poubelles et dépôts)
        for (CentreDeTri ct : ctDao.getAll()) {
            ctDao.delete(ct.getIdCentreDeTri(), false, 0);
        }

        // Création d'un centre et d'une poubelle
        CentreDeTri centre = new CentreDeTri(1, "CentreDepot", "1 rue Test");
        ctDao.insert(centre);
        Poubelle p = new Poubelle(10, "PoubDepot", TypePoubelle.PLASTIQUE);
        p.setAdresse("1 rue Test");
        p.setCapaciteMax(50f);
        p.setCapaciteActuelle(0f);
        pDao.insert(p, 1);

        // Nettoyage initial des dépôts
        for (Depot d : dao.getAll()) {
            dao.delete(d.getIdDepot());
        }

        // Test 1 : insertion d'un dépôt
        Dechet dech = new Dechet("PlastiquePiece", Contenu.PLASTIQUE, 1.5f);
        Depot depot1 = new Depot(dech, 5.0f, String.valueOf(p.getIdPoubelle()));
        depot1.setIdDepot(100);
        dao.insert(depot1);
        System.out.println(" Après INSERT depot1 ");
        printAll(dao);

        // Test 2 : mise à jour du dépôt
        depot1.setQuantite(7.5f);
        dao.update(depot1);
        System.out.println("\n Après UPDATE depot1 ");
        printAll(dao);

        // Test 3 : récupération individuelle
        System.out.println("\n Récupération de getById(100) ");
        Depot loaded = dao.getById(100);
        if (loaded != null) {
            System.out.println("  id        = " + loaded.getIdDepot());
            System.out.println("  quantite  = " + loaded.getQuantite());
            System.out.println("  dateDepot = " + loaded.getDateDepot());
            System.out.println("  dechet    = " + loaded.getDechet().getNom());
        }

        // Test 4 : getByPoubelleId
        System.out.println("\n Dépôts pour poubelle '1'");
        List<Depot> list = dao.getByPoubelleId(String.valueOf(p.getIdPoubelle()));
        System.out.println("Nombre de dépôts: " + list.size());

        // Test 5 : suppression
        dao.delete(100);
        System.out.println("\nAprès DELETE depot1 ");
        printAll(dao);

        // Nettoyage final
        pDao.deleteByCentreId(1);
        ctDao.delete(1, false, 0);
    }

    private static void printAll(DepotDAO dao) {
        List<Depot> list = dao.getAll();
        System.out.println("Liste des dépôts (" + list.size() + ") :");
        for (Depot d : list) {
            System.out.println("  [" + d.getIdDepot() + "] quantite=" + d.getQuantite()
                + ", dechet=" + d.getDechet().getNom()
                + ", poubelleId=" + d.getIdPoubelle());
        }
    }
}
