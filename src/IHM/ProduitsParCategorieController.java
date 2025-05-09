package IHM;

import classe.CategorieProduit;
import classe.Produit;
import DAO.ProduitDAO;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProduitsParCategorieController implements Initializable {

    @FXML private Label labelTitreCategorie;
    @FXML private TableView<Produit> tableProduits;

    private CategorieProduit categorie;
    private ObservableList<Produit> produitsList = FXCollections.observableArrayList();

    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
        labelTitreCategorie.setText("Produits de la catégorie : " + categorie.getNom());
        chargerProduits();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<Produit, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Produit, Float> prixCol = new TableColumn<>("Prix");
        prixCol.setCellValueFactory(p -> new SimpleFloatProperty(p.getValue().getPrix()).asObject());

        tableProduits.getColumns().setAll(nomCol, prixCol);
        tableProduits.setItems(produitsList);
    }

    private void chargerProduits() {
        ProduitDAO dao = new ProduitDAO();
        produitsList.setAll(dao.getByCategorieId(categorie.getIdCategorie()));
    }
} 