package IHM;

import DAO.PoubelleDAO;
import DAO.CentreDeTriDAO;
import classe.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreerPoubelleController {

    @FXML private TextField nomField;
    @FXML private TextField capaciteField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<String> typeBox;

    private final PoubelleDAO poubelleDAO = new PoubelleDAO();
    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();

    private Compte admin;

    public void setAdmin(Compte admin) {
        this.admin = admin;
    }

    @FXML
    private void initialize() {
        typeBox.getItems().addAll("PLASTIQUE", "VERRE", "PAPIER", "METAUX", "BIODECHET");
    }

    @FXML
    private void handleCreer() {
        try {
            String nom = nomField.getText();
            float capacite = Float.parseFloat(capaciteField.getText());
            String adresse = adresseField.getText();
            TypePoubelle type = TypePoubelle.valueOf(typeBox.getValue());

            Poubelle p = new Poubelle(nom, type); // id non spécifié car auto-incrémenté
            p.setCapaciteMax(capacite);
            p.setAdresse(adresse);

            int idCentre = centreDAO.getIdCentreByAdmin(admin.getIdCompte());

            poubelleDAO.insert(p, idCentre); // insert sans id manuel

            new Alert(Alert.AlertType.INFORMATION, "Poubelle créée avec succès.").showAndWait();
            ((Stage) nomField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la création de la poubelle.").showAndWait();
        }
    }
}
