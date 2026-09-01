import java.util.Locale;
import java.util.Set;

public class ContenuFormule implements IContenu {

    private String contenu;
    private String dernierAffichage;
    private boolean change;

    public ContenuFormule(String contenu) {
        this.contenu = contenu.trim();
        dernierAffichage = null;
        change = false;
    }

    public double getValue(Feuille feuille) {
        return evaluerFormule(feuille, new java.util.HashSet<String>());
    }

    double getValue(Feuille feuille, Set<String> referencesEnCours) {
        return evaluerFormule(feuille, referencesEnCours);
    }

    private double evaluerFormule(Feuille feuille, Set<String> referencesEnCours) {
        String[] morceaux = parserFormule(contenu);

        if (morceaux == null) {
            throw new IllegalArgumentException("Formule invalide: " + contenu);
        }

        double x = lireOperande(morceaux[0], feuille, referencesEnCours);
        double y = lireOperande(morceaux[2], feuille, referencesEnCours);
        String operateur = morceaux[1];

        if (operateur.equals("+")) {
            return x + y;
        }
        if (operateur.equals("-")) {
            return x - y;
        }
        if (operateur.equals("*")) {
            return x * y;
        }
        if (operateur.equals("/")) {
            if (y == 0) {
                return Double.NaN;
            }
            return x / y;
        }

        throw new IllegalArgumentException("Formule invalide: " + contenu);
    }

    private double lireOperande(String operande, Feuille feuille, Set<String> referencesEnCours) {
        operande = operande.trim();

        if (operande.length() == 0) {
            throw new IllegalArgumentException("Formule invalide: " + contenu);
        }

        try {
            return Double.parseDouble(operande);
        } catch (NumberFormatException e) {
            return feuille.getValue(operande, referencesEnCours);
        }
    }

    private static String[] parserFormule(String formule) {
        String expression = formule.substring(1).trim();
        int positionOperateur = trouverOperateur(expression);

        if (positionOperateur < 0) {
            return null;
        }

        String operande1 = expression.substring(0, positionOperateur).trim();
        String operateur = String.valueOf(expression.charAt(positionOperateur));
        String operande2 = expression.substring(positionOperateur + 1).trim();

        if (operande1.length() == 0 || operande2.length() == 0) {
            return null;
        }

        return new String[]{operande1, operateur, operande2};
    }

    private static int trouverOperateur(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c != '+' && c != '-' && c != '*' && c != '/') {
                continue;
            }

            if ((c == '+' || c == '-') && estSigneUnaire(expression, i)) {
                continue;
            }

            return i;
        }

        return -1;
    }

    private static boolean estSigneUnaire(String expression, int position) {
        int precedent = position - 1;

        while (precedent >= 0 && Character.isWhitespace(expression.charAt(precedent))) {
            precedent--;
        }

        if (precedent < 0) {
            return true;
        }

        char c = expression.charAt(precedent);
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public String getDisplay(Feuille feuille) {
        double n = getValue(feuille);
        String affichage;

        if (Double.isNaN(n)) {
            affichage = "#DIV/0!";
        } else if (n == (long) n) {
            affichage = String.format(Locale.US, "%d", (long) n);
        } else {
            affichage = String.format(Locale.US, "%.2f", n);
        }

        change = dernierAffichage == null || !dernierAffichage.equals(affichage);
        dernierAffichage = affichage;

        return affichage;
    }

    public String getContenu() {
        return contenu;
    }

    public boolean aChange() {
        return change;
    }

    public String toString() {
        return contenu;
    }
}
