package IHM;

import classe.Compte;

public class SessionManager {

    private static Compte compteConnecte;

    public static void setCompteConnecte(Compte compte) {
        SessionManager.compteConnecte = compte;
    }

    public static Compte getCompteConnecte() {
        return compteConnecte;
    }

    public static void clearSession() {
        compteConnecte = null;
    }

    public static boolean isConnecte() {
        return compteConnecte != null;
    }
}
