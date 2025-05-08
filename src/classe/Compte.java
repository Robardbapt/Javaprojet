package classe;

import java.util.*;

/**
 * Représente un compte utilisateur ou admin du système de tri.
 */
public class Compte {

    private int idCompte;
    private String nom;
    private String email;
    private String motDePasse;
    private float pointFidelite;
    private String adresse;
    private String typeUser; // "user" ou "admin"

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
        this.typeUser = "user";
    }

    // Constructeur complet //
    public Compte(int id, String nom, String email, String motDePasse, String adresse, String typeUser) {
        this.idCompte = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
        this.typeUser = typeUser;
        this.poubellesAutorisees = new ArrayList<>();
        this.produitsPossedes = new ArrayList<>();
        this.bonsDisponibles = new ArrayList<>();
        this.historique = new HistoriqueDepot();
        this.pointFidelite = 0f;
    }

    // Se connecter à un compte
    public boolean seConnecter(String email, String mdp) {
        return this.email.equals(email) && this.motDePasse.equals(mdp);
    }

    // Déposer des déchets
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

    // Échanger un bon de réduction
    public boolean echangerPoints(BonReduction bon) {
        if (bon != null && this.pointFidelite >= bon.getPointsNecessaires()) {
            this.pointFidelite -= bon.getPointsNecessaires();
            bon.utiliserBon();
            bonsDisponibles.remove(bon);
            return true;
        }
        return false;
    }

    // Acheter un produit
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

    public HistoriqueDepot consulterHistoriqueDepots() {
        return historique;
    }

    public void ajouterPoubelle(Poubelle p) {
        this.poubellesAutorisees.add(p);
    }

    public void ajouterBon(BonReduction bon) {
        this.bonsDisponibles.add(bon);
    }

    // Getters & Setters
    public int getIdCompte() { return idCompte; }
    public void setIdCompte(int idCompte) { this.idCompte = idCompte; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public float getPointFidelite() { return pointFidelite; }
    public void setPointFidelite(float pointFidelite) { this.pointFidelite = pointFidelite; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTypeUser() { return typeUser; }
    public void setTypeUser(String typeUser) { this.typeUser = typeUser; }

    public List<Poubelle> getPoubellesAutorisees() { return poubellesAutorisees; }

    public List<Produit> getProduitsPossedes() { return produitsPossedes; }

    public List<BonReduction> getBonsDisponibles() { return bonsDisponibles; }

    public HistoriqueDepot getHistorique() { return historique; }
}
