package IHM;

import classe.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Contrôleur du tableau de bord utilisateur.
 */
public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label pointsLabel;

    private String emailUtilisateur;

    /**
     * Définit l'email de l'utilisateur et charge les infos de la BDD.
     * @param email email de l'utilisateur connecté
     */
    public void setNomUtilisateur(String email) {
        this.emailUtilisateur = email;
        welcomeLabel.setText("Bienvenue " + email);

        try (Connection conn = DataBaseConnection.getConnection()) {
            String sql = "SELECT pointFidelite FROM Compte WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, emailUtilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int points = rs.getInt("pointFidelite");
                pointsLabel.setText("Points de fidélité : " + points);
            } else {
                pointsLabel.setText("Compte introuvable.");
            }
        } catch (Exception e) {
            pointsLabel.setText("Erreur lors du chargement des points.");
            e.printStackTrace();
        }
    }
    
    private Compte compte;

    public void setCompte(Compte compte) {
        this.compte = compte;
        welcomeLabel.setText("Bienvenue " + compte.getNom());
        pointsLabel.setText("Points de fidélité : " + compte.getPointFidelite());
    }

    /**
     * Handler pour afficher l'historique (à implémenter).
     */
    @FXML
    private void handleVoirHistorique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Historique.fxml"));
            Parent root = loader.load();

            HistoriqueController controller = loader.getController();
            controller.setCompte(compte);  // on transmet le compte entier

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Historique des dépôts");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Retourne à la page de connexion.
     */
    @FXML
    private void handleDeconnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connexion Utilisateur");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
