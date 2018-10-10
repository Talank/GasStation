package sample.model.transaction_model;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import sample.model.PrintBill;
import sample.model.SystemStatus;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.nepali_date.NepaliDateGenerator;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;

public class CreditTransactionBill {

    private List<Transaction> transactions;
    private Date postingDate = null;

    public CreditTransactionBill(List<Transaction> transactions) {
        this.transactions=transactions;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }


    public void print() {
        StringBuilder builder=new StringBuilder();
        Label label=new Label();
        String details="Pokhara 14,Chauthe";
        builder.append("Shree Om Petroleum Traders.\n");
        builder.append(details+"\n");

        if (postingDate != null) {//posting date is provided, so use that date instead
            NepaliDateGenerator generator = new NepaliDateGenerator(postingDate.toLocalDate());
            builder.append("Date :"+ generator.toString() +" \n");
        } else {//posting date is null, meaning the posting is not done, so use default nepali date
            builder.append("Date :"+ SystemStatus.getNepaliCurrentDate() +" \n");
        }


        builder.append("Pan no : 300207299  ph : 501041\n");
        builder.append("Company Name - "+Transaction.getName()+"\n");
        builder.append("---------------------------------------------------\n");
        builder.append("Type     V.n           Qty       Rate      Total\n");
        builder.append("---------------------------------------------------\n");
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
        builder.append("---------------------------------------------------\n");
        priceFormat.setFormat(Transaction.getTotal()+"");
        builder.append(String.format("%50s","Total:Rs."+priceFormat.getResult()+"\n"));

        label.setText(builder.toString());
        label.setFont(new Font("Arial",7));
        PrintBill.print(label);
        Transaction.setTotal(0);

    }
}
