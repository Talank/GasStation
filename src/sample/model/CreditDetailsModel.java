package sample.model;

public class CreditDetailsModel {
    private String vehicleNo, itemName,date,volume,price,rate;

    public CreditDetailsModel(String vehicleNo, String itemName, String date, String volume, String price, String rate) {
        this.vehicleNo = vehicleNo;
        this.itemName = itemName;
        this.date = date;
        this.volume = volume;
        this.price = price;
        this.rate=rate;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
