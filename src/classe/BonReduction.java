package classe;

/**
 * Fais partie du package classe qui représente l'ensemble des classes fonctionnelles 
 * Un BonReduction est une classe qui représente un bon échangeable contre des réductions pour l'achat d'un futur produit
 */

public class BonReduction {

    private static int compteur = 1;

    private int idBon;
    private String description;
    private float tauxReduction;
    private int pointsNecessaires;
    private boolean estUtilise;
    private CategorieProduit categorieLiee; 

    // Constructeur basique de la classe //
    public BonReduction(String description, int pointsNecessaires) {
        this.idBon = compteur++;
        this.description = description;
        this.pointsNecessaires = pointsNecessaires;
        this.tauxReduction = 0.10f;
        this.estUtilise = false;
    }
    
    // Constructeur complet de la classe //
    public BonReduction(String description, int pointsNecessaires, float tauxReduction, CategorieProduit categorieLiee) {
        this.idBon = compteur++;
        this.description = description;
        this.pointsNecessaires = pointsNecessaires;
        this.tauxReduction = tauxReduction;
        this.categorieLiee = categorieLiee;
        this.estUtilise = false;
    }

    // Méthode qui utilise un bon et change donc son attribut "est.utilise" //
    public void utiliserBon() {
        this.estUtilise = true;
    }


    // getters et setters //
    public int getIdBon() { return idBon; }
    public void setIdBon(int idBon) {
        this.idBon = idBon;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getTauxReduction() { return tauxReduction; }
    public void setTauxReduction(float tauxReduction) { this.tauxReduction = tauxReduction; }

    public int getPointsNecessaires() { return pointsNecessaires; }
    public void setPointsNecessaires(int pointsNecessaires) { this.pointsNecessaires = pointsNecessaires; }

    public boolean isEstUtilise() { return estUtilise; }
    
    public CategorieProduit getCategorieLiee() { return categorieLiee; }
    public void setCategorieLiee(CategorieProduit categorieLiee) { this.categorieLiee = categorieLiee; }
}
