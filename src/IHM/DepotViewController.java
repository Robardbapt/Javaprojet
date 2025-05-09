package IHM;

import DAO.DepotDAO;
import DAO.PoubelleDAO;
import DAO.CompteDAO;
import DAO.HistoriqueDepotDAO;
import classe.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

public class DepotViewController {

    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField masseField;

    private Compte compte;
    private Poubelle poubelle;
    private final HistoriqueDepotDAO historiqueDepotDAO = new HistoriqueDepotDAO();
    private final DepotDAO depotDAO = new DepotDAO();
    private final CompteDAO compteDAO = new CompteDAO();
    private final PoubelleDAO poubelleDAO = new PoubelleDAO();

    public void initialize() {
        typeComboBox.getItems().addAll("Plastique", "Verre", "Papier", "Métaux", "Biodéchet");
    }

    public void initialiser(Compte compte, Poubelle poubelle) {
        this.compte = compte;
        this.poubelle = poubelle;
    }

    @FXML
    private void validerDepot() {
        try {
            String type = typeComboBox.getValue();
            float masse = Float.parseFloat(masseField.getText());

            if (type == null || masse <= 0) {
                throw new IllegalArgumentException("Champs invalides");
            }

            // Sécurisation : recharge de la poubelle depuis la BDD
            this.poubelle = poubelleDAO.getById(poubelle.getIdPoubelle());

            Contenu contenu = mapToEnum(type);
            Dechet dechet = new Dechet(type, contenu, masse);

            if (!poubelle.verifierTypeDechet(dechet)) {
                showAlert(Alert.AlertType.ERROR, "Type incorrect", "Ce type de déchet n'est pas accepté par cette poubelle.");
                return;
            }

            Depot depot = new Depot(dechet, masse, String.valueOf(poubelle.getIdPoubelle()));
            depot.setIdDepot(genererIdDepot());
            depot.setDateDepot(Timestamp.valueOf(LocalDateTime.now()));
            depot.setPointsGagnes(depot.calculerValeurDepot());

            float espaceRestant = poubelle.gererQuantiteDechet(depot);

            if (espaceRestant < 0) {
                showAlert(Alert.AlertType.WARNING, "Capacité dépassée", "La poubelle est pleine ou ne peut pas contenir autant.");
                return;
            }

            compte.getHistorique().ajouterDepot(depot);
            compte.setPointFidelite(compte.getPointFidelite() + depot.getPointsGagnes());

            depotDAO.insert(depot);
            historiqueDepotDAO.insertDepot(depot, compte.getIdCompte());
            compteDAO.updatePoints(compte.getIdCompte(), compte.getPointFidelite());
            poubelleDAO.update(poubelle);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Déchet déposé avec succès ! Points gagnés : " + depot.getPointsGagnes());
            retourDashboard();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez entrer une masse valide (ex : 2.5)");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du dépôt.");
        }
    }

    private Contenu mapToEnum(String type) {
        return switch (type.toLowerCase()) {
            case "plastique"  -> Contenu.PLASTIQUE;
            case "verre"      -> Contenu.VERRE;
            case "papier"     -> Contenu.PAPIER;
            case "métaux"     -> Contenu.METAUX;
            case "biodéchet"  -> Contenu.BIODECHET;
            default -> throw new IllegalArgumentException("Type inconnu : " + type);
        };
    }

    private int genererIdDepot() {
        return new Random().nextInt(900000) + 100000;
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setCompte(compte);

            Stage stage = (Stage) typeComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
