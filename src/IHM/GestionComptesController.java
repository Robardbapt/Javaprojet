package IHM;

import DAO.CentreDeTriDAO;
import DAO.CompteDAO;
import classe.Compte;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

public class GestionComptesController {

    @FXML private TableView<Compte> tableComptes;
    @FXML private TableColumn<Compte, Integer> colId;
    @FXML private TableColumn<Compte, String> colNom;
    @FXML private TableColumn<Compte, String> colEmail;
    @FXML private TableColumn<Compte, String> colAdresse;
    @FXML private TableColumn<Compte, String> colType;

    private final CompteDAO compteDAO = new CompteDAO();
    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();

    private Compte compteAdmin;

    public void setCompteAdmin(Compte admin) {
        this.compteAdmin = admin;
        chargerComptes();
    }

    private void chargerComptes() {
        int idCentre = centreDAO.getIdCentreByAdmin(compteAdmin.getIdCompte());
        ObservableList<Compte> comptes = FXCollections.observableArrayList(
            compteDAO.getByCentreId(idCentre)
        );

        colId.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getIdCompte()));
        colNom.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getNom()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getEmail()));
        colAdresse.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getAdresse()));
        colType.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getTypeUser()));

        tableComptes.setItems(comptes);
    }

    @FXML
    private void handleSupprimer() {
        Compte c = tableComptes.getSelectionModel().getSelectedItem();
        if (c != null) {
            compteDAO.delete(c.getIdCompte());
            tableComptes.getItems().remove(c);
        } else {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un compte à supprimer.").showAndWait();
        }
    }

    @FXML
    private void handleModifier() {
        Compte c = tableComptes.getSelectionModel().getSelectedItem();
        if (c == null) {
            new Alert(Alert.AlertType.WARNING, "Aucun compte sélectionné.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/ModifierCompte.fxml"));
            Parent root = loader.load();

            ModifierCompteController controller = loader.getController();
            controller.setCompte(c);

            Stage stage = new Stage();
            stage.setTitle("Modifier un compte");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerComptes();
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
            controller.setCompte(compteAdmin);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion du centre de tri");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/CreerCompte.fxml"));
            Parent root = loader.load();

            CreerCompteController controller = loader.getController();
            controller.setCompteAdmin(this.compteAdmin); // correction ici

            Stage stage = new Stage();
            stage.setTitle("Créer un compte");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerComptes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleTransfererCentre() {
        Compte selected = tableComptes.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getTypeUser().equals("admin")) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur non-admin.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/TransfererCentre.fxml"));
            Parent root = loader.load();

            TransfererCentreController controller = loader.getController();
            controller.initialiser(selected);

            Stage stage = new Stage();
            stage.setTitle("Transférer le compte vers un autre centre");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerComptes();  // Refresh
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
