package sample.model;

import java.sql.Date;
import java.util.ArrayList;

public class RefillModel {

    private static ArrayList<TankerModel> tankerModels=new ArrayList<>();

    private Float expected,actual,loss;
    private TankerModel tankerModel;
    private boolean isAvailable=false;

    private String date;

    public RefillModel(Float expected, Float actual, String date, TankerModel tankerModel) {
        this.expected = expected;
        this.actual = actual;
        this.loss = expected-actual;
        this.date = date;
        checkAndAddTanker(tankerModel);
    }

    public void checkAndAddTanker(TankerModel tankerModel) {

        for(TankerModel model:tankerModels) {

            if((model.getTankerNumber()).substring(0,4).equals(tankerModel.getTankerNumber().substring(0,4))) {
                isAvailable=true;
                this.tankerModel=model;
                break;
            }

        }

        if(!isAvailable) {
            tankerModels.add(tankerModel);
            this.tankerModel=tankerModel;
        }

    }

    public Float getExpected() {
        return expected;
    }

    public String getExpectedString() {
        return String.valueOf(expected);
    }

    public void setExpected(Float expected) {
        this.expected = expected;
    }

    public Float getActual() {
        return actual;
    }

    public String getActualString() {
        return String.valueOf(actual);
    }

    public void setActual(Float actual) {
        this.actual = actual;
    }

    public Float getLoss() {
        return loss;
    }

    public String getLossString() {
        return String.valueOf(loss);
    }

    public void setLoss(Float loss) {
        this.loss = loss;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TankerModel getTankerModel() {
        return tankerModel;
    }

    public void setTankerModel(TankerModel tankerModel) {
        this.tankerModel = tankerModel;
    }

    public String getTankerNumber() {
        return tankerModel.getTankerNumber();
    }

    public void setTankerNumber(String tankerNumber) {
        this.tankerModel.setTankerNumber(tankerNumber);
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
