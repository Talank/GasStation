package sample.model;


import javafx.scene.control.Label;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;

public class InstantBill extends Bill {
    private String name;

    public InstantBill(String name, String price, String volume, String vehicleNo, boolean isDiesel) {
        super(price,volume,vehicleNo,isDiesel);
        this.name = name;
    }

    @Override
    public void printBill() {
        Label label=new Label();
        StringBuilder builder=new StringBuilder();
        String authorizedDealer = "NOC Authorized Dealer";
        String panNoAndPhone = "Pan no:300207255\t Tel:501041";

        builder.append(authorizedDealer);
        builder.append("Ohm Oil Pump\n");
        builder.append(panNoAndPhone);
        builder.append("Name:"+name+"\n");
        builder.append("Vehicle No:"+vehicleNo+"\n");
        builder.append("Oil Type:"+type+"\n");
        builder.append("Volume:"+volume+" Litres\n");
        builder.append("Price:Rs"+price);
        label.setText(builder.toString());
        if(PrintBill.print(label)) {
            SystemLogging.getLogger().fine("Bill Printed Successfully.");
        } else {
            SystemLogging.getLogger().warning("Error Printing Bill!");
            AppHomeController.getInstance().showMessage("Error Printing Bill",1000);
        }

    }

}
