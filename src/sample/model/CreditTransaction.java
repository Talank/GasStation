package sample.model;

import java.sql.Date;

public class CreditTransaction {
    private String companyName,vehicleNO;
    private float price,volume;
    private Date date;
    private char type;

    public CreditTransaction(String companyName, String vehicleNO, float price, float volume, Date date, char type) {
        this.companyName = companyName;
        this.vehicleNO = vehicleNO;
        this.price = price;
        this.volume = volume;
        this.date = date;
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVehicleNO() {
        return vehicleNO;
    }

    public void setVehicleNO(String vehicleNO) {
        this.vehicleNO = vehicleNO;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }
}
