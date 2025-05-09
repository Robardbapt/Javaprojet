package IHM;

import classe.Contrat;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ContratDetailController {
    @FXML private Label labelContratInfos;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void afficherContrat(Contrat contrat) {
        String details = "ID : " + contrat.getIdContrat() +
                         "\nDate début : " + contrat.getDateDebut() +
                         "\nDate fin : " + contrat.getDateFin() +
                         "\nTaux : " + contrat.getTauxDeConversion() + "%";
        labelContratInfos.setText(details);
    }

    @FXML
    private void retour() {
        stage.close();
    }
}
