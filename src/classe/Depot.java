package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un depot est une classe qui représente les déchets déposés par un ménage dans une poubelle 
**/

public class Depot {

    private int idDepot;
    private float quantite;
    private Date dateDepot;
    private float pointsGagnes;
    private String idPoubelle;
    private Dechet dechet; 

    // Constructeur de base //
    public Depot() {
        this.dateDepot = new Date();
    }

    // Constructeur complet //
    public Depot(Dechet dechet, float quantite, String idPoubelle) {
        if (dechet == null) {
            throw new IllegalArgumentException("Le déchet ne peut pas être null.");
        }
        this.dechet = dechet;
        this.quantite = quantite;
        this.idPoubelle = idPoubelle;
        this.dateDepot = new Date();
        this.pointsGagnes = calculerValeurDepot();
    }

    // Affiche les infos liées au depot //
    public String afficherDescription() {
        return "Dépôt #" + idDepot + " : " + dechet.getNom() + " (" + getContenu() + "), " + quantite + " kg";
    }

    // Calcule la valeur en points du depot //
    public float calculerValeurDepot() {
        float bonus = switch (getContenu()) {
            case PLASTIQUE -> 0.5f;
            case VERRE -> 0.2f;
            case PAPIER -> 0.3f;
            case METAUX -> 0.8f;
            case BIODECHET -> 0.1f;
            default -> 0f;
        };
        return quantite * (2 + bonus);
    }

    // getters et setters //
    public Contenu getContenu() {
        return dechet.getContenu();
    }

    public int getIdDepot() { return idDepot; }
    public void setIdDepot(int idDepot) { this.idDepot = idDepot; }

    public float getQuantite() { return quantite; }
    public void setQuantite(float quantite) { this.quantite = quantite; }

    public Date getDateDepot() { return dateDepot; }
    public void setDateDepot(Date dateDepot) { this.dateDepot = dateDepot; }

    public float getPointsGagnes() { return pointsGagnes; }
    public void setPointsGagnes(float pointsGagnes) { this.pointsGagnes = pointsGagnes; }

    public String getIdPoubelle() { return idPoubelle; }
    public void setIdPoubelle(String idPoubelle) { this.idPoubelle = idPoubelle; }

    public Dechet getDechet() { return dechet; }
    public void setDechet(Dechet dechet) { this.dechet = dechet; }
}
