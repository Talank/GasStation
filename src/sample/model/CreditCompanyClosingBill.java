package sample.model;

import javafx.scene.control.Label;
import sample.controller.AppHomeController;

public class CreditCompanyClosingBill {

    private DebtListModel model;

    public CreditCompanyClosingBill(DebtListModel model) {
        this.model=model;
    }

    public boolean printBill() {
        Label label=new Label();
        label.setText("Company Name:"+model.getCompanyName());

        if(PrintBill.print(label)) {
            return true;
        } else {
            AppHomeController.getInstance().showMessage("Error Printing Bill",1000);
        }
        return false;
    }

}
