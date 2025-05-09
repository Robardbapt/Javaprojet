package IHM;

import DAO.*;
import classe.*;
import javafx.beans.property.SimpleStringProperty;
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

public class ConversionPointsController implements Initializable {

    @FXML private Label labelPoints;
    @FXML private TableView<Commerce> tableCommerces;
    @FXML private TableColumn<Commerce, String> colNomCommerce;
    @FXML private TableColumn<Commerce, Void> colBtnVoirCategories;

    @FXML private TableView<CategorieProduit> tableCategories;
    @FXML private TableColumn<CategorieProduit, String> colNomCat;
    @FXML private TableColumn<CategorieProduit, Float> colTaux;
    @FXML private TableColumn<CategorieProduit, Integer> colPoints;
    @FXML private TableColumn<CategorieProduit, Void> colVoirProduits;
    @FXML private TableColumn<CategorieProduit, Void> colConvertir;
    @FXML private Button btnRetour;

    private Compte compte;
    private ObservableList<Commerce> commercesList = FXCollections.observableArrayList();
    private ObservableList<CategorieProduit> categoriesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        compte = SessionManager.getCompteConnecte();
        afficherPoints();
        configurerTableCommerces();
        configurerTableCategories();
        chargerCommerces();
        btnRetour.setOnAction(e -> handleRetour());
    }

    private void afficherPoints() {
        labelPoints.setText("Points : " + compte.getPointFidelite());
    }

    private void configurerTableCommerces() {
        colNomCommerce.setCellValueFactory(new PropertyValueFactory<>("nom"));

        colBtnVoirCategories.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Voir catégories");

            {
                btn.setOnAction(e -> {
                    Commerce commerce = getTableView().getItems().get(getIndex());
                    ouvrirPageCategorieProduit(commerce);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tableCommerces.setItems(commercesList);
    }

    private void configurerTableCategories() {
        colNomCat.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colTaux.setCellValueFactory(new PropertyValueFactory<>("tauxReduction"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("pointNecessaire"));

        colVoirProduits.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Produits");

            {
                btn.setOnAction(e -> {
                    CategorieProduit cat = getTableView().getItems().get(getIndex());
                    Alerte.info("Produits liés à : " + cat.getNom());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        colConvertir.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Convertir");

            {
                btn.setOnAction(e -> {
                    CategorieProduit cat = getTableView().getItems().get(getIndex());
                    convertirEnBon(cat);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tableCategories.setItems(categoriesList);
    }

    private void chargerCommerces() {
        commercesList.clear();
        if (compte.getPoubellesAutorisees().isEmpty()) {
            Alerte.erreur("Aucune poubelle liée au compte.");
            return;
        }
        int idPoubelle = compte.getPoubellesAutorisees().get(0).getIdPoubelle();
        int idCentre = new PoubelleDAO().getCentreIdByPoubelleId(idPoubelle);
        commercesList.addAll(new CommerceDAO().getByCentre(idCentre));
    }

    private void convertirEnBon(CategorieProduit cat) {
        if (compte.getPointFidelite() < cat.getPointNecessaire()) {
            Alerte.erreur("Points insuffisants pour cette catégorie.");
            return;
        }

        BonReduction bon = new BonReduction("Réduction pour " + cat.getNom(), cat.getPointNecessaire(), cat.getTauxReduction(), cat);
        new BonReductionDAO().insert(bon, compte.getIdCompte());

        compte = new CompteDAO().getById(compte.getIdCompte());
        SessionManager.setCompteConnecte(compte);
        afficherPoints();

        Alerte.info("Bon de réduction créé avec succès.");
    }

    private void ouvrirPageCategorieProduit(Commerce commerce) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CategorieProduit.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            CategorieProduitController controller = loader.getController();
            controller.setCompteEtCommerce(compte, commerce, (Stage) labelPoints.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setTitle("Catégories de produits");
            stage.show();
            labelPoints.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() {
        ((Stage) labelPoints.getScene().getWindow()).close();
    }
}