package IHM;

import classe.Compte;

/**
 * Classe utilitaire pour gérer l'utilisateur connecté dans le système.
 * Permet d'accéder globalement au compte utilisateur en cours de session.
 */
public class SessionManager {

    private static Compte compteConnecte;

    /**
     * Définit le compte actuellement connecté.
     * @param compte le compte utilisateur actif
     */
    public static void setCompteConnecte(Compte compte) {
        SessionManager.compteConnecte = compte;
    }

    /**
     * Retourne le compte actuellement connecté.
     * @return l'utilisateur actif
     */
    public static Compte getCompteConnecte() {
        return compteConnecte;
    }

    /**
     * Réinitialise la session.
     */
    public static void clearSession() {
        compteConnecte = null;
    }

    /**
     * Vérifie s'il y a une session active.
     * @return true si un compte est connecté, sinon false
     */
    public static boolean isConnecte() {
        return compteConnecte != null;
    }
}
