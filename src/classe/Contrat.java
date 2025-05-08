package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un Contrat est une classe qui représente le partenariat passé entre un CentreDeTri et un Commerce 
**/

public class Contrat {

    private int idContrat;
    private Date dateDebut;
    private Date dateFin;
    private int tauxDeConversion; 
    private List<CategorieProduit> categoriesConcernees;

    // Constructeur de base //
    public Contrat() {
        this.categoriesConcernees = new ArrayList<>();
    }

    // Constructeur complet //
    public Contrat(int idContrat, Date dateDebut, Date dateFin, int tauxDeConversion) {
        this.idContrat = idContrat;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tauxDeConversion = tauxDeConversion;
        this.categoriesConcernees = new ArrayList<>();
    }

    // Décrit les règles liées au contrat //
    public String definirRegleUtilisation() {
        return "Ce contrat est actif du " + dateDebut + " au " + dateFin +
               " avec un taux de conversion de " + tauxDeConversion +
               "%. Il concerne " + categoriesConcernees.size() + " catégories de produits.";
    }

    // Affiche les catégories concernées //
    public void getCategoriesProduits() {
        System.out.println("Catégories concernées par le contrat :");
        for (CategorieProduit cat : categoriesConcernees) {
            System.out.println("- " + cat.getNom());
        }
    }

    // Affiche le tx de conversion //
    public void getTaux() {
        System.out.println("Taux de conversion : " + tauxDeConversion + "%");
    }

    // Vérifie si le contrat est actif aujourd'hui //
    public boolean estActif() {
        Date today = new Date();
        return today.after(dateDebut) && today.before(dateFin);
    }

    // Change la date de fin du contrat //
    public void renouvelerContrat(Date nouvelleDateFin) {
        this.dateFin = nouvelleDateFin;
    }

    // getters et setters //
    public int getIdContrat() {
        return idContrat;
    }

    public void setIdContrat(int idContrat) {
        this.idContrat = idContrat;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public int getTauxDeConversion() {
        return tauxDeConversion;
    }

    public void setTauxDeConversion(int tauxDeConversion) {
        this.tauxDeConversion = tauxDeConversion;
    }

    public List<CategorieProduit> getCategoriesConcernees() {
        return categoriesConcernees;
    }

    public void setCategoriesConcernees(List<CategorieProduit> categoriesConcernees) {
        this.categoriesConcernees = categoriesConcernees;
    }

    // Ajoute une catégorieProduit au contrat //
    public void ajouterCategorie(CategorieProduit categorie) {
        this.categoriesConcernees.add(categorie);
    }
}
