package DAO;

import classe.HistoriqueDepot;
import classe.Depot;
import classe.Poubelle;

/*/ classe DAO de la classe HistoriqueDepot, permettant son implémentation dans une base de données/*/
public class HistoriqueDepotDAO {

    private final DepotDAO depotDAO         = new DepotDAO();
    private final PoubelleDAO poubelleDAO   = new PoubelleDAO();

/*/ méthode récupérant l'historique complet des dépots /*/
    public HistoriqueDepot getAllHistory() {
        HistoriqueDepot hist = new HistoriqueDepot();
        for (Poubelle p : poubelleDAO.getAll()) {
            for (Depot d : p.getHistoriqueDepots()) {
                hist.ajouterDepot(d);
            }
        }
        return hist;
    }

/*/ méthode récupérant l'historique dépot pour une poubelle /*/
    public HistoriqueDepot getByPoubelleId(String idPoubelle) {
        HistoriqueDepot hist = new HistoriqueDepot();
        for (Depot d : depotDAO.getByPoubelleId(idPoubelle)) {
            hist.ajouterDepot(d);
        }
        return hist;
    }

/*/ méthode récupérant l'historique dépot pour l'ensemble d'un centre de tri /*/
    public HistoriqueDepot getByCentreId(int idCentre) {
        HistoriqueDepot hist = new HistoriqueDepot();
        // On prend toutes les poubelles du centre, puis leurs dépôts
        for (Poubelle p : poubelleDAO.getByCentreId(idCentre)) {
            for (Depot d : p.getHistoriqueDepots()) {
                hist.ajouterDepot(d);
            }
        }
        return hist;
    }
}
