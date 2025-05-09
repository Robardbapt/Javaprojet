package IHM;

import classe.Compte;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class GestionCentreController {

    private Compte compte;

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    @FXML
    private void handleGestionPoubelles(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/GestionPoubelles.fxml"));
            Parent root = loader.load();

            GestionPoubellesController controller = loader.getController();
            controller.setCompteAdmin(compte);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Poubelles");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGestionComptes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/GestionComptes.fxml"));
            Parent root = loader.load();

            GestionComptesController controller = loader.getController();
            controller.setCompteAdmin(compte);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Comptes");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/AdminDashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setCompte(compte);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord Admin");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

}
