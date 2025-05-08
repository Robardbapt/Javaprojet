package IHM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour établir et fermer la connexion à la base de données MySQL.
 */
public class DataBaseConnection {

    // URL de la base de données (à adapter selon ta config)
    private static final String URL = "jdbc:mysql://localhost:3306/tri_selectif?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    /**
     * Retourne une instance unique de la connexion JDBC.
     * @return une connexion MySQL active
     * @throws SQLException si erreur lors de la connexion
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données réussie.");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver JDBC non trouvé.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la connexion à la base.");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    /**
     * Ferme proprement la connexion si elle est ouverte.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion.");
            e.printStackTrace();
        }
    }
}
