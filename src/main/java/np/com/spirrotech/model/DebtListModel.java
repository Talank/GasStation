package np.com.spirrotech.model;

import java.sql.Date;

public class DebtListModel {
    private String companyName,date,days,prevExpireDate,phone;
    private int transactionID,prevTransactionID;

    private Date expireDate;

    private float due,total,prePaid;

    private boolean isExpired=false;

    public DebtListModel(String companyName,String days,int transactionID,int prevTransactionID,float due,float total,String phone) {
        this.companyName = companyName;
        this.days=days;
        this.transactionID=transactionID;
        this.prevTransactionID=prevTransactionID;
        this.due=due;
        this.total=total;
        this.phone=phone;
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

    public int getPrevTransactionID() {
        return prevTransactionID;
    }

    public void setPrevTransactionID(int prevTransactionID) {
        this.prevTransactionID = prevTransactionID;
    }

    public float getDue() {
        return due;
    }

    public void setDue(float due) {
        this.due = due;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getPrevExpireDate() {
        return prevExpireDate;
    }

    public void setPrevExpireDate(String prevExpireDate) {
        this.prevExpireDate = prevExpireDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public float getPrePaid() {
        return prePaid;
    }

    public void setPrePaid(float prePaid) {
        this.prePaid = prePaid;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }

    public String getPhone() {
        return phone;
    }
}
