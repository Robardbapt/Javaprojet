package classe;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un Dechet est une classe qui représente un contenu qui va être déposé dans une poubelle  
**/

public class Dechet {

    private String nom;
    private Contenu contenu; 
    private float masse;     

    // Constructeur de base //
    public Dechet() {}

    // Constructeur semi-complet //
    public Dechet(String nom, Contenu contenu) {
        this.nom = nom;
        this.contenu = contenu;
        this.masse = 1f;
    }

    // Constructeur complet //
    public Dechet(String nom, Contenu contenu, float masse) {
        this.nom = nom;
        this.contenu = contenu;
        this.masse = masse;
    }

    // getters et setters //
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Contenu getContenu() {
        return contenu;
    }

    public void setContenu(Contenu contenu) {
        this.contenu = contenu;
    }

    public float getMasse() {
        return masse;
    }

    public void setMasse(float masse) {
        this.masse = masse;
    }


    // Affiche l'ensemble des infos liées au déchet //
    public String afficherDescription() {
        return nom + " (" + contenu + "), " + masse + " kg";
    }
}
