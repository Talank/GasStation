package sample.model;

import java.sql.Date;

public class RefillModel {

    private Float expected,actual,loss;

    private String date;

    public RefillModel(Float expected, Float actual, Float loss, String date) {
        this.expected = expected;
        this.actual = actual;
        this.loss = loss;
        this.date = date;
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
}
