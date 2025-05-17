package IHM;

import classe.*;
import DAO.*;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AchatProduitController implements Initializable {

    @FXML private Label labelPoints;
    @FXML private TableView<Commerce> tableCommerces;
    @FXML private Button btnVoirCategories;
    @FXML private TableView<BonReduction> tableBons;
    @FXML private Button btnUtiliserBon;
    @FXML private Button btnRetour;

    private Compte compte;
    private BonReduction bonSelectionne;

    private ObservableList<Commerce> commercesList = FXCollections.observableArrayList();
    private ObservableList<BonReduction> bonsList = FXCollections.observableArrayList();

    public void setCompte(Compte compte) {
        this.compte = compte;
        afficherPoints();
        chargerCommercesDepuisPoubelle();
        chargerBons();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurerTableCommerces();
        configurerTableBons();
        btnVoirCategories.setOnAction(e -> ouvrirPageCategories());
        btnUtiliserBon.setOnAction(e -> ouvrirPageProduitsAvecBon());
        btnRetour.setOnAction(e -> handleRetour());
    }

    private void afficherPoints() {
        labelPoints.setText("Points de fidélité : " + compte.getPointFidelite());
    }

    private void configurerTableCommerces() {
        TableColumn<Commerce, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableColumn<Commerce, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        tableCommerces.getColumns().setAll(nomCol, adresseCol);
        tableCommerces.setItems(commercesList);
    }

    private void configurerTableBons() {
        TableColumn<BonReduction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<BonReduction, Float> tauxCol = new TableColumn<>("Réduction");
        tauxCol.setCellValueFactory(new PropertyValueFactory<>("tauxReduction"));
        tableBons.getColumns().setAll(descCol, tauxCol);
        tableBons.setItems(bonsList);
    }

    private void chargerCommercesDepuisPoubelle() {
        commercesList.clear();
        if (compte.getPoubellesAutorisees().isEmpty()) {
            Alerte.erreur("Aucune poubelle liée au compte.");
            return;
        }
        int idPoubelle = compte.getPoubellesAutorisees().get(0).getIdPoubelle();
        int idCentre = new PoubelleDAO().getCentreIdByPoubelleId(idPoubelle);
        commercesList.addAll(new CommerceDAO().getByCentre(idCentre));
    }

    private void chargerBons() {
        bonsList.clear();
        List<BonReduction> tousLesBons = BonReductionDAO.getBonsByCompte(compte.getIdCompte());
        for (BonReduction b : tousLesBons) {
            if (!b.isEstUtilise()) bonsList.add(b);
        }
    }

    private void ouvrirPageCategories() {
        Commerce selected = tableCommerces.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerte.erreur("Veuillez sélectionner un commerce.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CategorieProduit.fxml"));
            Parent root = loader.load();
            CategorieProduitController controller = loader.getController();
            controller.setCompteEtCommerce(compte, selected, (Stage) labelPoints.getScene().getWindow());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Catégories du commerce");
            stage.show();
            labelPoints.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ouvrirPageProduitsAvecBon() {
        bonSelectionne = tableBons.getSelectionModel().getSelectedItem();
        if (bonSelectionne == null) {
            Alerte.erreur("Veuillez sélectionner un bon à utiliser.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ProduitsAvecReduction.fxml"));
            Parent root = loader.load();
            ProduitsAvecReductionController controller = loader.getController();
            controller.setCompteEtBon(compte, bonSelectionne, (Stage) labelPoints.getScene().getWindow());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Produits à acheter");
            stage.show();
            labelPoints.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserDashboard.fxml"));
            Parent root = loader.load();
            UserDashboardController controller = loader.getController();
            controller.setCompte(compte);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord utilisateur");
            stage.show();

            ((Stage) labelPoints.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            Alerte.erreur("Erreur lors du retour vers le tableau de bord.");
        }
    }
    
    public void rafraichirAffichage() {
        this.compte = new CompteDAO().getById(compte.getIdCompte());
        afficherPoints();
        chargerBons();
    }



}
