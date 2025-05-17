package test;

import DAO.PoubelleDAO;
import DAO.CentreDeTriDAO;
import classe.Poubelle;
import classe.CentreDeTri;
import classe.TypePoubelle;

import java.util.List;

public class PoubelleDAOTest {
    public static void main(String[] args) {
        CentreDeTriDAO ctDao = new CentreDeTriDAO();
        PoubelleDAO dao = new PoubelleDAO();

        // Nettoyage initial : supprimer tous les centres (cascade supprime les poubelles)
        for (CentreDeTri ct : ctDao.getAll()) {
            ctDao.delete(ct.getIdCentreDeTri(), false, 0);
        }

        // Test 1 : insertion d'un centre et d'une poubelle
        CentreDeTri centre = new CentreDeTri(10, "CentreTest", "1 rue Test");
        ctDao.insert(centre);

        Poubelle p = new Poubelle(100, "PoubelleTest", TypePoubelle.PAPIER);
        p.setAdresse("1 rue Test");
        p.setCapaciteMax(100f);
        p.setCapaciteActuelle(10f);
        dao.insert(p, 10);

        System.out.println(" Après INSERT poubelle ");
        printAll(dao, 10);

        // Test 2 : mise à jour de la capacité actuelle
        p.setCapaciteActuelle(20f);
        dao.update(p);
        System.out.println("\n Après UPDATE poubelle ");
        printAll(dao, 10);

        // Test 3 : récupération individuelle depuis la base
        System.out.println("\n Récupération de getById(100) ");
        Poubelle loaded = dao.getById(100);
        if (loaded != null) {
            System.out.println("id=" + loaded.getIdPoubelle()
                + ", nom=" + loaded.getNom()
                + ", type=" + loaded.getType()
                + ", adresse=" + loaded.getAdresse()
                + ", capaciteMax=" + loaded.getCapaciteMax()
                + ", capaciteActuelle=" + loaded.getCapaciteActuelle());
        }

        // Test 4 : suppression des poubelles du centre
        dao.deleteByCentreId(10);
        System.out.println("\n Après deleteByCentreId(10) ");
        printAll(dao, 10);

        // Nettoyage final
        ctDao.delete(10, false, 0);
    }

    private static void printAll(PoubelleDAO dao, int idCentre) {
        List<Poubelle> list = dao.getByCentreId(idCentre);
        System.out.println("Liste pour centre " + idCentre + " (" + list.size() + ") :");
        for (Poubelle p : list) {
            System.out.println("  [" + p.getIdPoubelle() + "] "
                + p.getNom()
                + ", type=" + p.getType()
                + ", adr=" + p.getAdresse()
                + ", capMax=" + p.getCapaciteMax()
                + ", capAct=" + p.getCapaciteActuelle());
        }
    }
}
