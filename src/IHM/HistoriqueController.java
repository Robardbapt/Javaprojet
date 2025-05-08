package IHM;

import classe.Compte;
import classe.Depot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class HistoriqueController {

    @FXML private TableView<Depot> tableHistorique;
    @FXML private TableColumn<Depot, String> colDate;
    @FXML private TableColumn<Depot, String> colDechet;
    @FXML private TableColumn<Depot, Float> colQuantite;
    @FXML private TableColumn<Depot, Float> colPoints;

    private Compte compte;

    public void setCompte(Compte compte) {
        this.compte = compte;
        afficherDepots();
    }

    private void afficherDepots() {
        ObservableList<Depot> depots = FXCollections.observableArrayList(
            compte.getHistorique().getDepots()
        );

        colDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getDateDepot().toString()
        ));
        colDechet.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getDechet().getNom()
        ));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("pointsGagnes"));

        tableHistorique.setItems(depots);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setCompte(compte);  // on renvoie le même compte

            Stage stage = (Stage) tableHistorique.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
