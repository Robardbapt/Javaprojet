package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour l'accès à la base de données.
 */
public class DataBaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/tri_selectif";
    private static final String USER = "root"; 
    private static final String PASSWORD = "";

    // Constructeur privé pour empêcher l'instanciation
    private DataBaseManager() {
        throw new UnsupportedOperationException("Classe utilitaire, instanciation interdite.");
    }

    /**
     * Retourne une connexion à la base de données.
     * @return Connexion SQL
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
