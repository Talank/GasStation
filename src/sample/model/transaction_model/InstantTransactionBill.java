package sample.model.transaction_model;

import javafx.scene.control.Label;
import sample.model.PrintBill;
import sample.model.nepaliPriceFormat.PriceFormat;


import java.util.List;

public class InstantTransactionBill {

    private List<Transaction> transactions;

    public InstantTransactionBill(List<Transaction> transactions) {
        this.transactions=transactions;
    }

    public void print() {
        StringBuilder builder=new StringBuilder();
        builder.append("----------Om Petrol Pump----------\n");
        builder.append("Customer - "+Transaction.getName()+"\n");
        builder.append("-----------------------------------\n");
        builder.append("Type     V.n     Qty     Rate     Total\n");
        builder.append("-----------------------------------\n");
        PriceFormat priceFormat=new PriceFormat();

        priceFormat.getResult();
        for (Transaction transaction:transactions) {
            priceFormat.setFormat(transaction.getPrice()+"");
            if(transaction.getType().equals("Diesel")||transaction.getType().equals("Petrol")) {
                builder.append(transaction.getType()+"   "+transaction.getVehicleNo()+"   "+transaction.getVolume()+"   "+transaction.getPrice()/transaction.getVolume()+"  "+priceFormat.getResult()+"\n");
            } else {
                builder.append(transaction.getType()+" "+ " xxxx    "+transaction.getQuantity()+"     "+transaction.getRate()+"  "+priceFormat.getResult()+"\n");
            }
        }
        builder.append("-----------------------------------\n");
        priceFormat.setFormat(Transaction.getTotal()+"");
        builder.append("Total:Rs."+priceFormat.getResult()+"\n");

        Label label=new Label(builder.toString());
        PrintBill.print(label);
        Transaction.setTotal(0);

    }


}
