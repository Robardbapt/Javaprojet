package IHM;

import classe.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label pointsLabel;
    @FXML private Label adresseLabel;
    @FXML private Label centreLabel;
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

        afficherCentreTri();

        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getIdPoubelle()));
        colType.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getType().toString()));
        colCapacite.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCapaciteMax()));
        colEmplacement.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAdresse()));

        ajouterBoutonsDepot();
        chargerPoubellesDepuisBDD();
    }

    private void afficherCentreTri() {
        try (Connection conn = DataBaseConnection.getConnection()) {
            String sql = """
                SELECT nom FROM CentreDeTri 
                WHERE idCentreDeTri = (
                    SELECT idCentreDeTri FROM Poubelle p
                    JOIN Compte_Poubelle cp ON p.idPoubelle = cp.idPoubelle
                    WHERE cp.idCompte = ?
                    LIMIT 1
                )
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, compte.getIdCompte());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                centreLabel.setText("Centre assigné : " + rs.getString("nom"));
            } else {
                centreLabel.setText("Centre assigné : Aucun");
            }

        } catch (Exception e) {
            centreLabel.setText("Erreur lors du chargement du centre");
            e.printStackTrace();
        }
    }

    private void chargerPoubellesDepuisBDD() {
        try (Connection conn = DataBaseConnection.getConnection()) {
            String sql = """
                SELECT p.idPoubelle, p.nom, p.typePoubelle, p.capaciteMax, p.adresse
                FROM compte_poubelle cp
                JOIN Poubelle p ON cp.idPoubelle = p.idPoubelle
                WHERE cp.idCompte = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, compte.getIdCompte());
            ResultSet rs = stmt.executeQuery();

            poubelleTable.getItems().clear();

            while (rs.next()) {
                int id = rs.getInt("idPoubelle");
                String nom = rs.getString("nom");
                TypePoubelle type = TypePoubelle.valueOf(rs.getString("typePoubelle"));
                float capacite = rs.getFloat("capaciteMax");
                String adresse = rs.getString("adresse");

                Poubelle p = new Poubelle(id, nom, type);
                p.setCapaciteMax(capacite);
                p.setAdresse(adresse);
                poubelleTable.getItems().add(p);
            }

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

    @FXML
    private void handleDeconnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion Utilisateur");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
