package IHM;

import DAO.CompteDAO;
import classe.Compte;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierCompteController {

    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<String> typeBox;

    private Compte compte;
    private final CompteDAO compteDAO = new CompteDAO();

    public void setCompte(Compte compte) {
        this.compte = compte;

        // Pré-remplissage des champs
        nomField.setText(compte.getNom());
        emailField.setText(compte.getEmail());
        adresseField.setText(compte.getAdresse());

        typeBox.getItems().addAll("user", "admin");
        typeBox.setValue(compte.getTypeUser());
    }

    @FXML
    private void handleSave() {
        if (compte == null) return;

        // Mise à jour des champs
        compte.setNom(nomField.getText());
        compte.setEmail(emailField.getText());
        compte.setAdresse(adresseField.getText());
        compte.setTypeUser(typeBox.getValue());

        compteDAO.update(compte);

        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close(); // ferme la fenêtre de modification
    }
}
