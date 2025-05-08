package DAO;

import java.sql.*;
import classe.Statistique;
import classe.Depot;
import classe.Dechet;
import classe.Contenu;

/*/ classe DAO de la classe Statistique, permettant son implémentation dans une base de données/*/
public class StatistiqueDAO {

/*/ méthode retournant les statistiques de dépôts pour un CentreDeTri, pas de table dans la base de données pour la classe statistique/*/
    public Statistique getByCentreId(int idCentre) {
        Statistique stat = new Statistique();
        String sql = "SELECT d.* FROM Depot d "
                   + "JOIN Poubelle p ON d.idPoubelle = p.idPoubelle "
                   + "WHERE p.idCentreDeTri = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCentre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Depot d = new Depot();
                d.setIdDepot(rs.getInt("idDepot"));
                d.setQuantite(rs.getFloat("quantite"));
                d.setDateDepot(rs.getTimestamp("dateDepot"));
                d.setPointsGagnes(rs.getFloat("pointsGagnes"));
                d.setIdPoubelle(rs.getString("idPoubelle"));

                Dechet dec = new Dechet();
                dec.setNom(rs.getString("dechetNom"));
                dec.setContenu(Contenu.valueOf(rs.getString("contenu")));
                d.setDechet(dec);

                stat.enregistrerDepot(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stat;
    }
}
