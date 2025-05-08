package IHM;

import classe.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label pointsLabel;
    @FXML private Label adresseLabel;
    @FXML private VBox listePoubellesVBox;
    @FXML private TableView<Poubelle> poubelleTable;
    @FXML private TableColumn<Poubelle, Integer> colId;
    @FXML private TableColumn<Poubelle, String> colType;
    @FXML private TableColumn<Poubelle, Float> colCapacite;
    @FXML private TableColumn<Poubelle, String> colEmplacement;
    @FXML private TableColumn<Poubelle, Void> colAction;


    private Compte compte;

    public void setCompte(Compte compte) {
        this.compte = compte;
        welcomeLabel.setText("Bienvenue " + compte.getNom());
        pointsLabel.setText("Points de fidélité : " + compte.getPointFidelite());
        adresseLabel.setText("Adresse : " + compte.getAdresse());

        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getIdPoubelle()));
        colType.setCellValueFactory(data -> new ReadOnlyObjectWrapper<String>(data.getValue().getType().toString()));
        colCapacite.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Float>(data.getValue().getCapaciteMax()));

        colEmplacement.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAdresse()));

        ajouterBoutonsDepot();
        poubelleTable.getItems().addAll(compte.getPoubellesAutorisees());


        chargerPoubellesDepuisBDD();
    }

    private void chargerPoubellesDepuisBDD() {
        try (Connection conn = DataBaseConnection.getConnection()) {
            String sql = "SELECT * FROM compte_poubelle WHERE idCompte = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, compte.getIdCompte());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
            	int id = rs.getInt("idPoubelle");
            	String nom = rs.getString("nom");
            	TypePoubelle type = TypePoubelle.valueOf(rs.getString("typePoubelle"));
            	float capacite = rs.getFloat("capacite");
            	String adresse = rs.getString("emplacement");

            	Poubelle p = new Poubelle(id, nom, type);
            	p.setCapaciteMax(capacite);
            	p.setAdresse(adresse);

                VBox bloc = new VBox(5);
                bloc.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #f8f8f8;");

                Label idLabel = new Label("ID : " + id);
                Label typeLabel = new Label("Type : " + type);
                Label capaciteLabel = new Label("Capacité max : " + capacite);
                Label emplacementLabel = new Label("Emplacement : " + adresse);

                Button boutonDepot = new Button("Déposer ici");
                boutonDepot.setOnAction(e -> {
                    // À remplacer plus tard par une popup ou choix réel de déchet
                    System.out.println("Dépôt demandé dans poubelle " + id);
                });

                bloc.getChildren().addAll(idLabel, typeLabel, capaciteLabel, emplacementLabel, boutonDepot);
                listePoubellesVBox.getChildren().add(bloc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoirHistorique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Historique.fxml"));
            Parent root = loader.load();

            HistoriqueController controller = loader.getController();
            controller.setCompte(compte);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Historique des dépôts");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoirProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/AchatProduits.fxml"));
            Parent root = loader.load();

            AchatProduitsController controller = loader.getController();
            controller.setCompte(compte);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Produits et Bons de réduction");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ajouterBoutonsDepot() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Déposer");

            {
                btn.setOnAction(event -> {
                    Poubelle p = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/DepotView.fxml"));
                        Parent root = loader.load();

                        DepotViewController controller = loader.getController();
                        controller.initialiser(compte, p);

                        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                        stage.setScene(new Scene(root));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    // Tu peux ici ouvrir une popup ou appeler une méthode pour simuler le dépôt
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    @FXML
    private void handleDeconnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connexion Utilisateur");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
