package sample.model.transaction_model;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import sample.model.PrintBill;
import sample.model.SystemStatus;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.nepali_date.NepaliDateGenerator;
import sun.util.calendar.BaseCalendar;


import javax.xml.soap.Text;
import java.awt.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class InstantTransactionBill {

    private List<Transaction> transactions;

    public InstantTransactionBill(List<Transaction> transactions) {
        this.transactions=transactions;
    }

    public void print() {
        /*Label label=new Label();

        StringBuilder builder=new StringBuilder();
        String details="Pokhara 14,Chauthe";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");


        LocalDate localDate = LocalDate.now();
        NepaliDateGenerator generator = new NepaliDateGenerator(localDate);
        builder.append("Shree Om Petroleum Traders.\n");
        //builder.append("-----------𝗦𝗵𝗿𝗲𝗲 𝗢𝗺 𝗣𝗲𝘁𝗿𝗼𝗹 𝗣𝘂𝗺𝗽------------\n");
        builder.append(details+"\n");
        builder.append("Date :"+ generator.toString() +" \n");
        builder.append("Pan no : 300207299  ph : 501041\n");

        builder.append("Customer - "+Transaction.getName()+"\n");
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
                //builder.append(transaction.getType()+"   "+transaction.getVehicleNo()+"   "+ String.format("%-2s",new DecimalFormat("##.##").format(transaction.getVolume()))+"   "+String.format("%s",new DecimalFormat("##.###").format(transaction.getPrice()/transaction.getVolume()))+"  "+String.format("%2s",priceFormat.getResult())+"\n");
            } else {
                String data=String.format("%s  %s    %8s   %8s    %8s",transaction.getType(),"xxxx",new DecimalFormat("##.##").format(transaction.getQuantity()),new DecimalFormat("##.##").format(transaction.getRate()),priceFormat.getResult()+"\n");
                builder.append(data);
//                builder.append(transaction.getType()+" "+ " xxxx    "+  String.format("%-2s",new DecimalFormat("##.##").format(transaction.getQuantity()))
//                +"     "+String.format("%s",new DecimalFormat("##.##").format(transaction.getRate()))+"  "+ String.format("%2s",priceFormat.getResult())+"\n");
            }
        }
        builder.append("---------------------------------------------------\n");
        priceFormat.setFormat(Transaction.getTotal()+"");
        builder.append(String.format("%50s","Total:Rs."+priceFormat.getResult()+"\n"));
        label.setContentDisplay(ContentDisplay.LEFT);

        label.setText(builder.toString());

        //label.setPrefSize(250, 250);
        //it adds ... to uncovered portion of label

        //label.setAlignment(Pos.TOP_LEFT);
        //label.setPadding(new Insets(0,0,0,-10));
//        label.setStyle("-fx-padding: 0 0 0 0;");
        //label.setStyle("-fx-margin: null null null 0;");

//        FontMetrics metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(label.getFont());
//        label.setPadding(new Insets(-metrics.getDescent(), 0, 0, 0));

        //label.setTranslateX(-50);
        //label.setStyle("-fx-margin-left: -50;");
        //label.setPadding(new Insets(0,0,0,-50));
        label.setFont(new Font("Arial",7));
        PrintBill.print(label);
        Transaction.setTotal(0);*/

        newPrint();


    }

    public void newPrint() {


        //for comments refer to CreditTransactionBill.java

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setSpacing(10);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(new Label("Shree Om Petroleum Traders"));
        vBox.getChildren().add(hBox);

        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().add(new Label("Pokhara 14,Chauthe"));
        vBox.getChildren().add(hBox1);

        vBox.getChildren().add(new Label("Date: " + SystemStatus.getNepaliCurrentDate()));
        vBox.getChildren().add(new Label("Pan no : 300207299  ph : 501041"));
        vBox.getChildren().add(new Label("Customer Name: " + Transaction.getName()));

        Label separator = new Label("-----------------------------------------------------------------------------------------------");
        separator.setWrapText(false);
        vBox.getChildren().add(separator);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label type = new Label("Type");
        type.setUnderline(true);
        gridPane.add(type, 0, 0);

        Label vN = new Label("V.N");
        vN.setUnderline(true);
        gridPane.add(vN, 1,0);

        Label qty = new Label("Qty");
        qty.setUnderline(true);
        gridPane.add(qty, 2, 0);

        Label rate = new Label("Rate");
        rate.setUnderline(true);
        gridPane.add(rate, 3, 0);

        Label total = new Label("Total");
        total.setUnderline(true);
        gridPane.add(total, 4, 0);


        PriceFormat priceFormat=new PriceFormat();
        Precision precision = new Precision();

        int i = 1;
        for (Transaction transaction:transactions) {

            if(transaction.getType().equals("Diesel")||transaction.getType().equals("Petrol")) {

                gridPane.add(new Label(transaction.getType()), 0, i);
                gridPane.add(new Label(transaction.getVehicleNo()), 1, i);
                gridPane.add(new Label(priceFormat.format(precision.format(transaction.getVolume()))), 2, i);
                gridPane.add(new Label(priceFormat.format(precision.format(transaction.getRate()))), 3, i);
                gridPane.add(new Label(priceFormat.format(precision.format(transaction.getPrice()))), 4, i);

            } else {
                gridPane.add(new Label(transaction.getType()), 0, i);
                gridPane.add(new Label("XXXX"), 1, i);
                gridPane.add(new Label(priceFormat.format(precision.format(transaction.getQuantity()))), 2, i);
                gridPane.add(new Label(priceFormat.format(precision.format(transaction.getRate()))), 3, i);
                gridPane.add(new Label(priceFormat.format(precision.format(transaction.getPrice()))), 4, i);

            }
            i++;
        }
        vBox.getChildren().add(gridPane);

        Label separator1 = new Label("----------------------------------------------------------------------------------------------");
        separator1.setWrapText(false);
        vBox.getChildren().add(separator1);

        Label grandTotal = new Label("Total: " + priceFormat.format(precision.format(Transaction.getTotal())));
        grandTotal.setUnderline(true);
        vBox.getChildren().add(grandTotal);


        PrintBill.print(vBox);
        Transaction.setTotal(0);
    }


}
