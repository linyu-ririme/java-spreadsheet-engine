public class ContenuTexte implements IContenu {

    private String contenu;

    public ContenuTexte(String contenu) {
        this.contenu = contenu.trim();
    }

    public double getValue(Feuille feuille) {
        throw new IllegalStateException("texte");
    }

    public String getDisplay(Feuille feuille) {
        return contenu;
    }

    public String getContenu() {
        return contenu;
    }

    public boolean aChange() {
        return false;
    }

    public String toString() {
        return contenu;
    }
}
