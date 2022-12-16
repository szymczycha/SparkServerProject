import java.util.Date;
import java.util.Random;

public class CustomDate {
    private int day;
    private int month;
    private int year;

    public CustomDate(int year, int month, int day) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
    public CustomDate(){
        Random random = new Random();
        this.year = 1970 + random.nextInt(32);
        this.month = 1 + random.nextInt(12);
        this.day = 1 + random.nextInt(28);

    }
    public CustomDate(Date date){
        Random random = new Random();
        this.year = date.getYear();
        this.month = date.getMonth();
        this.day = date.getDay();

    }
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return year+"/"+month+"/"+day;
    }
}
