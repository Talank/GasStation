package np.com.spirrotech.utils.nepali_date;


import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;


/**
 *Class to convert english date to nepali
 */
public class NepaliDateGenerator {


    //Reference english date
    public static final int START_ENGLISH_YEAR = 2018;
    public static final int START_ENGLISH_MONTH = 4;
    public static final int START_ENGLISH_DAY = 14;

    //Reference Nepali date (both above and this is the same date)
    public static final int START_NEPALI_YEAR = 2075;
    public static final int START_NEPALI_MONTH = 1;
    public static final int START_NEPALI_DAY = 1;


    private LocalDate startEnglishDate,currentEnglishDate,convertedNepaliDate;

    private NepaliDateFormat dateFormat;


    /**
     *Default constructor, converts the current english date to nepali date
     */
    public NepaliDateGenerator() {
        
        startEnglishDate=LocalDate.of(START_ENGLISH_YEAR,START_ENGLISH_MONTH,START_ENGLISH_DAY);
        currentEnglishDate=LocalDate.now();
        convertDate();

    }

    /**
     *This constructor takes the date to be converted as parameter, and converts the date to nepali date
     */
    
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
        int totalDays=0;
        for(int i=0;i<yearDiff;i++) {
            totalDays+=Year.of(startYear++).length();
        }

        totalDays+=currentEnglishDate.getDayOfYear()-startEnglishDate.getDayOfYear();

        return totalDays;
    }


    /**
     *This function converts the date
     * This Function first computes the total no of days
     */
    private void convertDate() {

        //nepali date datas
        int nYear=START_NEPALI_YEAR,nMonth=START_NEPALI_MONTH,nDay=START_NEPALI_DAY;

        //reference english year
        int startYear=startEnglishDate.getYear();

        //calculate the difference of years between the reference year and the given english date year
        int yearDiff=currentEnglishDate.getYear()-startYear;
        int totalDays=0;

        //compute the total no of days from the reference year to current year
        for(int i=0;i<yearDiff;i++) {

            //use java Year to compute the no of days in year (this also takes into account the leap year)
            totalDays+=Year.of(startYear++).length();
        }

        //also take into effect the offset of days, i.e in above we only take account perfect years, but the current date may not be day 1 of the year
        totalDays+=currentEnglishDate.getDayOfYear()-startEnglishDate.getDayOfYear();//gets the total no of days for the current date, i.e today may be day 200 or 300

        NepaliDate nepaliDate=new NepaliDate();

        //we now increment the starting nepali date through the day difference
        for(int i=0;i<totalDays;i++) {
            if(nDay<nepaliDate.getDaysOfYearMonth(nYear,nMonth)) {//check if the current day falls within the month of the specified year
                nDay++;
            } else { //the day doesn't fall within the month
                if(nMonth<12) {//increment the month if it is within 12
                    nDay=1;//reset the day
                    nMonth++;
                } else if(nMonth==12) {//increment the year
                    nYear++;
                    nMonth=1;//reset the month
                    nDay=1;
                }
            }
        }




        dateFormat=new NepaliDateFormat(nYear,nMonth,nDay,nepaliDate.getDaysOfYearMonth(nYear,nMonth),currentEnglishDate.getDayOfWeek().name());



    }

    public NepaliDateFormat getDateFormat() {
        return dateFormat;
    }

    @Override
    public String toString() {
        return dateFormat.toString();
    }


    public void test() {

    }
}
