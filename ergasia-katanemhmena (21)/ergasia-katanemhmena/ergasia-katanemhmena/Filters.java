import java.io.Serializable;

public class Filters implements Serializable {

    String area;
    String endDate;
    String startDate;
    int number_of_people;
    int lowest_price;
    int max_price;
    int stars;

    public Filters() {
    }

    public Filters(String area, String endDate, String startDate, int number_of_people, int lowest_price, int max_price,
            int stars) {

        this.area = area;
        this.endDate = endDate;
        this.startDate = startDate;
        this.number_of_people = number_of_people;
        this.lowest_price = lowest_price;
        this.max_price = max_price;
        this.stars = stars;
    }

    public String getArea() {
        return this.area;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public int getNumberOfPeople() {
        return this.number_of_people;
    }

    public int getLowestPrice() {
        return this.lowest_price;
    }

    public int getMaxPrice() {
        return this.max_price;
    }

    public int getStars() {
        return this.stars;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setLowestPrice(int lowest_price) {
        this.lowest_price = lowest_price;
    }

    public void setMaxPrice(int max_price) {
        this.max_price = max_price;
    }

    public void setNumberOfPeople(int number_of_people) {
        this.number_of_people = number_of_people;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

}
