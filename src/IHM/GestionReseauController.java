package IHM;

import DAO.CentreDeTriDAO;
import classe.CentreDeTri;
import classe.Statistique;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GestionReseauController implements Initializable {

    @FXML private TableView<CentreDeTri> tableCentres;
    @FXML private TableColumn<CentreDeTri, String> colNom;
    @FXML private TableColumn<CentreDeTri, String> colAdresse;
    @FXML private TableColumn<CentreDeTri, String> colAdmin;
    @FXML private TableColumn<CentreDeTri, Integer> colNbDepots;
    @FXML private TableColumn<CentreDeTri, Float> colQuantite;

    private final ObservableList<CentreDeTri> centresList = FXCollections.observableArrayList();
    private final CentreDeTriDAO centreDeTriDAO = new CentreDeTriDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colAdresse.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAdresse()));
        colAdmin.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("Admin ID : " + data.getValue().getIdAdmin()));
        colNbDepots.setCellValueFactory(data -> {
            Statistique stat = data.getValue().getStatistique();
            return new javafx.beans.property.SimpleIntegerProperty(stat != null ? stat.getHistoriqueDepot().size() : 0).asObject();
        });
        colQuantite.setCellValueFactory(data -> {
            Statistique stat = data.getValue().getStatistique();
            return new javafx.beans.property.SimpleFloatProperty(stat != null ? stat.calculerTotalDechets() : 0f).asObject();
        });

        tableCentres.setItems(centresList);
        chargerCentres();
    }

    private void chargerCentres() {
        centresList.clear();
        centresList.addAll(centreDeTriDAO.getAll());
    }

    @FXML
    private void ajouterCentre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CreerCentre.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer un nouveau centre de tri");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> chargerCentres());
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alerte.erreur("Erreur lors de l'ouverture du formulaire de création.");
        }
    }


    @FXML
    private void supprimerCentre() {
        CentreDeTri selected = tableCentres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerte.erreur("Veuillez sélectionner un centre de tri.");
            return;
        }

        List<CentreDeTri> autresCentres = centresList.stream()
                .filter(c -> c.getIdCentreDeTri() != selected.getIdCentreDeTri())
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Supprimer les utilisateurs", "Supprimer les utilisateurs", "Transférer les utilisateurs vers un autre centre");
        dialog.setTitle("Suppression du centre de tri");
        dialog.setHeaderText("Choisissez le mode de suppression :");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        if (result.get().equals("Supprimer les utilisateurs")) {
            centreDeTriDAO.delete(selected.getIdCentreDeTri(), false, -1);
            Alerte.info("Centre supprimé avec ses utilisateurs et poubelles.");
        } else {
            ChoiceDialog<CentreDeTri> choixCentre = new ChoiceDialog<>(autresCentres.get(0), autresCentres);
            choixCentre.setTitle("Transfert des utilisateurs");
            choixCentre.setHeaderText("Sélectionnez le centre de tri de destination :");

            Optional<CentreDeTri> centreDest = choixCentre.showAndWait();
            if (centreDest.isEmpty()) return;

            centreDeTriDAO.delete(selected.getIdCentreDeTri(), true, centreDest.get().getIdCentreDeTri());
            Alerte.info("Centre supprimé. Utilisateurs et commerces transférés.");
        }

        chargerCentres();
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableCentres.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
