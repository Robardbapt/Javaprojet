package classe;

import java.util.*;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un Compte est une classe qui représente un ménage qui fait ses dépots et utilise les réductions pour acheter d'autres produits 
**/

public class Compte {

    private int idCompte;
    private String nom;
    private String email;
    private String motDePasse;
    private float pointFidelite;
    private String adresse;

    private List<Poubelle> poubellesAutorisees;
    private List<Produit> produitsPossedes;
    private List<BonReduction> bonsDisponibles; 
    private HistoriqueDepot historique;         
    
    // Constructeur de base //
    public Compte() {
        this.poubellesAutorisees = new ArrayList<>();
        this.produitsPossedes = new ArrayList<>();
        this.bonsDisponibles = new ArrayList<>();
        this.historique = new HistoriqueDepot();
        this.pointFidelite = 0f;
    }

    // Constructeur complet //
    public Compte(int id, String nom, String email, String motDePasse, String adresse) {
        this.idCompte = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
        this.poubellesAutorisees = new ArrayList<>();
        this.produitsPossedes = new ArrayList<>();
        this.bonsDisponibles = new ArrayList<>();
        this.historique = new HistoriqueDepot();
        this.pointFidelite = 0f;
    }

    // Se connecter à un compte en particulier lié à un mail et un mdp //
    public boolean seConnecter(String email, String mdp) {
        return this.email.equals(email) && this.motDePasse.equals(mdp);
    }

    // Le ménage dépose des déchets dans une poubelle spécifique //
    public boolean deposerDechets(Poubelle poubelle, Dechet dechet) {
        if (dechet == null) return false;
        if (poubellesAutorisees.contains(poubelle) && poubelle.verifierTypeDechet(dechet)) {
            Depot depot = new Depot(dechet, dechet.getMasse(), String.valueOf(poubelle.getIdPoubelle()));
            poubelle.gererQuantiteDechet(depot);
            historique.ajouterDepot(depot);
            pointFidelite += depot.calculerValeurDepot();
            return true;
        }
        return false;
    }

    // Le ménage échange ses points contre un bon de réduction //
    public boolean echangerPoints(BonReduction bon) {
        if (bon != null && this.pointFidelite >= bon.getPointsNecessaires()) {
            this.pointFidelite -= bon.getPointsNecessaires();
            bon.utiliserBon();
            bonsDisponibles.remove(bon);
            System.out.println("Bon de réduction échangé avec succès.");
            return true;
        }
        return false;
    }

    // Le ménage achète un produit en utilisant ses points de fidélité/réduction //
    public List<String> acheterProduits(Produit p) {
        List<String> actions = new ArrayList<>();
        if (p != null) {
            produitsPossedes.add(p);
            actions.add("Produit acheté : " + p.getNom());
            actions.add("Points restants : " + pointFidelite);
        } else {
            actions.add("Produit invalide.");
        }
        return actions;
    }

    // Consulte l'ensemble des depots liés au compte //
    public HistoriqueDepot consulterHistoriqueDepots() {
        return historique;
    }

    // Ajoute une poubelle à celles autorisées //
    public void ajouterPoubelle(Poubelle p) {
        this.poubellesAutorisees.add(p);
    }

    // Ajoute un bon à ceux acquis //
    public void ajouterBon(BonReduction bon) {
        this.bonsDisponibles.add(bon);
    }

    // getters et setters //
    public int getIdCompte() { return idCompte; }
    public void setIdCompte(int idCompte) { this.idCompte = idCompte; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }

    public float getPointFidelite() { return pointFidelite; }
    public void setPointFidelite(float pointFidelite) { this.pointFidelite = pointFidelite; }

    public String getAdresse() { return adresse; }

    public List<Poubelle> getPoubellesAutorisees() { return poubellesAutorisees; }

    public List<Produit> getProduitsPossedes() { return produitsPossedes; }

    public List<BonReduction> getBonsDisponibles() { return bonsDisponibles; }

    public HistoriqueDepot getHistorique() { return historique; }
}
