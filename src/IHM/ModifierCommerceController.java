package IHM;

import DAO.CommerceDAO;
import classe.Commerce;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierCommerceController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldAdresse;

    private Commerce commerce;
    private Stage stage;

    public void setCommerce(Commerce commerce) {
        this.commerce = commerce;
        fieldNom.setText(commerce.getNom());
        fieldAdresse.setText(commerce.getAdresse());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void validerModif() {
        String nouveauNom = fieldNom.getText();
        String nouvelleAdresse = fieldAdresse.getText();

        if (nouveauNom.isEmpty() || nouvelleAdresse.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.").showAndWait();
            return;
        }

        commerce.setNom(nouveauNom);
        commerce.setAdresse(nouvelleAdresse);

        CommerceDAO dao = new CommerceDAO();
        dao.update(commerce);

        stage.close();
    }

    @FXML
    private void fermer() {
        stage.close();
    }
}
