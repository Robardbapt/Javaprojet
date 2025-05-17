package IHM;

import DAO.CompteDAO;
import classe.Compte;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            errorLabel.setText("Champs requis.");
            return;
        }

        try {
            CompteDAO dao = new CompteDAO();
            Compte compte = dao.findByEmailAndPassword(email, password);

            if (compte != null) {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader;
                Parent root;

                if (compte.getIdCompte() == 1) {
                    loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/GestionReseau.fxml"));
                    root = loader.load();
                    stage.setTitle("Gestion du Réseau");

                } else if ("admin".equalsIgnoreCase(compte.getTypeUser())) {
                    loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/AdminDashboard.fxml"));
                    root = loader.load();
                    AdminDashboardController controller = loader.getController();
                    controller.setCompte(compte);
                    stage.setTitle("Tableau de bord - Admin");

                } else {
                    loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/UserDashboard.fxml"));
                    root = loader.load();
                    UserDashboardController controller = loader.getController();
                    controller.setCompte(compte);
                    stage.setTitle("Tableau de bord - Utilisateur");
                }

                stage.setScene(new Scene(root));

            } else {
                errorLabel.setText("Email ou mot de passe incorrect.");
            }

        } catch (Exception e) {
            errorLabel.setText("Erreur de connexion.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Créer un compte");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
