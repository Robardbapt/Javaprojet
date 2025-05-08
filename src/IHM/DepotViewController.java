package IHM;

import classe.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DepotViewController {

    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField masseField;

    private Compte compte;
    private Poubelle poubelle;

    public void initialize() {
        typeComboBox.getItems().addAll("plastique", "verre", "carton", "métal");
    }

    public void initialiser(Compte compte, Poubelle poubelle) {
        this.compte = compte;
        this.poubelle = poubelle;
    }

    @FXML
    private void validerDepot() {
        try {
            String type = typeComboBox.getValue();
            float masse = Float.parseFloat(masseField.getText());

            Dechet dechet = new Dechet(type, masse);
            boolean success = compte.deposerDechets(poubelle, dechet);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Résultat du dépôt");

            if (success) {
                alert.setContentText("Dépôt réussi !");
            } else {
                alert.setContentText("Type incorrect pour cette poubelle.");
            }

            alert.showAndWait();
            retourDashboard(); // on revient au tableau de bord après
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur dans la saisie.");
            alert.showAndWait();
        }
    }

    @FXML
    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setCompte(compte); // recharge le compte

            Stage stage = (Stage) typeComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
