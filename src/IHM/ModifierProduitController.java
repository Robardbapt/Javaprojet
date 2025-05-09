package IHM;

import DAO.ProduitDAO;
import classe.CategorieProduit;
import classe.Produit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ModifierProduitController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldPrix;
    @FXML private DatePicker fieldDate;

    private Produit produit;
    private Stage stage;
    
    private CategorieProduit categorie;

    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        fieldNom.setText(produit.getNom());
        fieldPrix.setText(String.valueOf(produit.getPrix()));

        // ✅ Conversion propre si dateAchat est java.sql.Date
        if (produit.getDateAchat() != null) {
            try {
                fieldDate.setValue(((java.sql.Date) produit.getDateAchat()).toLocalDate());
            } catch (ClassCastException e) {
                // fallback si ce n'est pas un java.sql.Date
                LocalDate date = produit.getDateAchat().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                fieldDate.setValue(date);
            }
        }
    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleEnregistrer() {
        String nom = fieldNom.getText();
        String prixStr = fieldPrix.getText();
        LocalDate date = fieldDate.getValue();

        if (nom.isEmpty() || prixStr.isEmpty() || date == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        float prix;
        try {
            prix = Float.parseFloat(prixStr);
        } catch (NumberFormatException e) {
            showAlert("Le prix est invalide.");
            return;
        }

        produit.setNom(nom);
        produit.setPrix(prix);
        produit.setDateAchat(java.sql.Date.valueOf(date)); // ✅ conversion inverse

        new ProduitDAO().update(produit);
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
