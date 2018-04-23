package sample.model;

public class CreditDetailsModel {
    private String vehicleNo,type,date,volume,price;

    public CreditDetailsModel(String vehicleNo, String type, String date, String volume, String price) {
        this.vehicleNo = vehicleNo;
        this.type = type;
        this.date = date;
        this.volume = volume;
        this.price = price;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
