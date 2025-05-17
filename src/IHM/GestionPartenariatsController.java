package IHM;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import DAO.CentreDeTriDAO;
import DAO.CommerceDAO;
import classe.Compte;
import classe.Commerce;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class GestionPartenariatsController {

    @FXML private TableView<Commerce> tableCommerces;
    @FXML private TableColumn<Commerce, Integer> colId;
    @FXML private TableColumn<Commerce, String> colNom;
    @FXML private TableColumn<Commerce, String> colAdresse;
    @FXML private TableColumn<Commerce, String> colSecteur;

    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();
    private final CommerceDAO commerceDAO = new CommerceDAO();

    private Compte compteAdmin;

    public void setCompteAdmin(Compte admin) {
        this.compteAdmin = admin;
        chargerCommerces();
    }

    private void chargerCommerces() {
        int idCentre = centreDAO.getIdCentreByAdmin(compteAdmin.getIdCompte());
        List<Commerce> commerces = commerceDAO.getByCentre(idCentre);
        ObservableList<Commerce> liste = FXCollections.observableArrayList(commerces);

        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getIdCommerce()));
        colNom.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNom()));
        colAdresse.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAdresse()));

        tableCommerces.setItems(liste);
    }

    @FXML
    private void handleVoirContrat() {
        Commerce selected = tableCommerces.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un commerce.");
            alert.showAndWait();
            return;
        }

        var contrat = commerceDAO.getContratByCommerceId(selected.getIdCommerce());
        if (contrat == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aucun contrat associé.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ContratDetail.fxml"));
            Parent root = loader.load();

            ContratDetailController ctrl = loader.getController();
            Stage stage = new Stage();
            ctrl.setStage(stage);
            ctrl.afficherContrat(contrat);

            stage.setScene(new Scene(root));
            stage.setTitle("Détails du contrat");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterCommerce() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AjoutCommerce.fxml"));
            Parent root = loader.load();

            AjoutCommerceController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setIdCentre(centreDAO.getIdCentreByAdmin(compteAdmin.getIdCompte()));

            stage.setScene(new Scene(root));
            stage.setTitle("Ajout d'un commerce");
            stage.showAndWait();

            chargerCommerces();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifierCommerce() {
        Commerce selected = tableCommerces.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un commerce.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ModifierCommerce.fxml"));
            Parent root = loader.load();

            ModifierCommerceController controller = loader.getController();
            controller.setCommerce(selected);
            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Commerce");
            stage.showAndWait();

            chargerCommerces();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleSupprimerCommerce() {
        Commerce selected = tableCommerces.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un commerce.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce commerce ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            new CommerceDAO().delete(selected.getIdCommerce());
            chargerCommerces();
        }
    }


    @FXML
    private void handleGererCategories() {
        Commerce selected = tableCommerces.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un commerce.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GestionCategories.fxml"));
            Parent root = loader.load();

            GestionCategoriesController controller = loader.getController();
            controller.setCommerce(selected);
            controller.setStage((Stage) tableCommerces.getScene().getWindow()); // 👈 réutilise la fenêtre actuelle

            Stage stage = (Stage) tableCommerces.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Catégories liées au commerce : " + selected.getNom());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    @FXML
    private void handleRetourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AdminDashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setCompte(compteAdmin);

            Stage stage = (Stage) tableCommerces.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord administrateur");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
