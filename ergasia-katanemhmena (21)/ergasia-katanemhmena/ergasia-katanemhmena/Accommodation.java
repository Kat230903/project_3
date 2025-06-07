import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.time.ZoneId;

@SuppressWarnings("all")
public class Accommodation implements Serializable {
    String roommName;
    int noOfPersons;
    String area;
    int stars;
    int noOfReviews;
    String dates[];
    String managerName;
    int price;

    private MonthCalendar[] months;

    public Accommodation(String roommName, int persons, String area, int stars, int reviews, int price) {

        this.roommName = roommName;
        this.noOfPersons = persons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = reviews;
        this.price = price;

        // 0 --> Iounios
        // 1 --> Ioylios
        // 2 --> Augoustos
        this.months = new MonthCalendar[3]; // 3 mines ara 3 MonthCalendar
        for (int i = 0; i < 3; i++) { // Iounios Ioulios Augoustos
            months[i] = new MonthCalendar();
        }

    }

    // Gia kratisi imeras enos mhna
    public void reserveDay(int monthIndex, int day) {
        if (monthIndex >= 0 && monthIndex < 3) {
            months[monthIndex].reserveDay(day);
        } else {
            System.out.println("Sflama deikti: " + monthIndex);
        }
    }

    public boolean checkAvailability(int month, int startDay, int endDay) {
        for (int i = startDay; i <= endDay; i++) {
            if (!IsDiathesimi(month, i)) {
                return false;
            }
        }
        return true;
    }

    public void reserveDays(int startMonth, int startDay, int endMonth, int endDay) {
        if (startMonth == endMonth) {
            for (int i = startDay; i <= endDay; i++) {
                this.reserveDay(startMonth, i);
            }
        } else {
            for (int i = startDay; i <= 30; i++) {
                this.reserveDay(startMonth, i);
            }
            for (int i = 1; i <= endDay; i++) {
                this.reserveDay(endMonth, i);
            }
        }
    }

    // Akirwsh kratisis
    public void cancelReservation(int monthIndex, int day) {
        if (monthIndex >= 0 && monthIndex < 3) {
            System.out.print("Gia ton mhna " + monthIndex + " ");
            months[monthIndex].cancelReservation(day);
        } else {
            System.out.println("Sflama deikti: " + monthIndex);
        }
    }

    public void setDiathesimi(int monthIndex, int day) {
        if (monthIndex >= 0 && monthIndex < 3) {
            months[monthIndex].setZero(day);
        } else {
            System.out.println("Sflama deikti: " + monthIndex);
        }
    }

    // Εmfanisi imerolohiou mina monthIndex
    public void displayCalendar(int monthIndex) {
        if (monthIndex >= 0 && monthIndex < 3) {
            System.out.println("Hmerologio gia mhna " + (monthIndex + 1) + ":");
            months[monthIndex].displayCalendar();
        } else {
            System.out.println("Sfalma deikti: " + monthIndex);
        }
    }

    public void showMore() {
        System.out.println("");
        System.out.println("--STOIXEIA KATALYMATOS--");
        System.out.println("Room Name: " + roommName);
        System.out.println("Number of Persons: " + noOfPersons);
        System.out.println("Area: " + area);
        System.out.println("Number Of Stars: " + stars/noOfReviews);
        System.out.println("Number Of Reviews: " + noOfReviews);
        System.out.println("Price: " + price);
        System.out.println("		--------	");

    }

    public Accommodation() {

    }

    // An h hmera tou mhna einai diathesimi tote true alliws false
    public boolean IsDiathesimi(int monthIndex, int day) {
        boolean flag;
        if (this.months[monthIndex].isZero(day) == true) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public int getPrice() {
        return this.price;
    }

    public String getName() {
        return this.roommName;
    }

    public int getPersons() {
        return this.noOfPersons;
    }

    public String getArea() {
        return this.area;
    }

    public int getStars() {
        return this.stars;
    }

    public int getReviews() {
        return this.noOfReviews;
    }

    public String getManager() {
        return this.managerName;
    }

    public void setRoomName(String roomName) {
        this.roommName = roomName;
    }

    public void setPersons(int noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public void setManager(String managerName) {
        this.managerName = managerName;
    }

    public MonthCalendar[] getCalendar() {
        return months;
    }

   

    public boolean matchesFilters(Filters filters) {

        // Check if the accommodation's area matches the filter's area
        if (!this.area.equalsIgnoreCase(filters.getArea()))
            return false;

        // Check if the accommodation's number of persons is greater than or equal to
        // the filter's number_of_people
        if (this.noOfPersons < filters.getNumberOfPeople())
            return false;

        // Check if the accommodation's stars are within the filter's star range
        if (this.stars < filters.getStars())
            return false;

        // Check if the accommodation's price falls within the filter's price range
        if (this.getPrice() < filters.getLowestPrice() || this.getPrice() > filters.getMaxPrice())
            return false;

        // Check if the specified dates are available for reservation
        // 0 --> Iounios
        // 1 --> Ioulios
        // 2 --> Augoustos
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date filterStartDate = sdf.parse(filters.getStartDate());
            Date filterEndDate = sdf.parse(filters.getEndDate());
            int monthIndex = -1;
            for (Date date = filterStartDate; date.compareTo(filterEndDate) <= 0;) {
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int month = localDate.getMonthValue();
                if (month == 06 || month == 6) {
                    monthIndex = 0;
                }
                if (month == 07 || month == 7) {
                    monthIndex = 1;
                }
                if (month == 8) { // oxi 08 giati 08-->hexadical
                    monthIndex = 2;
                }
                int day = getDayFromDate(date);
                if (!IsDiathesimi(monthIndex, day)) {
                    System.out.println("Den einai diathesimi h mera!!");
                    return false; // Date is already booked
                } // end if
                date = incrementDate(date);
            }
        } catch (Exception e) {
            // Handle parsing exceptions
            e.printStackTrace();
            return false;
        }

        // If all conditions pass, return true
        System.out.println("Diathesimi mera");
        return true;
    }

    // Utility method to get the month index from a date
    private int getMonthIndexFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    // Utility method to get the day from a date
    private int getDayFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // Utility method to increment a date by one day
    private Date incrementDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if((calendar.get(Calendar.MONTH) == 6 && calendar.get(Calendar.DAY_OF_MONTH) == 30) || (calendar.get(Calendar.MONTH) == 7 && calendar.get(Calendar.DAY_OF_MONTH) == 30)) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        }
        else {
           calendar.add(Calendar.DAY_OF_MONTH, 1); 
        }

        return calendar.getTime();
    }

}
