public class TestTableur {

    private static int tests;

    public static void main(String[] args) {
        testerToutesLesCellules5x5();
        testerReferencesInvalides();
        testerReferencesMinuscules();
        testerFormulesEtRafraichissement();
        testerFormulesInvalidesSansPollution();
        testerReferencesHorsGrilleDansFormule();
        testerReferencesCirculaires();
        testerTexteDansFormule();
        testerDivisionParZeroEtNombresSignes();
        testerEffacementEtTextesAmbigus();

        System.out.println("Tests OK: " + tests);
    }

    private static void testerToutesLesCellules5x5() {
        Feuille feuille = new Feuille(5, 5);

        for (int ligne = 1; ligne <= 5; ligne++) {
            for (char colonne = 'A'; colonne <= 'E'; colonne++) {
                String ref = "" + colonne + ligne;
                feuille.setContenu(ref, "1");
                verifier("1".equals(feuille.getDisplay(ref)), ref + " doit etre accessible");
            }
        }
    }

    private static void testerReferencesInvalides() {
        Feuille feuille = new Feuille(5, 5);
        String[] references = {"", "A", "A0", "F1", "A6", "AA1", "1A", null};

        for (String ref : references) {
            attendreExceptionControlee(
                    () -> feuille.setContenu(ref, "1"),
                    "Reference invalide attendue pour " + ref
            );
        }
    }

    private static void testerReferencesMinuscules() {
        Feuille feuille = new Feuille(5, 5);

        feuille.setContenu("a5", "3");
        verifierEgal("3", feuille.getDisplay("A5"), "a5 doit etre normalise en A5");

        feuille.setContenu("a1", "2");
        feuille.setContenu("b1", "=a1+1");
        verifierEgal("3", feuille.getDisplay("B1"), "les references minuscules doivent marcher dans les formules");
    }

    private static void testerFormulesEtRafraichissement() {
        Feuille feuille = new Feuille(5, 5);

        feuille.setContenu("A1", "1");
        feuille.setContenu("B1", "=A1+1");
        verifierEgal("2", feuille.getDisplay("B1"), "B1 doit valoir 2");

        feuille.setContenu("A1", "5");
        verifierEgal("6", feuille.getDisplay("B1"), "B1 doit etre recalcule apres modification de A1");
        verifierEgal("6", feuille.getAffichages()[0][1], "la grille d'affichage doit contenir B1 recalcule");
    }

    private static void testerFormulesInvalidesSansPollution() {
        Feuille feuille = new Feuille(5, 5);

        feuille.setContenu("A1", "7");
        attendreException(IllegalArgumentException.class, () -> feuille.setContenu("A1", "=1+"), "formule incomplete");
        verifierEgal("7", feuille.getDisplay("A1"), "ancienne valeur A1 conservee apres formule invalide");

        feuille.setContenu("B1", "2");
        verifierEgal("2", feuille.getDisplay("B1"), "une erreur precedente ne doit pas bloquer les autres cellules");
    }

    private static void testerReferencesHorsGrilleDansFormule() {
        Feuille feuille = new Feuille(5, 5);

        attendreException(IllegalArgumentException.class, () -> feuille.setContenu("A1", "=F1+1"), "reference F1 hors grille");
        verifier(feuille.getContenu("A1") == null, "la formule hors grille ne doit pas rester en A1");

        for (char colonne = 'A'; colonne <= 'E'; colonne++) {
            String ref = "" + colonne + 5;
            feuille.setContenu(ref, "4");
            verifierEgal("4", feuille.getDisplay(ref), ref + " doit rester utilisable apres rejet d'une formule hors grille");
        }
    }

