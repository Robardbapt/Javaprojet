package IHM;

import DAO.CategorieProduitDAO;
import DAO.CommerceDAO;
import classe.CategorieProduit;
import classe.Commerce;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreerCategorieController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldTaux;
    @FXML private TextField fieldPoints;

    private int idCommerce;
    private Stage stage;
    private int idCentre;
    
    public void setIdCentre(int idCentre) {
        this.idCentre = idCentre;
    }

    public void setIdCommerce(int idCommerce) {
        this.idCommerce = idCommerce;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCreer() {
        String nom = fieldNom.getText();
        int points;
        float taux;

        try {
            taux = Float.parseFloat(fieldTaux.getText());
            points = Integer.parseInt(fieldPoints.getText());
        } catch (NumberFormatException e) {
            showAlert("Taux ou points invalide(s)");
            return;
        }

        if (nom.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        // ⚠️ Vérifie si le nom est déjà utilisé dans ce commerce
        Commerce commerce = new CommerceDAO().getById(idCommerce);
        for (String existing : commerce.getCategoriesProduits()) {
            if (existing.equalsIgnoreCase(nom)) {
                showAlert("Ce commerce possède déjà une catégorie nommée \"" + nom + "\".\nVeuillez choisir un autre nom.");
                return;
            }
        }
        
        
        CategorieProduitDAO dao = new CategorieProduitDAO();
        CommerceDAO commerceDAO = new CommerceDAO();

        CategorieProduit cp = new CategorieProduit();
        cp.setNom(nom);
        cp.setTauxReduction(taux);
        cp.setPointNecessaire(points);

        dao.insert(cp, idCentre); // insertion dans categorieproduit
        commerceDAO.ajouterCategorieAuCommerce(idCommerce, nom); // liaison

        stage.close();
    }


    @FXML
    private void handleAnnuler() {
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
