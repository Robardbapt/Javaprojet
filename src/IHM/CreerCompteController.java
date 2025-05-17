package IHM;

import DAO.CentreDeTriDAO;
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
    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();

    private Compte compteAdmin;

    @FXML
    private void initialize() {
        typeBox.getItems().addAll("user", "admin");
    }

    public void setCompteAdmin(Compte admin) {
        this.compteAdmin = admin;
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

        try {
            Compte c = new Compte();
            c.setNom(nom);
            c.setEmail(email);
            c.setMotDePasse(mdp);
            c.setAdresse(adresse);
            c.setTypeUser(type);
            c.setPointFidelite(0);

            int generatedId = compteDAO.insertAndReturnId(c);
            c.setIdCompte(generatedId);

            if (type.equals("user")) {
                int idCentre = centreDAO.getIdCentreByAdmin(compteAdmin.getIdCompte());
                compteDAO.lierComptePoubelle(generatedId, idCentre);
            }

            new Alert(Alert.AlertType.INFORMATION, "Compte créé avec succès.").showAndWait();
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la création du compte.").showAndWait();
        }
    }
}
