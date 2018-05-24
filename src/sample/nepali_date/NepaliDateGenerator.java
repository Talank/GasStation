package sample.nepali_date;


import sample.controller.SystemLogging;
import sun.rmi.server.LoaderHandler;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

public class NepaliDateGenerator {


    public static final int START_ENGLISH_YEAR = 2018;
    public static final int START_ENGLISH_MONTH = 4;
    public static final int START_ENGLISH_DAY = 14;

    public static final int START_NEPALI_YEAR = 2075;
    public static final int START_NEPALI_MONTH = 1;
    public static final int START_NEPALI_DAY = 1;


    private LocalDate startEnglishDate,currentEnglishDate,convertedNepaliDate;

    private NepaliDateFormat dateFormat;


    public NepaliDateGenerator() {
        startEnglishDate=LocalDate.of(START_ENGLISH_YEAR,START_ENGLISH_MONTH,START_ENGLISH_DAY);
        currentEnglishDate=LocalDate.now();
        convertDate();

    }

    public NepaliDateGenerator(LocalDate date) {
        startEnglishDate=LocalDate.of(START_ENGLISH_YEAR,START_ENGLISH_MONTH,START_ENGLISH_DAY);
        currentEnglishDate=date;
        convertDate();
    }

    public String convertDate(Date date) {
        currentEnglishDate=date.toLocalDate();
        convertDate();
        return dateFormat.toString();
    }

    public int getTotalDays(LocalDate currentEnglishDate) {
        int startYear=startEnglishDate.getYear();

        int yearDiff=currentEnglishDate.getYear()-startYear;
        System.out.println("Year Diff:"+yearDiff);
        int totalDays=0;
        for(int i=0;i<yearDiff;i++) {
            totalDays+=Year.of(startYear++).length();
        }

        totalDays+=currentEnglishDate.getDayOfYear()-startEnglishDate.getDayOfYear();

        return totalDays;
    }

    private void convertDate() {

        int nYear=START_NEPALI_YEAR,nMonth=START_NEPALI_MONTH,nDay=START_NEPALI_DAY;


        int startYear=startEnglishDate.getYear();

        int yearDiff=currentEnglishDate.getYear()-startYear;
        System.out.println("Year Diff:"+yearDiff);
        int totalDays=0;
        for(int i=0;i<yearDiff;i++) {
            totalDays+=Year.of(startYear++).length();
        }

        totalDays+=currentEnglishDate.getDayOfYear()-startEnglishDate.getDayOfYear();
        System.out.println("Total Days:"+totalDays);

        NepaliDate nepaliDate=new NepaliDate();

        for(int i=0;i<totalDays;i++) {
            if(nDay<nepaliDate.getDaysOfYearMonth(nYear,nMonth)) {
                nDay++;
            } else {
                if(nMonth<12) {
                    nDay=1;
                    nMonth++;
                } else if(nMonth==12) {
                    nYear++;
                    nMonth=1;
                    nDay=1;
                }
            }
        }

        /*convertedNepaliDate=LocalDate.of(nYear,nMonth,nDay);*/



        dateFormat=new NepaliDateFormat(nYear,nMonth,nDay,nepaliDate.getDaysOfYearMonth(nYear,nMonth),currentEnglishDate.getDayOfWeek().name());

        SystemLogging.log("Converted Nepali Date:"+dateFormat.toString());



    }

    public NepaliDateFormat getDateFormat() {
        return dateFormat;
    }

    @Override
    public String toString() {
        return dateFormat.toString();
    }


    /*public LocalDate getConvertedNepaliDate() {
        return convertedNepaliDate;
    }*/

    public void test() {

    }
}