    private static void testerReferencesCirculaires() {
        Feuille feuille = new Feuille(5, 5);

        attendreException(IllegalStateException.class, () -> feuille.setContenu("A1", "=A1+1"), "reference circulaire directe");
        verifier(feuille.getContenu("A1") == null, "la reference circulaire directe ne doit pas rester en A1");

        feuille.setContenu("A1", "1");
        feuille.setContenu("B1", "=A1+1");
        attendreException(IllegalStateException.class, () -> feuille.setContenu("A1", "=B1+1"), "reference circulaire indirecte");
        verifierEgal("1", feuille.getDisplay("A1"), "ancienne valeur A1 conservee apres cycle indirect");
        verifierEgal("2", feuille.getDisplay("B1"), "B1 reste coherent apres rejet du cycle");
    }

    private static void testerTexteDansFormule() {
        Feuille feuille = new Feuille(5, 5);

        feuille.setContenu("A1", "bonjour");
        attendreException(IllegalStateException.class, () -> feuille.setContenu("B1", "=A1+1"), "texte utilise comme nombre");
        verifier(feuille.getContenu("B1") == null, "la formule avec texte ne doit pas rester en B1");

        feuille.setContenu("A1", "10");
        feuille.setContenu("B1", "=A1+1");
        attendreException(IllegalStateException.class, () -> feuille.setContenu("A1", "bonjour"), "texte qui casserait une formule dependante");
        verifierEgal("10", feuille.getDisplay("A1"), "ancienne valeur A1 conservee si un texte casse une formule");
        verifierEgal("11", feuille.getDisplay("B1"), "formule dependante conservee apres rollback");
    }

    private static void testerDivisionParZeroEtNombresSignes() {
        Feuille feuille = new Feuille(5, 5);

        feuille.setContenu("A1", "=4/0");
        verifierEgal("#DIV/0!", feuille.getDisplay("A1"), "division par zero");

        feuille.setContenu("B1", "=-1+2");
        verifierEgal("1", feuille.getDisplay("B1"), "nombre negatif en premier operande");

        feuille.setContenu("C1", "=3*-2");
        verifierEgal("-6", feuille.getDisplay("C1"), "nombre negatif en second operande");
    }

    private static void testerEffacementEtTextesAmbigus() {
        Feuille feuille = new Feuille(5, 5);

        feuille.setContenu("A1", "10");
        feuille.setContenu("B1", "=A1+1");
        feuille.setContenu("A1", "");
        verifier(feuille.getContenu("A1") == null, "effacer une cellule doit la rendre vide");
        verifierEgal("1", feuille.getDisplay("B1"), "une cellule vide vaut 0 dans une formule");

        feuille.setContenu("C1", "-");
        verifierEgal("-", feuille.getDisplay("C1"), "un tiret seul doit etre du texte");

        feuille.setContenu("D1", ".");
        verifierEgal(".", feuille.getDisplay("D1"), "un point seul doit etre du texte");
    }

    private static void attendreExceptionControlee(Action action, String message) {
        try {
            action.executer();
        } catch (IllegalArgumentException | IllegalStateException e) {
            tests++;
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new AssertionError(message + " ne doit pas produire ArrayIndexOutOfBoundsException", e);
        } catch (Throwable e) {
            throw new AssertionError(message + " a produit une exception inattendue: " + e.getClass().getName(), e);
        }

        throw new AssertionError(message + " n'a produit aucune exception");
    }

    private static void attendreException(Class<? extends Throwable> type, Action action, String message) {
        try {
            action.executer();
        } catch (Throwable e) {
            if (type.isInstance(e)) {
                tests++;
                return;
            }

            throw new AssertionError(message + " a produit " + e.getClass().getName()
                    + " au lieu de " + type.getName(), e);
        }

        throw new AssertionError(message + " n'a produit aucune exception");
    }

    private static void verifierEgal(String attendu, String obtenu, String message) {
        verifier(attendu.equals(obtenu), message + " (attendu: " + attendu + ", obtenu: " + obtenu + ")");
    }

    private static void verifier(boolean condition, String message) {
        tests++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private interface Action {
        void executer();
    }
}
