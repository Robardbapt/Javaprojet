package IHM;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Contrôleur pour la création d'un compte utilisateur.
 */
public class RegisterController {

    @FXML
    private TextField usernameField;  // utilisé ici comme email

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    /**
     * Création d'un compte si l'email n'existe pas encore.
     */
    @FXML
    private void handleRegister() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DataBaseConnection.getConnection()) {
            // Vérifie si un compte avec cet email existe déjà
            PreparedStatement check = conn.prepareStatement("SELECT * FROM Compte WHERE email = ?");
            check.setString(1, email);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                messageLabel.setText("Un compte avec cet email existe déjà.");
            } else {
                // Création du compte : id auto-incrémenté (ou géré autrement), nom = email, adresse par défaut
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Compte (nom, email, motDePasse, pointFidelite, adresse) VALUES (?, ?, ?, 0, ?)"
                );
                insert.setString(1, email);      // nom = email par défaut
                insert.setString(2, email);      // email
                insert.setString(3, password);   // mot de passe
                insert.setString(4, "adresse inconnue"); // valeur temporaire

                insert.executeUpdate();
                messageLabel.setText("Compte créé avec succès !");
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la création du compte.");
            e.printStackTrace();
        }
    }

    /**
     * Retour à l'écran de connexion.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
