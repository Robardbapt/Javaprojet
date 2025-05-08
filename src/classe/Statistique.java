package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Une statistique est une classe qui représente des infos sur les dépots enregistrées par le CentreDeTri 
**/

public class Statistique {

    private List<Depot> historiqueDepot;

    // Constructeur de base //
    public Statistique() {
        this.historiqueDepot = new ArrayList<>();
    }

    // Enregistre un depot //
    public float enregistrerDepot(Depot d) {
        if (d != null) {
            historiqueDepot.add(d);
            return d.getQuantite();
        }
        return 0f;
    }

    // Calcule la quantité totale des dépots //
    public float calculerTotalDechets() {
        float total = 0f;
        for (Depot d : historiqueDepot) {
            total += d.getQuantite();
        }
        return total;
    }

    // Calcule la quantité moyenne des dépots //
    public float productionMoyenne() {
        if (historiqueDepot.isEmpty()) return 0f;
        return calculerTotalDechets() / historiqueDepot.size();
    }
    
    // getter //
    public List<Depot> getHistoriqueDepot() {
        return historiqueDepot;
    }

    // Affiche l'ensemble des infos //
    public void afficherHistorique() {
        for (Depot d : historiqueDepot) {
            System.out.println("- " + d.getQuantite() + " kg de " + d.getDechet().getMasse());
        }
    }
}
