package IHM;

import DAO.PoubelleDAO;
import classe.Poubelle;
import classe.TypePoubelle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierPoubelleController {

    @FXML private TextField nomField;
    @FXML private TextField capaciteMaxField;
    @FXML private TextField capaciteActuelleField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<TypePoubelle> typeBox;

    private Poubelle poubelle;
    private final PoubelleDAO dao = new PoubelleDAO();

    public void setPoubelle(Poubelle p) {
        this.poubelle = p;

        nomField.setText(p.getNom());
        capaciteMaxField.setText(String.valueOf(p.getCapaciteMax()));
        capaciteActuelleField.setText(String.valueOf(p.getCapaciteActuelle()));
        adresseField.setText(p.getAdresse());
        typeBox.getItems().setAll(TypePoubelle.values());
        typeBox.setValue(p.getType());
    }

    @FXML
    private void handleSave() {
        try {
            poubelle.setNom(nomField.getText());
            poubelle.setCapaciteMax(Float.parseFloat(capaciteMaxField.getText()));
            poubelle.setCapaciteActuelle(Float.parseFloat(capaciteActuelleField.getText()));
            poubelle.setAdresse(adresseField.getText());
            poubelle.setType(typeBox.getValue());

            poubelle.verifierPleine(); // calcule automatiquement estPleine
            dao.update(poubelle);

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l’enregistrement").showAndWait();
            e.printStackTrace();
        }
    }
}
