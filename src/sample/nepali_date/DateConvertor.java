package sample.nepali_date;

public class DateConvertor {

    public static final int START_ENGLISH_YEAR = 2018;
    public static final int START_ENGLISH_MONTH = 4;
    public static final int START_ENGLISH_DAY = 14;

    public static final int START_NEPALI_YEAR = 2075;
    public static final int START_NEPALI_MONTH = 1;
    public static final int START_NEPALI_DAY = 1;


    public static int getTotalDays(int nYear,int nMonth,int nDay) {

        NepaliDate date=new NepaliDate();

        int sYear=START_NEPALI_YEAR,sMonth=START_NEPALI_MONTH,sDay=START_NEPALI_DAY;

        int totalDays=0;

        while(true) {
            if(nYear==sYear&&sMonth==nMonth&&sDay==nDay) {
                break;
            }

            if(sDay<date.getDaysOfYearMonth(sYear,sMonth)) {
                sDay++;
            } else if(sMonth<12) {
                sDay=1;
                sMonth++;
            } else if(sMonth==12) {
                sYear++;
                sMonth=1;
                sDay=1;
            }
            totalDays++;
        }

        return totalDays;

    }

}
