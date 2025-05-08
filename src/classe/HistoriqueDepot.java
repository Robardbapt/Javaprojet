package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un HistoriqueDepot est une classe qui représente l'ensemble des dépots d'un compte  
**/

public class HistoriqueDepot {

    private List<Depot> depots;

    // constructeur de la classe //
    public HistoriqueDepot() {
        this.depots = new ArrayList<>();
    }

    // Ajoute un depot à l'historique //
    public void ajouterDepot(Depot depot) {
        if (depot != null) {
            depots.add(depot);
        }
    }

    // Donne un total en quantité mais par contenu //
    public Map<Contenu, Float> totalParContenu() {
        Map<Contenu, Float> map = new HashMap<>();
        for (Depot d : depots) {
            Contenu c = d.getContenu();
            map.put(c, map.getOrDefault(c, 0f) + d.getQuantite());
        }
        return map;
    }

    // Affiche tous les dépots //
    public void afficherHistorique() {
        if (depots.isEmpty()) {
            System.out.println("Aucun dépôt enregistré.");
        } else {
            for (Depot d : depots) {
                System.out.println(d.afficherDescription());
            }
        }
    }

    // Calcule la quantité totale déposée//
    public float totalDeposes() {
        float total = 0f;
        for (Depot d : depots) {
            total += d.getQuantite();
        }
        return total;
    }

    // Retourne les dépots d'un type de contenu //
    public List<Depot> filtrerParContenu(Contenu contenu) {
        List<Depot> filtres = new ArrayList<>();
        for (Depot d : depots) {
            if (d.getContenu() == contenu) {
                filtres.add(d);
            }
        }
        return filtres;
    }

    // Retourne la moyenne des quantités des dépots //
    public float moyenneDepot() {
        if (depots.isEmpty()) return 0f;
        return totalDeposes() / depots.size();
    }

    // getter //
    public List<Depot> getDepots() {
        return depots;
    }
}
