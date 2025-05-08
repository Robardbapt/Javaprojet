package IHM;

import classe.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class AchatProduitsController {

    @FXML private TableView<BonReduction> produitTable;
    @FXML private TableColumn<BonReduction, String> colNom;
    @FXML private TableColumn<BonReduction, Integer> colPrix;
    @FXML private TableColumn<BonReduction, Void> colAction;

    private Compte compte;

    public void setCompte(Compte compte) {
        this.compte = compte;
        chargerProduits();
    }

    private void chargerProduits() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("pointsNecessaires"));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Acheter");

            {
                btn.setOnAction(event -> {
                    BonReduction bon = getTableView().getItems().get(getIndex());
                    if (compte.echangerPoints(bon)) {
                        new CompteDAO().updatePoints(compte.getIdCompte(), compte.getPointFidelite());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Achat effectué !");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Pas assez de points !");
                        alert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        List<BonReduction> liste = compte.getBonsDisponibles();
        produitTable.getItems().setAll(liste);
    }

    @FXML
    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setCompte(compte);

            Stage stage = (Stage) produitTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
