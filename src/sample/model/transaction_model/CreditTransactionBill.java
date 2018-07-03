package sample.model.transaction_model;

import javafx.scene.control.Label;
import sample.model.PrintBill;
import sample.model.nepaliPriceFormat.PriceFormat;

import java.text.DecimalFormat;
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
        builder.append("-------Shree Om Petrol Pump-------\n");
        builder.append("    Po-Le 7,Swagatnagar,chauthe    \n");
//        builder.append(date.getDate()+"\n");
        builder.append("Company Name - "+Transaction.getName()+"\n");
        builder.append("-----------------------------------------\n");
        builder.append("Type     V.n           Qty       Rate      Total\n");
        builder.append("-----------------------------------------\n");
        PriceFormat priceFormat=new PriceFormat();

        priceFormat.getResult();
        for (Transaction transaction:transactions) {
            priceFormat.setFormat(transaction.getPrice()+"");

            if(transaction.getType().equals("Diesel")||transaction.getType().equals("Petrol")) {
                String data= String.format("%s   %s    %8s  %8s    %8s",transaction.getType(),transaction.getVehicleNo(),new DecimalFormat("##.##").format(transaction.getVolume()),new DecimalFormat("##.##").format(transaction.getPrice()/transaction.getVolume()),priceFormat.getResult()+"\n");
                builder.append(data);
                } else {
                String data=String.format("%s  %s    %8s  %8s    %8s",transaction.getType(),"xxxx",new DecimalFormat("##.##").format(transaction.getQuantity()),new DecimalFormat("##.##").format(transaction.getRate()),priceFormat.getResult()+"\n");
                builder.append(data);
                        }
        }
        builder.append("----------------------------------------\n");
        priceFormat.setFormat(Transaction.getTotal()+"");
        builder.append(String.format("%50s","Total:Rs."+priceFormat.getResult()+"\n"));

        Label label=new Label(builder.toString());
        PrintBill.print(label);
        Transaction.setTotal(0);

    }
}
