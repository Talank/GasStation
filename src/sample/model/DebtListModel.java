package sample.model;

public class DebtListModel {
    private String companyName,date,days;
    private int id,transactionID;

    private boolean isExpired=false;

    public DebtListModel(String companyName,String date,String days,int id,int transactionID) {
        this.companyName = companyName;
        this.date=date;
        this.days=days;
        this.id=id;
        this.transactionID=transactionID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
}
