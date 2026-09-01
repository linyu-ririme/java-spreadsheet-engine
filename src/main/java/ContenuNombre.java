import java.util.Locale;

public class ContenuNombre implements IContenu {

    private String contenu;
    private double nombre;

    public ContenuNombre(String contenu) {
        this.contenu = contenu.trim();
        nombre = Double.parseDouble(this.contenu);
    }

    public double getValue(Feuille feuille) {
        return nombre;
    }

    public String getDisplay(Feuille feuille) {
        if (nombre == (long) nombre) {
            return String.format(Locale.US, "%d", (long) nombre);
        }

        return String.format(Locale.US, "%.2f", nombre);
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
