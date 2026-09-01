public class SpreadsheetDemo {

    public static void main(String[] args) {
        Feuille sheet = new Feuille(5, 5);

        sheet.setContenu("A1", "10");
        sheet.setContenu("B1", "=A1*2");
        sheet.setContenu("C1", "=B1+5");

        System.out.println("Initial sheet:");
        System.out.println(sheet);

        sheet.setContenu("A1", "7");
        System.out.println("\nAfter changing A1 from 10 to 7:");
        System.out.println("B1 = " + sheet.getDisplay("B1"));
        System.out.println("C1 = " + sheet.getDisplay("C1"));

        try {
            sheet.setContenu("A1", "=C1+1");
        } catch (IllegalStateException exception) {
            System.out.println("\nRejected update: " + exception.getMessage());
            System.out.println("A1 remains " + sheet.getDisplay("A1"));
        }
    }
}
