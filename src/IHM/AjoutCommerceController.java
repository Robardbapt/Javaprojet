package IHM;

import DAO.CommerceDAO;
import DAO.ContratDAO;
import classe.Commerce;
import classe.Contrat;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class AjoutCommerceController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldAdresse;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private TextField fieldTaux;

    private Stage stage;
    private int idCentre;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setIdCentre(int idCentre) {
        this.idCentre = idCentre;
    }

    @FXML
    private void ajouterCommerce() {
        String nom = fieldNom.getText();
        String adresse = fieldAdresse.getText();
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();
        int taux;

        try {
            taux = Integer.parseInt(fieldTaux.getText());
        } catch (NumberFormatException e) {
            showAlert("Taux invalide");
            return;
        }

        if (nom.isEmpty() || adresse.isEmpty() || debut == null || fin == null) {
            showAlert("Tous les champs doivent être remplis.");
            return;
        }

        Commerce commerce = new Commerce(nom, adresse);
        CommerceDAO commerceDAO = new CommerceDAO();
        commerceDAO.insert(commerce);

        Contrat contrat = new Contrat();
        contrat.setDateDebut(Date.valueOf(debut));
        contrat.setDateFin(Date.valueOf(fin));
        contrat.setTauxDeConversion(taux);
        contrat.setIdCentre(idCentre);
        ContratDAO contratDAO = new ContratDAO();
        contratDAO.insert(contrat, idCentre);

        commerceDAO.lierAuContrat(commerce.getIdCommerce(), contrat.getIdContrat());

        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    @FXML
    private void fermerFenetre() {
        stage.close();
    }
}
