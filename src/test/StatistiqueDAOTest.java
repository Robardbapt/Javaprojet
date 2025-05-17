package test;

import DAO.StatistiqueDAO;
import DAO.DepotDAO;
import DAO.PoubelleDAO;
import DAO.CentreDeTriDAO;
import classe.Statistique;
import classe.Depot;
import classe.Dechet;
import classe.Contenu;
import classe.Poubelle;
import classe.CentreDeTri;
import classe.TypePoubelle;

public class StatistiqueDAOTest {
    public static void main(String[] args) {
        CentreDeTriDAO ctDao = new CentreDeTriDAO();
        PoubelleDAO pDao = new PoubelleDAO();
        DepotDAO dDao = new DepotDAO();
        StatistiqueDAO sDao = new StatistiqueDAO();

        // Cleanup éventuel
        ctDao.delete(1, false, 0);

        // Test initial sans dépôts
        System.out.println(" Test initial sans dépôts ");
        Statistique s0 = sDao.getByCentreId(1);
        System.out.println("Total déchets centre 1: " + s0.calculerTotalDechets());
        System.out.println("Production moyenne centre 1: " + s0.productionMoyenne());

        // Création d'un centre et d'une poubelle
        CentreDeTri centre = new CentreDeTri(1, "CentreStat", "Stat Rue");
        ctDao.insert(centre);
        Poubelle p = new Poubelle(1, "PoubStat", TypePoubelle.PAPIER);
        p.setAdresse("Stat Rue");
        p.setCapaciteMax(100f);
        p.setCapaciteActuelle(0f);
        pDao.insert(p, 1);

        // Cleanup des anciens dépôts
        for (Depot d : dDao.getAll()) {
            dDao.delete(d.getIdDepot());
        }

        // Insertion de deux dépôts
        Dechet d1 = new Dechet("Papier1", Contenu.PAPIER, 2.0f);
        Depot dep1 = new Depot(d1, 2.0f, String.valueOf(p.getIdPoubelle()));
        dep1.setIdDepot(2000);
        dDao.insert(dep1);

        Dechet d2 = new Dechet("Papier2", Contenu.PAPIER, 4.0f);
        Depot dep2 = new Depot(d2, 4.0f, String.valueOf(p.getIdPoubelle()));
        dep2.setIdDepot(2001);
        dDao.insert(dep2);

        // Test après dépôts
        System.out.println("\n Test après insertion de dépôts ");
        Statistique s1 = sDao.getByCentreId(1);
        System.out.println("Total déchets centre 1: " + s1.calculerTotalDechets());
        System.out.println("Production moyenne centre 1: " + s1.productionMoyenne());

        // Cleanup final
        dDao.delete(2000);
        dDao.delete(2001);
        pDao.deleteByCentreId(1);
        ctDao.delete(1, false, 0);
    }
}
