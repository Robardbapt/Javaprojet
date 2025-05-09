package IHM;

import DAO.CentreDeTriDAO;
import DAO.StatistiqueDAO;
import classe.Compte;
import classe.Statistique;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class StatistiqueController {

    @FXML private Label totalLabel;
    @FXML private Label moyenneLabel;

    private final CentreDeTriDAO centreDAO = new CentreDeTriDAO();
    private final StatistiqueDAO statistiqueDAO = new StatistiqueDAO();
    private Compte admin;

    public void setCompteAdmin(Compte admin) {
        this.admin = admin;
        chargerStatistiques();
    }

    private void chargerStatistiques() {
        int idCentre = centreDAO.getIdCentreByAdmin(admin.getIdCompte());
        Statistique stat = statistiqueDAO.getByCentreId(idCentre);

        totalLabel.setText("Quantité totale déposée : " + stat.calculerTotalDechets() + " kg");
        moyenneLabel.setText("Quantité moyenne par dépôt : " + stat.productionMoyenne() + " kg");
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/AdminDashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setCompte(admin);

            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
