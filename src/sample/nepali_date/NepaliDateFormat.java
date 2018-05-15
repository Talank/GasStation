package sample.nepali_date;

public class NepaliDateFormat {


    private String formattedDate;

    private int nYear,nMonth,nDay;

    public static final String BAISHAK="BHADRA";
    public static final String JESTHA="JESTHA";
    public static final String ASHAR="ASHAR";
    public static final String SHRAWAN="SHRAWAN";
    public static final String BHADRA="BHADRA";
    public static final String ASHOJ="ASHOJ";
    public static final String KARTIK="KARTIK";
    public static final String MANGSIR="MANGSIR";
    public static final String POUSH="POUSH";
    public static final String MAGH="MAGH";
    public static final String FALGUN="FALGUN";
    public static final String CHAITRA="CHAITRA";





    public NepaliDateFormat(int nYear, int nMonth, int nDay,String dayOfWeek) {

        this.nYear=nYear;
        this.nMonth=nMonth;
        this.nDay=nDay;

        formattedDate=dayOfWeek+",";

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


    @Override
    public String toString() {
        return formattedDate;
    }
}
