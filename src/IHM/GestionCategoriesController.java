package IHM;

import DAO.CategorieProduitDAO;
import DAO.CentreDeTriDAO;
import DAO.CommerceDAO;
import classe.CategorieProduit;
import classe.Commerce;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GestionCategoriesController {

    @FXML private TableView<CategorieProduit> tableCategories;
    @FXML private TableColumn<CategorieProduit, String> colNom;

    private final CategorieProduitDAO categorieDAO = new CategorieProduitDAO();
    private final CommerceDAO commerceDAO = new CommerceDAO();

    private Commerce commerce;
    private Stage stage;

    public void setCommerce(Commerce commerce) {
        this.commerce = commerce;
        chargerCategories();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void chargerCategories() {
        List<String> noms = commerce.getCategoriesProduits();
        List<CategorieProduit> categories = categorieDAO.getAll();

        ObservableList<CategorieProduit> associees = FXCollections.observableArrayList();
        for (CategorieProduit cat : categories) {
            if (noms.contains(cat.getNom())) {
                associees.add(cat);
            }
        }

        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        tableCategories.setItems(associees);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GestionPartenariats.fxml"));
            Parent root = loader.load();

            // On récupère le contrôleur et on transmet l'admin
            GestionPartenariatsController controller = loader.getController();
            controller.setCompteAdmin(new CommerceDAO().getCompteAdminFromCommerce(commerce.getIdCommerce()));

            // ✅ On récupère la fenêtre actuelle au lieu d'en créer une nouvelle
            Stage stage = (Stage) tableCategories.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Partenariats");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleCreerCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CreerCategorie.fxml"));
            Parent root = loader.load();

            CreerCategorieController controller = loader.getController();
            controller.setIdCommerce(commerce.getIdCommerce());
            controller.setIdCentre(new CentreDeTriDAO().getIdCentreByCommerce(commerce.getIdCommerce())); // <-- ICI

            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
            stage.setTitle("Créer une nouvelle catégorie");
            stage.initOwner(tableCategories.getScene().getWindow());
            stage.showAndWait();

            // Recharge après création
            commerce = new CommerceDAO().getById(commerce.getIdCommerce());
            chargerCategories();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void handleModifierCategorie() {
        CategorieProduit selected = tableCategories.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une catégorie à modifier.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ModifierCategorie.fxml"));
            Parent root = loader.load();

            ModifierCategorieController controller = loader.getController();
            controller.setCategorie(selected);
            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la catégorie : " + selected.getNom());
            stage.initOwner(tableCategories.getScene().getWindow());
            stage.showAndWait();

            // Rechargement après modification
            commerce = commerceDAO.getById(commerce.getIdCommerce());
            chargerCategories();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleSupprimerCategorie() {
        CategorieProduit selected = tableCategories.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une catégorie à supprimer.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer définitivement cette catégorie et tous les produits associés ?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            CategorieProduitDAO dao = new CategorieProduitDAO();
            dao.delete(selected.getIdCategorie());
            commerce = commerceDAO.getById(commerce.getIdCommerce());
            chargerCategories();
        }
    }



    

    @FXML
    private void handleGererProduits() {
        CategorieProduit selected = tableCategories.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une catégorie.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GestionProduits.fxml"));
            Parent root = loader.load();

            GestionProduitsController controller = loader.getController();
            controller.setCategorie(selected);
            int idCentre = new CentreDeTriDAO().getIdCentreByCommerce(commerce.getIdCommerce());
            controller.setIdCentre(idCentre);

            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
            stage.setTitle("Produits de la catégorie : " + selected.getNom());
            stage.initOwner(tableCategories.getScene().getWindow());
            stage.showAndWait();

            // Facultatif : recharger les catégories si besoin
            commerce = commerceDAO.getById(commerce.getIdCommerce());
            chargerCategories();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
