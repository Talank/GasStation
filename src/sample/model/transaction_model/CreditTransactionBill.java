package sample.model.transaction_model;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import sample.model.PrintBill;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.nepali_date.NepaliDateGenerator;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        Label label=new Label();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        NepaliDateGenerator generator = new NepaliDateGenerator(localDate);
        String details="𝗣𝗼𝗸𝗵𝗮𝗿𝗮 𝟭𝟰,𝗦𝘄𝗮𝗴𝗮𝘁𝗻𝗮𝗴𝗮𝗿,𝗖𝗵𝗮𝘂𝘁𝗵𝗲 𝗣𝗵 : 𝟱𝟬𝟭𝟬𝟰𝟭";
        builder.append("-----------𝗦𝗵𝗿𝗲𝗲 𝗢𝗺 𝗣𝗲𝘁𝗿𝗼𝗹 𝗣𝘂𝗺𝗽------------\n");
        builder.append("   "+details+"\n");
        builder.append("Pan no : 300207299   , "+  "Date :"+ generator.toString() +" \n");
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
        label.setFont(new Font("Arial",13));
        PrintBill.print(label);
        Transaction.setTotal(0);

    }
}
