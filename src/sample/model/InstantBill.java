package sample.model;

public class InstantBill {
    private String name,price,volume,vehicleNo;
    private boolean isDiesel;

    public InstantBill(String name, String price, String volume, String vehicleNo, boolean isDiesel) {
        this.name = name;
        this.price = price;
        this.volume = volume;
        this.vehicleNo = vehicleNo;
        this.isDiesel = isDiesel;
    }



}
