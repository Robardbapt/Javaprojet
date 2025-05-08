package test;

import DAO.CentreDeTriDAO;
import DAO.PoubelleDAO;
import DAO.DepotDAO;
import DAO.HistoriqueDepotDAO;
import classe.CentreDeTri;
import classe.Poubelle;
import classe.Depot;
import classe.Dechet;
import classe.Contenu;
import classe.TypePoubelle;

import java.util.List;

public class HistoriqueDepotDAOTest {
    public static void main(String[] args) {
        CentreDeTriDAO ctDao = new CentreDeTriDAO();
        PoubelleDAO pDao = new PoubelleDAO();
        DepotDAO dDao = new DepotDAO();
        HistoriqueDepotDAO hDao = new HistoriqueDepotDAO();

        // Nettoyage initial
        for (CentreDeTri ct : ctDao.getAll()) {
            ctDao.delete(ct.getIdCentreDeTri());
        }

        // Création d'un centre et d'une poubelle
        CentreDeTri centre = new CentreDeTri(7, "CentreHist", "7 rue Histo");
        ctDao.insert(centre);
        Poubelle poub = new Poubelle(7, "PoubHist", TypePoubelle.BIODECHET);
        poub.setAdresse("7 rue Histo"); 
        poub.setCapaciteMax(50f);
        poub.setCapaciteActuelle(0f);
        pDao.insert(poub, 7);

        // Insertion de quelques dépôts
        Dechet d1 = new Dechet("Bio1", Contenu.BIODECHET, 2.0f);
        Depot dep1 = new Depot(d1, 3.0f, "7");
        dep1.setIdDepot(900);
        dDao.insert(dep1);

        Dechet d2 = new Dechet("Bio2", Contenu.BIODECHET, 1.0f);
        Depot dep2 = new Depot(d2, 5.0f, "7");
        dep2.setIdDepot(901);
        dDao.insert(dep2);

        // Test 1 : Historique complet (tous dépôts)
        System.out.println(" Test 1 : getAllHistory ");
        List<Depot> all = hDao.getAllHistory().getDepots();
        System.out.println("Nombre total de dépôts: " + all.size());
        for (Depot d : all) {
            System.out.println("  [" + d.getIdDepot() + "] qte=" + d.getQuantite() 
                + ", poubId=" + d.getIdPoubelle());
        }

        // Test 2 : Historique d'une poubelle
        System.out.println("\n Test 2 : getByPoubelleId(\"7\") ");
        List<Depot> byP = hDao.getByPoubelleId("7").getDepots();
        System.out.println("Dépôts dans poubelle 7: " + byP.size());

        // Test 3 : Historique d'un centre
        System.out.println("\n Test 3 : getByCentreId(7) ");
        List<Depot> byC = hDao.getByCentreId(7).getDepots();
        System.out.println("Dépôts pour centre 7: " + byC.size());

        // Test 4 : suppression et re-test
        dDao.delete(900);
        System.out.println("\n delete(900)");
        System.out.println("getByPoubelleId(\"7\"): " + hDao.getByPoubelleId("7").getDepots().size());

        // Nettoyage final
        dDao.delete(901);
        pDao.deleteByCentreId(7);
        ctDao.delete(7);
    }
}
