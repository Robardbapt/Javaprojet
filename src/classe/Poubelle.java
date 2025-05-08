package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Une Poubelle est une classe qui représente les poubelles connectées gérées par le CentreDeTri
**/
public class Poubelle {

    private int idPoubelle;
    private String nom;
    private float capaciteMax;
    private float capaciteActuelle;
    private boolean estPleine;
    private String adresse;
    private TypePoubelle typePoubelle;
    private List<Depot> historiqueDepots;

    // Constructeur de base //
    public Poubelle() {
        this.historiqueDepots = new ArrayList<>();
    }

    // Constructeur complet //
    public Poubelle(int idPoubelle, String nom, TypePoubelle type) {
        this.idPoubelle = idPoubelle;
        this.nom = nom;
        this.typePoubelle = type;
        this.capaciteActuelle = 0f;
        this.capaciteMax = 100f;
        this.estPleine = false;
        this.historiqueDepots = new ArrayList<>();
    }

    // Vérifie si un utilisateur à accès à la poubelle //
    public boolean identifierUtilisateur(Compte utilisateur) {
        return utilisateur != null && utilisateur.getAdresse().equals(this.adresse);
    }

    // Change les infos après un dépôt //
    public float gererQuantiteDechet(Depot depot) {
        if (verifierTypeDechet(depot.getDechet())) {
            float qte = depot.getQuantite();
            if (capaciteActuelle + qte <= capaciteMax) {
                capaciteActuelle += qte;
                historiqueDepots.add(depot);
                verifierPleine();
                return capaciteMax - capaciteActuelle;
            }
        }
        return -1f;
    }

    // Vérifie la compatibilité entre contenu et typePoubelle //
    public boolean verifierTypeDechet(Dechet dechet) {
        return dechet != null && dechet.getContenu().name().equals(this.typePoubelle.name());
    }

    // Attribue des points liés au dernier dépôt //
    public int attribuerPoint() {
        if (historiqueDepots.isEmpty()) return 0;
        Depot dernier = historiqueDepots.get(historiqueDepots.size() - 1);
        return (int) (dernier.getQuantite() * 2);
    }

    // Notification au CentreDeTri si poubelle pleine //
    public void notifierCentreTri() {
        if (estPleine) {
            System.out.println("Notification envoyée au centre de tri : poubelle pleine !");
        }
    }

    // Vérifie si la poubelle est pleine //
    public boolean verifierPleine() {
        this.estPleine = capaciteActuelle >= capaciteMax;
        return estPleine;
    }

    // Vide l'intégralité de la poubelle //
    public void vider() {
        capaciteActuelle = 0;
        estPleine = false;
        System.out.println("Poubelle vidée.");
    }

    // Accepte l'accès //
    public boolean verifierAcces() {
        return true;
    }

    // getters et setters //
    public int getIdPoubelle() {
        return idPoubelle;
    }

    public void setIdPoubelle(int idPoubelle) {
        this.idPoubelle = idPoubelle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(float capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public float getCapaciteActuelle() {
        return capaciteActuelle;
    }

    public void setCapaciteActuelle(float capaciteActuelle) {
        this.capaciteActuelle = capaciteActuelle;
    }

    public boolean isEstPleine() {
        return estPleine;
    }

    public void setEstPleine(boolean estPleine) {
        this.estPleine = estPleine;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public TypePoubelle getType() {
        return typePoubelle;
    }

    public void setType(TypePoubelle typePoubelle) {
        this.typePoubelle = typePoubelle;
    }

    public List<Depot> getHistoriqueDepots() {
        return historiqueDepots;
    }

    public void setHistoriqueDepots(List<Depot> historiqueDepots) {
        this.historiqueDepots = historiqueDepots;
    }
}
