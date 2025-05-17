package IHM;

import classe.*;
import DAO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategorieProduitController implements Initializable {

    @FXML private Label labelPoints;
    @FXML private TableView<CategorieProduit> tableCategories;
    @FXML private Button btnAcheterBon;
    @FXML private Button btnRetour;

    private Compte compte;
    private Commerce commerce;
    private Stage previousStage;

    private ObservableList<CategorieProduit> categoriesList = FXCollections.observableArrayList();

    public void setCompteEtCommerce(Compte compte, Commerce commerce, Stage previousStage) {
        this.compte = compte;
        this.commerce = commerce;
        this.previousStage = previousStage;
        labelPoints.setText("Points de fidélité : " + compte.getPointFidelite());
        chargerCategories();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<CategorieProduit, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        TableColumn<CategorieProduit, Float> tauxCol = new TableColumn<>("Réduction");
        tauxCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getTauxReduction()).asObject());
        TableColumn<CategorieProduit, Integer> ptsCol = new TableColumn<>("Points nécessaires");
        ptsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPointNecessaire()).asObject());
        tableCategories.getColumns().setAll(nomCol, tauxCol, ptsCol);
        tableCategories.setItems(categoriesList);

        btnAcheterBon.setOnAction(e -> acheterBon());
        btnRetour.setOnAction(e -> handleRetour());
    }

    private void chargerCategories() {
        CategorieProduitDAO dao = new CategorieProduitDAO();
        List<CategorieProduit> categories = dao.getByCommerce(commerce.getIdCommerce());
        categoriesList.setAll(categories);
    }

    private void acheterBon() {
        CategorieProduit selected = tableCategories.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerte.erreur("Sélectionnez une catégorie.");
            return;
        }

        if (compte.getPointFidelite() < selected.getPointNecessaire()) {
            Alerte.erreur("Points insuffisants.");
            return;
        }

        BonReduction bon = new BonReduction("Réduction pour " + selected.getNom(), selected.getPointNecessaire(), selected.getTauxReduction(), selected);
        new BonReductionDAO().insert(bon, compte.getIdCompte());

        compte = new CompteDAO().getById(compte.getIdCompte());
        SessionManager.setCompteConnecte(compte);
        labelPoints.setText("Points de fidélité : " + compte.getPointFidelite());

        Alerte.info("Bon de réduction créé !");
        if (previousStage != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AchatProduit.fxml"));
                Parent root = loader.load();
                AchatProduitController controller = loader.getController();
                controller.setCompte(compte);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Achat de produits");
                stage.show();
                ((Stage) labelPoints.getScene().getWindow()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRetour() {
        Stage current = (Stage) labelPoints.getScene().getWindow();
        current.close();
        if (previousStage != null) {
            previousStage.show();
        }
    }
}
