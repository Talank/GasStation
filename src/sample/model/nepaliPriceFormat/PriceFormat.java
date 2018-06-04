package sample.model.nepaliPriceFormat;


public class PriceFormat {


    String result;
    int m=4;//Initially the comma position will be at 4 from reverse side.


    public PriceFormat(){

    }


    public void setFormat(String  result){
        int floatPos = result.indexOf(".") > -1 ? result.length() - result.indexOf(".") : 0;
       // int spacePos=  result.indexOf(" ") >-1 ? 1:0;
        int x=(result.indexOf("-")>-1?1:0);//In case the data is in - form
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
        return result;
    }

}
