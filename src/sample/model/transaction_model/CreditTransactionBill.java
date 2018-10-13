package sample.model.transaction_model;

import javafx.geometry.Pos;
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
        /*StringBuilder builder=new StringBuilder();
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
        Transaction.setTotal(0);*/

        newPrint();

    }

    public void newPrint() {

        //the root for the printing node
        VBox vBox = new VBox();

        //set the default alignment to top and left
        vBox.setAlignment(Pos.TOP_LEFT);

        //the vertical spacing between nodes
        vBox.setSpacing(10);

        //The hbox for the title of the bill
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(new Label("Shree Om Petroleum Traders"));
        vBox.getChildren().add(hBox);

        //Hbox for details
        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().add(new Label("Pokhara 14,Chauthe"));
        vBox.getChildren().add(hBox1);

        //add the labels to the VBox
        vBox.getChildren().add(new Label("Date: " + SystemStatus.getNepaliCurrentDate()));
        vBox.getChildren().add(new Label("Pan no : 300207299  ph : 501041"));
        vBox.getChildren().add(new Label("Company Name: " + Transaction.getName()));

        //the line separator
        Label separator = new Label("-----------------------------------------------------------------------------------------------");
        separator.setWrapText(false);//set the text wrap to false, so that only single line of separator is printed
        vBox.getChildren().add(separator);


        //using GridPane, as it automatically handles nodes layout and placement
        GridPane gridPane = new GridPane();

        //the horizontal and vertical gap
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        //add the headers and under line them
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


        //convert the prices to nepali format
        PriceFormat priceFormat=new PriceFormat();
        Precision precision = new Precision();

        int i = 1;//variable to help add the rows to the gridPane i.e rowIndex, start with 1 , as 0 is for the headers set in the above code
        for (Transaction transaction:transactions) {

            //add the data to the grid rows
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

        //add the gridPane to the root VBox
        vBox.getChildren().add(gridPane);

        Label separator1 = new Label("----------------------------------------------------------------------------------------------");
        separator1.setWrapText(false);
        vBox.getChildren().add(separator1);


        //add the line separator
        Label grandTotal = new Label("Total: " + priceFormat.format(precision.format(Transaction.getTotal())));
        grandTotal.setUnderline(true);
        vBox.getChildren().add(grandTotal);


        PrintBill printBill = new PrintBill();
        printBill.printTransactionBill(vBox);
        Transaction.setTotal(0);

    }

}
