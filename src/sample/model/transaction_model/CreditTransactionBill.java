package sample.model.transaction_model;

import javafx.scene.control.Label;
import sample.model.PrintBill;

import java.util.ArrayList;
import java.util.List;

public class CreditTransactionBill {

    private List<Transaction> transactions;

    public CreditTransactionBill(List<Transaction> transactions) {
        this.transactions=transactions;
    }


    public void print() {
        StringBuilder builder=new StringBuilder();
        builder.append("----------Ohm Petrol Pump----------\n");
        builder.append(Transaction.getName()+"\n");
        builder.append("-----------------------------------\n");
        for (Transaction transaction:transactions) {
            if(transaction.getType().equals("Diesel")||transaction.getType().equals("Petrol")) {
                builder.append(transaction.getType()+" "+transaction.getVehicleNo()+" "+transaction.getVolume()+" "+transaction.getPrice()+"\n");
            } else {
                builder.append(transaction.getType()+" "+transaction.getQuantity()+" "+transaction.getRate()+" "+transaction.getPrice()+"\n");
            }
        }
        builder.append("-----------------------------------\n");
        builder.append("Total:Rs."+Transaction.getTotal()+"\n");

        Label label=new Label(builder.toString());
        PrintBill.print(label);
        Transaction.setTotal(0);

    }
}
