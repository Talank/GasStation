package np.com.spirrotech.utils.nepaliPriceFormat;

/**
 We have used two functions for priceFormat in different Scenario
 setFormat and format
 */
public class PriceFormat {


    private String result;
    private boolean negative=false;//default negative position if false
    private int m=4;//Initially the comma position will be at 4 from reverse  side.


    public PriceFormat(){

    }

    public PriceFormat(String result) {
        setFormat(result);
    }

    public String format(String result) {
        int x=(result.indexOf("-")>-1?1:0);//In case the data is in - form
        boolean negativeSign=false;//default negative position is encountered as false
        if(x==1) {
            result = result.substring(1);
            negativeSign=true;
        }

        int floatPos = result.indexOf(".") > -1 ? result.length() - result.indexOf(".") : 0;
        // int spacePos=  result.indexOf(" ") >-1 ? 1:0;
        int nGroups= (result.length()-floatPos-1-x)/2;

        for(int i=0; i<nGroups; i++){

            int commaPos = result.length() - i * m - 3 - floatPos;
            if(commaPos>0) {
                result = result.substring(0, commaPos) + "," + result.substring(commaPos);
            }
            m=3;// the comma will now run in two-two steps
        }

        if(negativeSign) {
            return "-" + result;

        }


        return result;
    }


    public void setFormat(String  result){
        int x=(result.indexOf("-")>-1?1:0);//In case the data is in - form

        if(x==1) {
            result = result.substring(1);
            negative=true;
        }
        int floatPos = result.indexOf(".") > -1 ? result.length() - result.indexOf(".") : 0;
       // int spacePos=  result.indexOf(" ") >-1 ? 1:0;

        int nGroups= (result.length()-floatPos-1-x)/2;

        for(int i=0; i<nGroups; i++){

            int commaPos = result.length() - i * m - 3 - floatPos;
            if(commaPos>0) {
                result = result.substring(0, commaPos) + "," + result.substring(commaPos);
            }
            m=3;// the comma will now run in two-two steps
        }
        this.result=result;
    }

    public String getResult(){

        if(negative)
            return "-"+result;
        return result;
    }

}
