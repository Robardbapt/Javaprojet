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

/**
 * Contrôleur pour la page de connexion.
 */
public class LoginController {

    @FXML
    private TextField usernameField;  // email

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    /**
     * Gère la tentative de connexion de l'utilisateur.
     */
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

                if ("admin".equalsIgnoreCase(compte.getTypeUser())) {
                    loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/AdminDashboard.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/UserDashboard.fxml"));
                }

                Parent root = loader.load();

                if ("admin".equalsIgnoreCase(compte.getTypeUser())) {
                    AdminDashboardController controller = loader.getController();
                    controller.setCompte(compte);
                    stage.setTitle("Tableau de bord - Admin");
                } else {
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

    /**
     * Redirige vers la page d'inscription.
     */
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
