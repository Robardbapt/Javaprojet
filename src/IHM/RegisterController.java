package IHM;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.sql.*;
import java.util.*;

/**
 * Contrôleur pour la création d'un compte utilisateur.
 */
public class RegisterController {

    @FXML
    private TextField usernameField;  // utilisé ici comme email

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> centreComboBox;

    @FXML
    private Label messageLabel;

    private Map<String, Integer> nomCentreToId = new HashMap<>();

    /**
     * Initialise les centres dans la ComboBox
     */
    @FXML
    public void initialize() {
        try (Connection conn = DataBaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT idCentreDeTri, nom FROM CentreDeTri");
            ResultSet rs = stmt.executeQuery();
            List<String> noms = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("idCentreDeTri");
                String nom = rs.getString("nom");
                noms.add(nom);
                nomCentreToId.put(nom, id);
            }
            centreComboBox.setItems(FXCollections.observableArrayList(noms));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur chargement centres.");
        }
    }

    /**
     * Création d'un compte si l'email n'existe pas encore.
     */
    @FXML
    private void handleRegister() {
        String email = usernameField.getText();
        String password = passwordField.getText();
        String centreNom = centreComboBox.getValue();

        if (email.isBlank() || password.isBlank() || centreNom == null) {
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
                // Insère le compte
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Compte (nom, email, motDePasse, pointFidelite, adresse, typeUser) VALUES (?, ?, ?, 0, ?, 'user')",
                    Statement.RETURN_GENERATED_KEYS
                );
                insert.setString(1, email);
                insert.setString(2, email);
                insert.setString(3, password);
                insert.setString(4, "adresse inconnue");
                insert.executeUpdate();

                ResultSet generatedKeys = insert.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idCompte = generatedKeys.getInt(1);
                    int idCentre = nomCentreToId.get(centreNom);

                    // Récupère les poubelles du centre et les associe au compte
                    PreparedStatement getPoubelles = conn.prepareStatement("SELECT idPoubelle FROM Poubelle WHERE idCentreDeTri = ?");
                    getPoubelles.setInt(1, idCentre);
                    ResultSet rsP = getPoubelles.executeQuery();
                    while (rsP.next()) {
                        int idP = rsP.getInt("idPoubelle");
                        PreparedStatement assoc = conn.prepareStatement("INSERT INTO Compte_Poubelle (idCompte, idPoubelle) VALUES (?, ?)");
                        assoc.setInt(1, idCompte);
                        assoc.setInt(2, idP);
                        assoc.executeUpdate();
                    }
                }

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
