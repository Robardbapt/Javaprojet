package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un Commerce est une classe qui représente l'entité qui gère les categoriesproduits et établit des contrats avec les CentreDeTri
**/

public class Commerce {

    private int idCommerce;
    private String nom;
    private String adresse;
    private List<String> categoriesProduits;
    private List<Contrat> contrats;

    // Constructeur de base //
    public Commerce() {
        this.categoriesProduits = new ArrayList<>();
        this.contrats = new ArrayList<>();
    }

    // Constructeur Complet //
    public Commerce(int idCommerce, String nom, String adresse) {
        this.idCommerce = idCommerce;
        this.nom = nom;
        this.adresse = adresse;
        this.categoriesProduits = new ArrayList<>();
        this.contrats = new ArrayList<>();
    }

    // Nouveau constructeur sans ID pour création initiale //
    public Commerce(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
        this.categoriesProduits = new ArrayList<>();
        this.contrats = new ArrayList<>();
    }

    // Supprime le dernier contrat //
    public boolean supprimerContrat() {
        if (!contrats.isEmpty()) {
            contrats.remove(contrats.size() - 1);
            return true;
        }
        return false;
    }

    // Vérifie s'il existe un contrat lié au commerce //
    public boolean estEnContrat() {
        for (Contrat contrat : contrats) {
            if (contrat.estActif()) {
                return true;
            }
        }
        return false;
    }

    // Applique le taux de réduction max des contrats mis en place //
    public float appliquerReduction(int points) {
        float tauxMax = 0.0f;
        for (Contrat contrat : contrats) {
            if (contrat.estActif()) {
                for (CategorieProduit categorie : contrat.getCategoriesConcernees()) {
                    if (categorie.verifierReduction(points)) {
                        tauxMax = Math.max(tauxMax, categorie.getTauxReduction());
                    }
                }
            }
        }
        return tauxMax;
    }
    
    // getters et setters //
    public int getIdCommerce() {
        return idCommerce;
    }

    public void setIdCommerce(int idCommerce) {
        this.idCommerce = idCommerce;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public List<String> getCategoriesProduits() {
        return categoriesProduits;
    }

    public void setCategoriesProduits(List<String> categoriesProduits) {
        this.categoriesProduits = categoriesProduits;
    }

    public List<Contrat> getContrats() {
        return contrats;
    }

    public void setContrats(List<Contrat> contrats) {
        this.contrats = contrats;
    }
    
    // Ajoute un contrat //
    public void ajouterContrat(Contrat contrat) {
        this.contrats.add(contrat);
    }

    // Ajoute une CategorieProduit //
    public void ajouterCategorieProduit(String categorie) {
        this.categoriesProduits.add(categorie);
    }
}
