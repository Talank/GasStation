package sample.nepali_date;

import java.time.Instant;

public class ParseNepaliDate {

    private String nepaliDate,dayOfWeek;

    private int nYear=-1,nMonth=-1,nDay=-1;

    public ParseNepaliDate() {

    }

    public ParseNepaliDate(String nepaliDate) {
        this.nepaliDate = nepaliDate;
        parse();
    }

    private void parse() {

        int firstIndex=nepaliDate.indexOf(',');

        dayOfWeek=nepaliDate.substring(0,firstIndex);


        nepaliDate=nepaliDate.substring(firstIndex+1);

        firstIndex=nepaliDate.indexOf(' ');

        String month=nepaliDate.substring(0,firstIndex);


        if(month.equalsIgnoreCase(NepaliDateFormat.BAISHAK)) {
            nMonth=1;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.JESTHA)) {
            nMonth=2;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.ASHAR)) {
            nMonth=3;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.SHRAWAN)) {
            nMonth=4;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.BHADRA)) {
            nMonth=5;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.ASHOJ)) {
            nMonth=6;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.KARTIK)) {
            nMonth=7;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.MANGSIR)) {
            nMonth=8;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.POUSH)) {
            nMonth=9;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.MAGH)) {
            nMonth=10;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.FALGUN)) {
            nMonth=11;
        } else if(month.equalsIgnoreCase(NepaliDateFormat.CHAITRA)) {
            nMonth=12;
        }


        nepaliDate=nepaliDate.substring(firstIndex+1);

        firstIndex=nepaliDate.indexOf(',');

        nDay=Integer.parseInt(nepaliDate.substring(0,firstIndex));

        nepaliDate=nepaliDate.substring(firstIndex+1);

        nYear=Integer.parseInt(nepaliDate);


    }

    public String getNepaliDate() {
        return nepaliDate;
    }

    public void setNepaliDate(String nepaliDate) {
        this.nepaliDate = nepaliDate;
        parse();
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getnYear() {
        return nYear;
    }

    public void setnYear(int nYear) {
        this.nYear = nYear;
    }

    public int getnMonth() {
        return nMonth;
    }

    public void setnMonth(int nMonth) {
        this.nMonth = nMonth;
    }

    public int getnDay() {
        return nDay;
    }

    public void setnDay(int nDay) {
        this.nDay = nDay;
    }

    public void test() {

    }
}
