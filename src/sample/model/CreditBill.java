package sample.model;

import javafx.scene.control.Label;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;
import sample.model.databases.DatabaseHandler;

import java.sql.Date;

public class CreditBill extends Bill {

    private String companyName;

    public CreditBill(String companyName, String price, String volume, String vehicleNo, boolean isDiesel) {
        super(price,volume,vehicleNo,isDiesel);
        this.companyName = companyName;
    }

    @Override
    public void printBill() {

        storeTransaction();

        javafx.scene.control.Label label=new Label();
        StringBuilder builder=new StringBuilder();
        builder.append("Ohm Oil Pump\n");
        builder.append("Company Name:"+companyName+"\n");
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

    private void storeTransaction() {
        char type;
        if(isDiesel)
            type='1';
        else
            type='0';
      /*  CreditTransaction creditTransaction=new CreditTransaction(companyName,vehicleNo,Float.parseFloat(price),Float.parseFloat(volume),new Date((new java.util.Date()).getTime()),type);
        DatabaseHandler.storeCreditTransaction(creditTransaction);*/
    }

}
