package IHM;

import DAO.ProduitDAO;
import classe.CategorieProduit;
import classe.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GestionProduitsController {

    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Float> colPrix;

    private CategorieProduit categorie;
    private final ProduitDAO produitDAO = new ProduitDAO();
    private Stage stage;
    private int idCentre;

    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
        chargerProduits();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setIdCentre(int idCentre) {
        this.idCentre = idCentre;
    }

    private void chargerProduits() {
        List<Produit> produits = produitDAO.getByCategorieId(categorie.getIdCategorie());
        ObservableList<Produit> liste = FXCollections.observableArrayList(produits);

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdProduit()).asObject());
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colPrix.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getPrix()).asObject());

        tableProduits.setItems(liste);
    }

    @FXML
    private void handleRetour() {
        stage.close();
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AjoutProduit.fxml"));
            Parent root = loader.load();

            AjoutProduitController controller = loader.getController();
            controller.setCategorie(categorie);
            controller.setStage(new Stage());

            Stage ajoutStage = controller.getStage();
            controller.setStage(ajoutStage);
            controller.setIdCentre(idCentre);

            ajoutStage.setScene(new Scene(root));
            ajoutStage.setTitle("Ajouter un produit");
            ajoutStage.initOwner(tableProduits.getScene().getWindow());
            ajoutStage.showAndWait();

            chargerProduits();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer() {
        Produit selected = tableProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un produit à supprimer.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce produit ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            produitDAO.delete(selected.getIdProduit());
            chargerProduits();
        }
    }

    @FXML
    private void handleModifier() {
        Produit selected = tableProduits.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un produit.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ModifierProduit.fxml"));
            Parent root = loader.load();

            ModifierProduitController controller = loader.getController();
            controller.setProduit(selected);
            controller.setCategorie(categorie);
            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le produit : " + selected.getNom());
            stage.initOwner(tableProduits.getScene().getWindow());
            stage.showAndWait();

            chargerProduits();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
