package sample.model;

import java.sql.Date;

public class RefillModel {

    private Float expected,actual,loss;
    private String tankerNumber;

    private String date;

    public RefillModel(Float expected, Float actual, String date, String  tankerNumber) {
        this.expected = expected;
        this.actual = actual;
        this.loss = expected-actual;
        this.date = date;
        this.tankerNumber = tankerNumber;
    }

    public Float getExpected() {
        return expected;
    }

    public void setExpected(Float expected) {
        this.expected = expected;
    }

    public Float getActual() {
        return actual;
    }

    public void setActual(Float actual) {
        this.actual = actual;
    }

    public Float getLoss() {
        return loss;
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

    public String getTankerNumber() {
        return tankerNumber;
    }

    public void setTankerNumber(String tankerNumber) {
        this.tankerNumber = tankerNumber;
    }
}
