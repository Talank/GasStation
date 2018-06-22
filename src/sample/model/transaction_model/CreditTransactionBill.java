package sample.model.transaction_model;

import javafx.scene.control.Label;
import sample.model.PrintBill;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;

public class CreditTransactionBill {

    private List<Transaction> transactions;
//    private Date date;

    public CreditTransactionBill(List<Transaction> transactions) {
        this.transactions=transactions;
    }


    public void print() {
        StringBuilder builder=new StringBuilder();
        builder.append("-------Shree Ohm Petrol Pump-------\n");
        builder.append("    Po-Le 7,Swagatnagar chauthe    \n");
//        builder.append(date.getDate()+"\n");
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
