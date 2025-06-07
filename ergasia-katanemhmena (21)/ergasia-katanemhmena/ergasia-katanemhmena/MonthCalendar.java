import java.io.Serializable;

public class MonthCalendar implements Serializable {
    private int[] daysOfMonth;

    public MonthCalendar() {
        // 31 meres kathe mhna
        daysOfMonth = new int[30];
        // 0 --> diathesimi mera , 1 --> mhDiathesimh
        for (int i = 0; i < 30; i++) {
            daysOfMonth[i] = 1;
        }
    }

    // kratisi gia mia mera
    public void reserveDay(int day) {
        // kratisi
        if (day >= 1 && day <= 30) {
            daysOfMonth[day - 1] = 1;
            System.out.println("---> Day : " + day + " set Mh diathesimi <---");
        } else {

            System.out.println("Invalid day format.");
        }
    }

    // akirwsh kratisis
    public void cancelReservation(int day) {

        if (day >= 1 && day <= 30) {
            daysOfMonth[day - 1] = 0;
            System.out.println("---> Reservation of the day : " + day + " is cancelled <---");
        } else {
            System.out.println("Invalid day format.");
        }
    }

    // Bazei diathesimi
    public void setZero(int day) {
        if (day >= 1 && day <= 30) {
            daysOfMonth[day - 1] = 0;
        } else {
            System.out.println("Invalid day format.");
        }
    }

    public boolean isZero(int day) { // Elegxei an h timh einai 0 --> Diathesimi mera
        boolean flag;
        if (this.daysOfMonth[day - 1] == 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    // Gia kathe mina emfanizei to imerologio-kratiseis
    public void displayCalendar() {
        for (int i = 0; i < 30; i++) {
            if (daysOfMonth[i] == 1) { // An einai 1 tote einai kratimeni
                System.out.println(" *Day " + (i + 1) + ": Mh diathesimi");
            } else {
                System.out.println(" *Day " + (i + 1) + ": Diathesimi");
            }
        }
    }

}
