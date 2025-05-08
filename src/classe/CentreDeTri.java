package classe;

import java.util.*;

/**
 * Représente un centre de tri géré par un administrateur, contenant des poubelles et des contrats.
 */
public class CentreDeTri {

    private int idCentreDeTri;
    private String nom;
    private String adresse;
    private int idAdmin; // nouveau champ : ID du compte administrateur

    private List<Contrat> partenariats;
    private Statistique statistique;
    private Map<String, List<Poubelle>> poubellesPlacees;

    // Constructeur de base
    public CentreDeTri() {
        this.partenariats = new ArrayList<>();
        this.poubellesPlacees = new HashMap<>();
    }

    // Constructeur complet sans admin
    public CentreDeTri(int id, String nom, String adresse) {
        this.idCentreDeTri = id;
        this.nom = nom;
        this.adresse = adresse;
        this.partenariats = new ArrayList<>();
        this.poubellesPlacees = new HashMap<>();
    }

    // Constructeur complet avec admin
    public CentreDeTri(int id, String nom, String adresse, int idAdmin) {
        this.idCentreDeTri = id;
        this.nom = nom;
        this.adresse = adresse;
        this.idAdmin = idAdmin;
        this.partenariats = new ArrayList<>();
        this.poubellesPlacees = new HashMap<>();
    }

    // Placer une poubelle
    public void placerPoubelle(Poubelle p, String adresse) {
        poubellesPlacees.putIfAbsent(adresse, new ArrayList<>());
        poubellesPlacees.get(adresse).add(p);
        System.out.println("Poubelle placée à l'adresse : " + adresse);
    }

    // Retirer une poubelle
    public void retirerPoubelle(Poubelle p, String adresse) {
        if (poubellesPlacees.containsKey(adresse)) {
            poubellesPlacees.get(adresse).remove(p);
            System.out.println("Poubelle retirée de l'adresse : " + adresse);
            if (poubellesPlacees.get(adresse).isEmpty()) {
                poubellesPlacees.remove(adresse);
            }
        } else {
            System.out.println("Aucune poubelle trouvée à cette adresse.");
        }
    }

    // Collecte des déchets
    public void collecteDechets(Poubelle p) {
        p.vider();
        System.out.println("Déchets collectés pour la poubelle : " + p.getIdPoubelle());
    }

    // Getters & Setters
    public int getIdCentreDeTri() {
        return idCentreDeTri;
    }

    public void setIdCentreDeTri(int idCentreDeTri) {
        this.idCentreDeTri = idCentreDeTri;
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

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public List<Contrat> getPartenariats() {
        return partenariats;
    }

    public void setPartenariats(List<Contrat> partenariats) {
        this.partenariats = partenariats;
    }

    public Statistique getStatistique() {
        return statistique;
    }

    public void setStatistique(Statistique statistique) {
        this.statistique = statistique;
    }

    public Map<String, List<Poubelle>> getPoubellesPlacees() {
        return poubellesPlacees;
    }

    public void setPoubellesPlacees(Map<String, List<Poubelle>> poubellesPlacees) {
        this.poubellesPlacees = poubellesPlacees;
    }

    public void ajouterPartenariat(Contrat contrat) {
        this.partenariats.add(contrat);
    }
}
