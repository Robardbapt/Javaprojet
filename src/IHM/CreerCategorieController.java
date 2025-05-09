package IHM;

import DAO.CategorieProduitDAO;
import DAO.CommerceDAO;
import classe.CategorieProduit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreerCategorieController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldTaux;
    @FXML private TextField fieldPoints;

    private int idCommerce;
    private Stage stage;

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

        CategorieProduitDAO dao = new CategorieProduitDAO();
        CommerceDAO commerceDAO = new CommerceDAO();

        CategorieProduit cp = new CategorieProduit();
        cp.setNom(nom);
        cp.setTauxReduction(taux);
        cp.setPointNecessaire(points);

        dao.insert(cp);
        commerceDAO.ajouterCategorieAuCommerce(idCommerce, nom);

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
