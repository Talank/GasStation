package sample.model;

import javafx.scene.control.Label;
import sample.controller.AppHomeController;

public class CreditCompanyClosingBill {

    public CreditCompanyClosingBill() {

    }

    public boolean printBill() {
        Label label=new Label();

        if(PrintBill.print(label)) {
            return true;
        } else {
            AppHomeController.getInstance().showMessage("Error Printing Bill",1000);
        }
        return false;
    }

}
