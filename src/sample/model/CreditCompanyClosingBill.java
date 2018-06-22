package sample.model;

import javafx.scene.control.Label;
import sample.controller.AppHomeController;

import java.sql.Connection;

public class CreditCompanyClosingBill {
    private static Connection connection;

    private DebtListModel model;

    public CreditCompanyClosingBill(DebtListModel model) {
        this.model=model;
    }

    public boolean printBill() {
        String tableName= model.getCompanyName();

        Label label=new Label();
        //label.setText("Company Name:"+model.getCompanyName());


        StringBuilder builder=new StringBuilder();
        builder.append("Ohm Oil Pump\n");
        builder.append("Company Name:"+tableName+"\n");

        //Statement getResults=connection.createStatement();
        //ResultSet totalResult=getResults.executeQuery("SELECT TRANSACTION_ID,PRICE FROM "+tableName);


        label.setText(builder.toString());
        if(PrintBill.print(label)) {
            return true;
        } else {
            AppHomeController.getInstance().showMessage("Error Printing Bill",1000);
        }
        return false;
    }

}
