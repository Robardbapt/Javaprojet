package IHM;

import DAO.CategorieProduitDAO;
import classe.CategorieProduit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierCategorieController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldTaux;
    @FXML private TextField fieldPoints;

    private CategorieProduit categorie;
    private Stage stage;

    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
        // Pré-remplissage des champs
        fieldNom.setText(categorie.getNom());
        fieldTaux.setText(String.valueOf(categorie.getTauxReduction()));
        fieldPoints.setText(String.valueOf(categorie.getPointNecessaire()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleModifier() {
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

        categorie.setNom(nom);
        categorie.setTauxReduction(taux);
        categorie.setPointNecessaire(points);

        CategorieProduitDAO dao = new CategorieProduitDAO();
        dao.update(categorie);

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
