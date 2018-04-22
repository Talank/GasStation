package sample.model;

public class Bill {
    protected String price,volume,vehicleNo,type;
    protected boolean isDiesel;

    public Bill(String price, String volume, String vehicleNo, boolean isDiesel) {
        this.price = price;
        this.volume = volume;
        this.vehicleNo = vehicleNo;
        this.isDiesel = isDiesel;
        if(isDiesel)
            type="Diesel";
        else
            type="Petrol";
    }
}
