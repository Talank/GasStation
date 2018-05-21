package sample.model;

public class DebtListModel {
    private String companyName,date,days;
    private int id,transactionID,expireID,prevExpireID,prevTransactionID;

    private boolean isExpired=false;

    public DebtListModel(String companyName,String date,String days,int id,int transactionID,int expireID,int prevExpireID,int prevTransactionID) {
        this.companyName = companyName;
        this.date=date;
        this.days=days;
        this.id=id;
        this.transactionID=transactionID;
        this.expireID=expireID;
        this.prevExpireID=prevExpireID;
        this.prevTransactionID=prevTransactionID;
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

    public int getExpireID() {
        return expireID;
    }

    public void setExpireID(int expireID) {
        this.expireID = expireID;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public int getPrevExpireID() {
        return prevExpireID;
    }

    public void setPrevExpireID(int prevExpireID) {
        this.prevExpireID = prevExpireID;
    }

    public int getPrevTransactionID() {
        return prevTransactionID;
    }

    public void setPrevTransactionID(int prevTransactionID) {
        this.prevTransactionID = prevTransactionID;
    }
}
