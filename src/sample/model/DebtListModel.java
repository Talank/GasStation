package sample.model;

public class DebtListModel {
    private String companyName,date,days;
    private int id;

    public DebtListModel(String companyName,String date,String days,int id) {
        this.companyName = companyName;
        this.date=date;
        this.days=days;
        this.id=id;
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
}
