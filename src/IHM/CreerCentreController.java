package IHM;

import DAO.CentreDeTriDAO;
import DAO.CompteDAO;
import classe.CentreDeTri;
import classe.Compte;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreerCentreController {

    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField emailField;
    @FXML private PasswordField mdpField;

    private final CompteDAO compteDAO = new CompteDAO();
    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();

    @FXML
    private void handleCreer() {
        String nom = nomField.getText();
        String adresse = adresseField.getText();
        String email = emailField.getText();
        String mdp = mdpField.getText();

        if (nom.isBlank() || adresse.isBlank() || email.isBlank() || mdp.isBlank()) {
            Alerte.erreur("Tous les champs sont requis.");
            return;
        }

        Compte admin = new Compte();
        admin.setNom("Admin " + nom);
        admin.setAdresse(adresse);
        admin.setEmail(email);
        admin.setMotDePasse(mdp);
        admin.setTypeUser("admin");

        int idCompte = compteDAO.insertAndReturnId(admin);
        if (idCompte == -1) {
            Alerte.erreur("Erreur lors de la création du compte.");
            return;
        }

        admin.setIdCompte(idCompte);
        CentreDeTri centre = new CentreDeTri();
        centre.setNom(nom);
        centre.setAdresse(adresse);
        centre.setIdAdmin(idCompte);

        centreDAO.insert(centre);
        Alerte.info("Centre de tri et compte admin créés.");

        ((Stage) nomField.getScene().getWindow()).close();
    }
}
