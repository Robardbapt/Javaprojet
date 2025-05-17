package test;

import DAO.CommerceDAO;
import DAO.ContratDAO;
import DAO.CentreDeTriDAO;      // <-- import additionnel
import classe.Commerce;
import classe.Contrat;
import classe.CentreDeTri;      // <-- import additionnel

import java.util.Date;
import java.util.List;

public class CommerceDAOTest {
    public static void main(String[] args) {
        CommerceDAO dao = new CommerceDAO();
        ContratDAO cDao = new ContratDAO();
        CentreDeTriDAO ctDao = new CentreDeTriDAO();  // <-- on gère aussi les centres

        // 1) Nettoyage initial
        for (Commerce c : dao.getAll()) {
            dao.delete(c.getIdCommerce());
        }
        for (CentreDeTri ct : ctDao.getAll()) {
            ctDao.delete(ct.getIdCentreDeTri(), false, 0);
        }
        for (Contrat c : cDao.getAll()) {
            cDao.delete(c.getIdContrat());
        }

        // 2) Créer un commerce
        int shopId = 200;
        Commerce shop = new Commerce(shopId, "MonMagasin", "10 rue d'avron");
        shop.ajouterCategorieProduit("Alimentation");
        shop.ajouterCategorieProduit("Textile");
        dao.insert(shop);
        System.out.println(" Après INSERT shop ");
        printAll(dao);

        // 3) Mettre à jour le commerce
        shop.setNom("MonMagasinPlus");
        shop.setAdresse("20 rue d'avron");
        dao.update(shop);
        System.out.println("\n Après UPDATE shop ");
        printAll(dao);

        // 4) Insérer un CentreDeTri avec le même ID que le shop (pour les tests)
        CentreDeTri centre = new CentreDeTri(shopId, "CentreAssoc", "Rue des Tests");
        ctDao.insert(centre);

        // 5) Insérer le contrat dans Partenariat en passant l'ID du centre
        Contrat contrat = new Contrat(
            300,
            new Date(),
            new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000),
            5
        );
        cDao.insert(contrat, shopId);

        // 6) Lier ensuite le contrat au commerce et mettre à jour
        shop.ajouterContrat(contrat);
        dao.update(shop);
        System.out.println("\n Après INSERT Contrat pour shop ");
        Commerce loaded = dao.getById(shopId);
        System.out.println("Contrats liés à shop : " + loaded.getContrats().size());

        // 7) Détails de getById
        System.out.println("\n Détails de getById(" + shopId + ") ");
        System.out.println("id        = " + loaded.getIdCommerce());
        System.out.println("nom       = " + loaded.getNom());
        System.out.println("adresse   = " + loaded.getAdresse());
        System.out.println("catégories= " + loaded.getCategoriesProduits());

        // 8) Suppression
        dao.delete(shopId);
        System.out.println("\n Après DELETE shop ");
        printAll(dao);

        // 9) Nettoyage final
        cDao.delete(300);
        ctDao.delete(shopId, false, shopId);
    }

    private static void printAll(CommerceDAO dao) {
        List<Commerce> list = dao.getAll();
        System.out.println("Liste des commerces (" + list.size() + ") :");
        for (Commerce c : list) {
            System.out.println("  [" + c.getIdCommerce() + "] "
                + c.getNom() + " – " + c.getAdresse()
                + " – catégories=" + c.getCategoriesProduits());
        }
    }
}
