package IHM;

import DAO.CentreDeTriDAO;
import DAO.ProduitDAO;
import classe.CategorieProduit;
import classe.Produit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class AjoutProduitController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldPrix;
    @FXML private DatePicker fieldDate;

    private CategorieProduit categorie;
    private Stage stage;
    private int idCentre;

    public void setCategorie(CategorieProduit cat) {
        this.categorie = cat;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setIdCentre(int idCentre) {
        this.idCentre = idCentre;
    }

    @FXML
    private void handleAjouter() {
        String nom = fieldNom.getText();
        float prix;
        LocalDate date = fieldDate.getValue();

        try {
            prix = Float.parseFloat(fieldPrix.getText());
        } catch (NumberFormatException e) {
            showAlert("Prix invalide");
            return;
        }

        if (nom.isEmpty() || date == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        Produit p = new Produit();
        p.setNom(nom);
        p.setPrix(prix);
        p.setDateAchat(Date.valueOf(date));
        p.ajouterCategorie(categorie);

        ProduitDAO dao = new ProduitDAO();
        dao.insertEtLierAComptes(p, idCentre);
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
