package sample.model;

import java.sql.Date;

public class RefillModel {

    private String expected,loss,actual;
    private Date date;

    public RefillModel(String expected, String loss, String actual, Date date) {
        this.expected = expected;
        this.loss = loss;
        this.actual = actual;
        this.date = date;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getLoss() {
        return loss;
    }

    public void setLoss(String loss) {
        this.loss = loss;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
