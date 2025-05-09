package IHM;

import DAO.CentreDeTriDAO;
import DAO.CompteDAO;
import classe.CentreDeTri;
import classe.Compte;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class TransfererCentreController {

    @FXML private ComboBox<CentreDeTri> centreBox;

    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();
    private final CompteDAO compteDAO = new CompteDAO();

    private Compte compte;

    public void initialiser(Compte c) {
        this.compte = c;
        List<CentreDeTri> centres = centreDAO.getAll();
        centreBox.getItems().addAll(centres);
    }

    @FXML
    private void handleValider() {
        CentreDeTri nouveauCentre = centreBox.getValue();
        if (nouveauCentre == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un centre.").showAndWait();
            return;
        }

        compteDAO.deplacerCompteVersCentre(compte.getIdCompte(), nouveauCentre.getIdCentreDeTri());

        new Alert(Alert.AlertType.INFORMATION, "Le compte a été transféré avec succès.").showAndWait();
        ((Stage) centreBox.getScene().getWindow()).close();
    }
}
