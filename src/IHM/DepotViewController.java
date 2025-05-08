package IHM;

import DAO.DepotDAO;
import classe.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Date;
import java.util.Random;

public class DepotViewController {

    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField masseField;

    private Compte compte;
    private Poubelle poubelle;

    private final DepotDAO depotDAO = new DepotDAO();

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

            Contenu contenu = Contenu.valueOf(type.toUpperCase());
            Dechet dechet = new Dechet(type, contenu, masse);

            Depot depot = new Depot(dechet, masse, String.valueOf(poubelle.getIdPoubelle()));
            depot.setIdDepot(genererIdDepot()); // méthode à améliorer plus tard
            depot.setDateDepot(new Date());
            depot.setPointsGagnes(depot.calculerValeurDepot());

            boolean success = compte.deposerDechets(poubelle, dechet); // côté Java (logique métier)
            depotDAO.insert(depot); // côté base de données

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Résultat du dépôt");

            if (success) {
                alert.setContentText("Dépôt enregistré avec succès !");
            } else {
                alert.setContentText("Attention : déchet incorrect pour cette poubelle.");
            }

            alert.showAndWait();
            retourDashboard();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur dans la saisie.");
            alert.showAndWait();
        }
    }

    private int genererIdDepot() {
        return new Random().nextInt(100000); // à remplacer par une vraie logique auto-incrémentée en BDD si possible
    }

    @FXML
    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setCompte(compte);

            Stage stage = (Stage) typeComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
