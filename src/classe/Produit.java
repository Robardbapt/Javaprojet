package classe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un Produit est une classe qui représente un bien particulier d'une categorieProduit 
**/

public class Produit {

    private int idProduit;
    private String nom;
    private float prix;
    private List<CategorieProduit> categorieProduit;
    private Date dateAchat;

    // Constructeur de base //
    public Produit() {
        this.categorieProduit = new ArrayList<>();
        this.dateAchat = new Date(System.currentTimeMillis()); 
    }

    // Constructeur complet //
    public Produit(int idProduit, String nom, float prix, Date dateAchat) {
        this.idProduit = idProduit;
        this.nom = nom;
        this.prix = prix;
        this.dateAchat = dateAchat;
        this.categorieProduit = new ArrayList<>();
    }

    // Affiche les infos liées au produit //
    public String afficherDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Produit : ").append(nom)
          .append("\nPrix : ").append(prix).append(" €")
          .append("\nDate d'achat : ").append(dateAchat);

        if (categorieProduit.isEmpty()) {
            sb.append("\nCatégories : Aucune");
        } else {
            sb.append("\nCatégories : ");
            for (CategorieProduit cat : categorieProduit) {
                sb.append(cat.getNom()).append(" ");
            }
        }

        return sb.toString();
    }

    // Getters et setters //
    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public List<CategorieProduit> getCategorieProduit() {
        return categorieProduit;
    }

    public void setCategorieProduit(List<CategorieProduit> categorieProduit) {
        this.categorieProduit = categorieProduit;
    }

    // Ajoute une categorie au produit //
    public void ajouterCategorie(CategorieProduit categorie) {
        this.categorieProduit.add(categorie);
    }

    public Date getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(Date dateAchat) {
        this.dateAchat = dateAchat;
    }
}
