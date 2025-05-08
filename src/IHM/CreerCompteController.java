package IHM;

import DAO.CompteDAO;
import classe.Compte;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreerCompteController {

    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<String> typeBox;

    private final CompteDAO compteDAO = new CompteDAO();

    @FXML
    private void initialize() {
        typeBox.getItems().addAll("user", "admin");
    }

    @FXML
    private void handleCreate() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String mdp = passwordField.getText();
        String adresse = adresseField.getText();
        String type = typeBox.getValue();

        if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty() || adresse.isEmpty() || type == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.").showAndWait();
            return;
        }

        Compte c = new Compte();
        c.setNom(nom);
        c.setEmail(email);
        c.setMotDePasse(mdp);
        c.setAdresse(adresse);
        c.setTypeUser(type);

        // ID auto-assigné par la base ou à calculer si nécessaire
        c.setIdCompte((int) (Math.random() * 100000)); // temporaire
        compteDAO.insert(c);

        new Alert(Alert.AlertType.INFORMATION, "Compte créé avec succès.").showAndWait();

        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
