package IHM;

import classe.*;
import DAO.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.collections.*;

public class GestionPoubellesController {

    @FXML private TableView<Poubelle> tablePoubelles;
    @FXML private TableColumn<Poubelle, Integer> colId;
    @FXML private TableColumn<Poubelle, String> colNom;
    @FXML private TableColumn<Poubelle, String> colAdresse;
    @FXML private TableColumn<Poubelle, String> colType;
    @FXML private TableColumn<Poubelle, Float> colCapaciteMax;
    @FXML private TableColumn<Poubelle, Float> colCapaciteActuelle;
    @FXML private TableColumn<Poubelle, Boolean> colPleine;

    private final PoubelleDAO poubelleDAO = new PoubelleDAO();
    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();

    private Compte compteAdmin;

    public void setCompteAdmin(Compte admin) {
        this.compteAdmin = admin;
        chargerPoubelles();
    }

    private void chargerPoubelles() {
        int idCentre = centreDAO.getIdCentreByAdmin(compteAdmin.getIdCompte());
        ObservableList<Poubelle> liste = FXCollections.observableArrayList(
            poubelleDAO.getByCentreId(idCentre)
        );

        colId.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getIdPoubelle()));
        colNom.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getNom()));
        colAdresse.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getAdresse()));
        colType.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getType().name()));
        colCapaciteMax.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getCapaciteMax()));
        colCapaciteActuelle.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getCapaciteActuelle()));
        colPleine.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().isEstPleine()));

        tablePoubelles.setItems(liste);
    }

    @FXML
    private void handleSupprimer() {
        Poubelle p = tablePoubelles.getSelectionModel().getSelectedItem();
        if (p != null) {
            poubelleDAO.delete(p.getIdPoubelle());
            tablePoubelles.getItems().remove(p);
        } else {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une poubelle à supprimer.").showAndWait();
        }
    }

    @FXML
    private void handleModifier() {
        Poubelle p = tablePoubelles.getSelectionModel().getSelectedItem();
        if (p == null) {
            new Alert(Alert.AlertType.WARNING, "Aucune poubelle sélectionnée.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/ModifierPoubelle.fxml"));
            Parent root = loader.load();

            ModifierPoubelleController controller = loader.getController();
            controller.setPoubelle(p);

            Stage stage = new Stage();
            stage.setTitle("Modifier une poubelle");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // attend fermeture

            chargerPoubelles(); // recharge la table

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/GestionCentre.fxml"));
            Parent root = loader.load();

            GestionCentreController controller = loader.getController();
            controller.setCompte(compteAdmin); // on repasse le compte admin

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion du centre de tri");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleViderPoubelle() {
        Poubelle selected = tablePoubelles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une poubelle à vider.").showAndWait();
            return;
        }

        selected.setCapaciteActuelle(0f);
        selected.setEstPleine(false);
        poubelleDAO.update(selected);

        new Alert(Alert.AlertType.INFORMATION, "La poubelle a été vidée avec succès.").showAndWait();
        chargerPoubelles(); // Recharge la table pour mettre à jour l'affichage
    }
    @FXML
    private void handleCreerPoubelle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/CreerPoubelle.fxml"));
            Parent root = loader.load();

            CreerPoubelleController controller = loader.getController();
            controller.setAdmin(compteAdmin);

            Stage stage = new Stage();
            stage.setTitle("Créer une poubelle");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerPoubelles(); // rafraîchit la liste après création

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
