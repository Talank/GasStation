package sample.model;

import javafx.scene.control.Label;
import sample.OutputTasks.GetCompanyClosing;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;

import java.sql.Connection;
import java.sql.SQLException;

public class CreditCompanyClosingBill {
    private DebtListModel model;

    public CreditCompanyClosingBill(DebtListModel model) {
        this.model=model;
    }

    public boolean printBill() throws SQLException {
        String tableName= model.getCompanyName();
        Label label=new Label();

        StringBuilder builder=new StringBuilder();
        builder.append("Ohm Oil Pump\n");
        builder.append("Company Name: "+tableName+"\n\n");
       // builder.append("Transaction\t");
        builder.append("Type     \t");
        builder.append("Vehicle No. \t");
        builder.append("Rate     \t");
        builder.append("Volume   \t");
        builder.append("Price    \t");
        builder.append("Date\n");

        GetCompanyClosing companyClosing= new GetCompanyClosing(model);
        StringBuilder companyData = companyClosing.call();
        builder.append(companyData);

        builder.append("\nTotal : Rs."+companyClosing.getTotalPrice());

        label.setText(builder.toString());
        if(PrintBill.print(label)) {
            SystemLogging.getLogger().fine("Credit Company Successfully closed");
            return true;
        } else {
            SystemLogging.getLogger().fine("Credit Company failed to close");
            AppHomeController.getInstance().showMessage("Error Printing Bill",1000);
        }
        return false;
    }

}
