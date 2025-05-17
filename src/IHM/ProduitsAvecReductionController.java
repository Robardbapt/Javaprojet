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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProduitsAvecReductionController implements Initializable {

    @FXML private Label labelPoints;
    @FXML private TableView<Produit> tableProduits;
    @FXML private Button btnAcheterProduit;
    @FXML private Button btnRetour;

    private Compte compte;
    private BonReduction bon;
    private ObservableList<Produit> produitsList = FXCollections.observableArrayList();
    private Stage previousStage;

    public void setCompteEtBon(Compte compte, BonReduction bon, Stage previousStage) {
        this.compte = compte;
        this.bon = bon;
        this.previousStage = previousStage;
        labelPoints.setText("Points de fidélité : " + compte.getPointFidelite());
        chargerProduits();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<Produit, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getNom()));

        TableColumn<Produit, Float> prixBaseCol = new TableColumn<>("Prix original");
        prixBaseCol.setCellValueFactory(p -> new SimpleFloatProperty(p.getValue().getPrix()).asObject());

        TableColumn<Produit, Float> prixReduitCol = new TableColumn<>("Prix après réduction");
        prixReduitCol.setCellValueFactory(p -> {
            float reductionPourcent = bon.getTauxReduction();
            float taux = Math.min(reductionPourcent / 100f, 0.9f);
            float prixReduit = Math.max(0f, p.getValue().getPrix() * (1 - taux));
            return new SimpleFloatProperty(prixReduit).asObject();
        });

        tableProduits.getColumns().setAll(nomCol, prixBaseCol, prixReduitCol);
        tableProduits.setItems(produitsList);

        btnAcheterProduit.setOnAction(e -> acheterProduit());
        btnRetour.setOnAction(e -> handleRetour());
    }

    private void chargerProduits() {
        produitsList.setAll(new ProduitDAO().getByCategorieId(bon.getCategorieLiee().getIdCategorie()));
    }

    private void acheterProduit() {
        Produit produit = tableProduits.getSelectionModel().getSelectedItem();
        if (produit == null) {
            Alerte.erreur("Veuillez sélectionner un produit.");
            return;
        }

        bon.utiliserBon();
        new BonReductionDAO().update(bon);

        compte = new CompteDAO().getById(compte.getIdCompte());
        SessionManager.setCompteConnecte(compte);
        labelPoints.setText("Points de fidélité : " + compte.getPointFidelite());

        Alerte.info("Produit acheté avec succès !");
        produitsList.clear();

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
