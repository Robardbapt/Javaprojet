package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Une CategorieProduit est une classe qui caractérise un ensemble de produits et les place dans une catégorie
**/

public class CategorieProduit {

    private int idCategorie;
    private String nom;
    private float tauxReduction;
    private List<Produit> produits;
    private int pointNecessaire;

    // Constructeur basique //
    public CategorieProduit() {
        this.produits = new ArrayList<>();
    }

    // Constructeur complet //
    public CategorieProduit(int idCategorie, String nom, float tauxReduction, int pointNecessaire) {
        this.idCategorie = idCategorie;
        this.nom = nom;
        this.tauxReduction = tauxReduction;
        this.pointNecessaire = pointNecessaire;
        this.produits = new ArrayList<>();
    }

    // Vérifie si les points sont assez élevés pour recevoir une réduction //
    public boolean verifierReduction(int points) {
        return points >= pointNecessaire;
    }

    // Change le prix en appliquant un taux de réduction //
    public float appliquerReduction(float prix, int points) {
        if (verifierReduction(points)) {
            return prix * (1 - tauxReduction);
        }
        return prix;
    }
    
    // getters et setters //
    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getTauxReduction() {
        return tauxReduction;
    }

    public void setTauxReduction(float tauxReduction) {
        this.tauxReduction = tauxReduction;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    public int getPointNecessaire() {
        return pointNecessaire;
    }

    public void setPointNecessaire(int pointNecessaire) {
        this.pointNecessaire = pointNecessaire;
    }
    
    // Ajout de produit à la catégorie //
    public void ajouterProduit(Produit produit) {
        this.produits.add(produit);
    }
}
