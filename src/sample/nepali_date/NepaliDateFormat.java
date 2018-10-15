package sample.nepali_date;


/**
 *This class converts the numeric date representation to text based representation
 */
public class NepaliDateFormat {


    private String formattedDate;

    private int nYear,nMonth,nDay,totalDaysOfMonth;

    public static final String BAISHAK="Baishak";
    public static final String JESTHA="Jestha";
    public static final String ASHAR="Ashar";
    public static final String SHRAWAN="Shrawan";
    public static final String BHADRA="Bhadra";
    public static final String ASHOJ="Ashoj";
    public static final String KARTIK="Kartik";
    public static final String MANGSIR="Mangsir";
    public static final String POUSH="Poush";
    public static final String MAGH="Magh";
    public static final String FALGUN="Falgun";
    public static final String CHAITRA="Chaitra";




    /**
     *Convert the given date into text format
     */
    public NepaliDateFormat(int nYear, int nMonth, int nDay,int totalDaysOfMonth,String dayOfWeek) {

        this.nYear=nYear;
        this.nMonth=nMonth;
        this.nDay=nDay;
        this.totalDaysOfMonth=totalDaysOfMonth;

        formattedDate=dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase() +",";

        switch (nMonth) {

            case 1:
                formattedDate+=BAISHAK;
                break;

            case 2:
                formattedDate+=JESTHA;
                break;

            case 3:
                formattedDate+=ASHAR;
                break;

            case 4:
                formattedDate+=SHRAWAN;
                break;

            case 5:
                formattedDate+=BHADRA;
                break;

            case 6:
                formattedDate+=ASHOJ;
                break;

            case 7:
                formattedDate+=KARTIK;
                break;

            case 8:
                formattedDate+=MANGSIR;
                break;

            case 9:
                formattedDate+=POUSH;
                break;

            case 10:
                formattedDate+=MAGH;
                break;

            case 11:
                formattedDate+=FALGUN;
                break;

            case 12:
                formattedDate+=CHAITRA;
                break;

        }

        formattedDate+=" "+nDay+","+nYear;

    }

    public int getnYear() {
        return nYear;
    }


    public int getnMonth() {
        return nMonth;
    }

    public int getnDay() {
        return nDay;
    }

    public int getTotalDaysOfMonth() {
        return totalDaysOfMonth;
    }


    @Override
    public String toString() {
        return formattedDate;
    }
}
