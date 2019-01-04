package np.com.spirrotech.model;

import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class CreditDetailsModel {
    private SimpleStringProperty vehicleNo, itemName,date,volume,price,rate;

    private Date sqlDate;

    private int transactionID;

    public CreditDetailsModel(String vehicleNo, String itemName, String date, String volume, String price, String rate) {
        this.vehicleNo = new SimpleStringProperty(vehicleNo);
        this.itemName = new SimpleStringProperty(itemName);
        this.date = new SimpleStringProperty(date);
        this.volume = new SimpleStringProperty(volume);
        this.price = new SimpleStringProperty(price);
        this.rate= new SimpleStringProperty(rate);
    }

    public Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public SimpleStringProperty vehicleNoProperty() {
        return vehicleNo;
    }

    public SimpleStringProperty itemNameProperty() {
        return itemName;
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleStringProperty volumeProperty() {
        return volume;
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public SimpleStringProperty rateProperty() {
        return rate;
    }

    public String getVehicleNo() {
        return vehicleNo.get();
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo.set(vehicleNo);
    }

    public String getItemName() {
        return itemName.get();
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getVolume() {
        return volume.get();
    }

    public void setVolume(String volume) {
        this.volume.set(volume);
    }

    public String getPrice() {
        return price.get();
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public String getRate() {
        return rate.get();
    }

    public void setRate(String rate) {
        this.rate.set(rate);
    }
}
