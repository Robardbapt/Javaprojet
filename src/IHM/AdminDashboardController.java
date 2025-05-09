package IHM;

import classe.Compte;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    private Compte compte;

    public void setCompte(Compte compte) {
        this.compte = compte;
        welcomeLabel.setText("Bienvenue Admin " + compte.getNom());
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleGererCentre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/GestionCentre.fxml"));
            Parent root = loader.load();

            GestionCentreController controller = loader.getController();
            controller.setCompte(compte); // passe l'admin connecté

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Centre de tri - Administration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleConsulterStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/StatistiqueView.fxml"));
            Parent root = loader.load();

            StatistiqueController controller = loader.getController();
            controller.setCompteAdmin(this.compte);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques du centre");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleGererPartenariats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/GestionPartenariats.fxml"));
            Parent root = loader.load();

            GestionPartenariatsController controller = loader.getController();
            controller.setCompteAdmin(compte); // passe l’admin pour filtrer le centre

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Partenariats");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
