import java.util.HashSet;
import java.util.Set;

public class Feuille {

    private IContenu[][] cellules;
    private int lignes;
    private int colonnes;

    public Feuille(int lignes, int colonnes) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        cellules = new IContenu[lignes][colonnes];
    }

    private String normaliserReference(String ref) {
        if (ref == null) {
            throw new IllegalArgumentException("Reference invalide");
        }

        String reference = ref.trim().toUpperCase();

        if (reference.length() < 2) {
            throw new IllegalArgumentException("Reference invalide");
        }

        char lettre = reference.charAt(0);
        if (lettre < 'A' || lettre > 'Z') {
            throw new IllegalArgumentException("Reference invalide: " + reference);
        }

        for (int i = 1; i < reference.length(); i++) {
            if (!Character.isDigit(reference.charAt(i))) {
                throw new IllegalArgumentException("Reference invalide: " + reference);
            }
        }

        int ligne;
        try {
            ligne = Integer.parseInt(reference.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Reference invalide: " + reference);
        }

        int colonne = lettre - 'A';

        if (ligne < 0 || ligne >= lignes || colonne < 0 || colonne >= colonnes) {
            throw new IllegalArgumentException("Cellule hors de la grille: " + reference);
        }

        return reference;
    }

    private int[] parseReference(String ref) {
        String reference = normaliserReference(ref);
        char lettre = reference.charAt(0);
        int colonne = lettre - 'A';
        int ligne = Integer.parseInt(reference.substring(1)) - 1;
        return new int[]{ligne, colonne};
    }

    public void setContenu(String ref, String contenu) {
        int[] coordonnees = parseReference(ref);
        String texte = contenu == null ? "" : contenu.trim();
        IContenu ancienContenu = cellules[coordonnees[0]][coordonnees[1]];
        IContenu nouveauContenu = creerContenu(texte);

        cellules[coordonnees[0]][coordonnees[1]] = nouveauContenu;

        try {
            recalculerTout();
        } catch (RuntimeException e) {
            cellules[coordonnees[0]][coordonnees[1]] = ancienContenu;
            recalculerTout();
            throw e;
        }
    }

    private IContenu creerContenu(String texte) {
        if (texte.length() == 0) {
            return null;
        }

        if (texte.charAt(0) == '=') {
            return new ContenuFormule(texte);
        }

        try {
            return new ContenuNombre(texte);
        } catch (NumberFormatException e) {
            return new ContenuTexte(texte);
        }
    }

    public double getValue(String ref) {
        return getValue(ref, new HashSet<String>());
    }

    double getValue(String ref, Set<String> referencesEnCours) {
        String reference = normaliserReference(ref);
        int[] coordonnees = parseReference(ref);
        IContenu contenu = cellules[coordonnees[0]][coordonnees[1]];

        if (contenu == null) {
            return 0.0;
        }

        if (referencesEnCours.contains(reference)) {
            throw new IllegalStateException("Reference circulaire: " + reference);
        }

        referencesEnCours.add(reference);
        try {
            if (contenu instanceof ContenuFormule) {
                return ((ContenuFormule) contenu).getValue(this, referencesEnCours);
            }
            return contenu.getValue(this);
        } finally {
            referencesEnCours.remove(reference);
        }
    }

    public String getDisplay(String ref) {
        int[] coordonnees = parseReference(ref);
        IContenu contenu = cellules[coordonnees[0]][coordonnees[1]];

        if (contenu == null) {
            return "";
        }

        return contenu.getDisplay(this);
    }

    public String getContenu(String ref) {
        int[] coordonnees = parseReference(ref);
        IContenu contenu = cellules[coordonnees[0]][coordonnees[1]];

        if (contenu == null) {
            return null;
        }

        return contenu.getContenu();
    }

    public int getNombreLignes() {
        return lignes;
    }

    public int getNombreColonnes() {
        return colonnes;
    }

    public String[][] getAffichages() {
        String[][] affichages = new String[lignes][colonnes];

        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                String ref = String.valueOf((char) ('A' + colonne)) + (ligne + 1);
                affichages[ligne][colonne] = getDisplay(ref);
            }
        }

        return affichages;
    }

    private void recalculerTout() {
        boolean changement;

        do {
            changement = false;

            for (int ligne = 0; ligne < lignes; ligne++) {
                for (int colonne = 0; colonne < colonnes; colonne++) {
                    if (cellules[ligne][colonne] != null) {
                        cellules[ligne][colonne].getDisplay(this);

                        if (cellules[ligne][colonne].aChange()) {
                            changement = true;
                        }
                    }
                }
            }
        } while (changement);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(" \t");
        for (int colonne = 0; colonne < colonnes; colonne++) {
            sb.append((char) ('A' + colonne));
            if (colonne < colonnes - 1) {
                sb.append('\t');
            }
        }
        sb.append('\n');

        for (int ligne = 0; ligne < lignes; ligne++) {
            sb.append(ligne + 1);
            sb.append('\t');

            for (int colonne = 0; colonne < colonnes; colonne++) {
                String ref = String.valueOf((char) ('A' + colonne)) + (ligne + 1);
                sb.append(getDisplay(ref));

                if (colonne < colonnes - 1) {
                    sb.append('\t');
                }
            }

            if (ligne < lignes - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }
}
