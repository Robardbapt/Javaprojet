package IHM;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DataBaseConnection.getConnection()) {
            String sql = "SELECT * FROM Compte WHERE username = ? AND motDePasse = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                errorLabel.setText("Connexion réussie !");
                // Ici tu peux charger une nouvelle interface
            } else {
                errorLabel.setText("Nom d'utilisateur ou mot de passe invalide.");
            }
        } catch (Exception e) {
            errorLabel.setText("Erreur de connexion.");
            e.printStackTrace();
        }
    }
}
