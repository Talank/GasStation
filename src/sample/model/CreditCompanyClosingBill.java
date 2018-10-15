package sample.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import sample.OutputTasks.GetCompanyClosing;
import sample.OutputTasks.GetCompanyDetailsTask;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;
import sample.model.databases.DatabaseHandler;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.TaskManager;


import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import static javafx.geometry.Pos.TOP_LEFT;
import static javafx.scene.layout.StackPane.setAlignment;

public class CreditCompanyClosingBill {
    private DebtListModel model;
    private GetCompanyClosing task;

    private List<List<CreditDetailsModel>> companyData;

    public CreditCompanyClosingBill(DebtListModel model) {
        this.model=model;
    }

    public boolean printBill() {

        //Get the company Transaction details from database
        task=new GetCompanyClosing(model);


        //the company details data from the tasks
        companyData = task.call();


        //create the root layout
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(5);

        //top logo
        Image image = new Image("sample/resources/logo.png");
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);

        //node for the details of company at the top of the paper
        HBox topImage = new HBox();
        topImage.setAlignment(Pos.CENTER);
        topImage.getChildren().add(imageView);

        mainLayout.getChildren().add(topImage);

        VBox mainCompany = new VBox();
        mainCompany.setAlignment(Pos.CENTER);

        Label mainCompanyName = new Label("𝐒𝐡𝐫𝐞𝐞 𝐎𝐦 𝐏𝐞𝐭𝐫𝐨𝐥𝐞𝐮𝐦 𝐓𝐫𝐚𝐝𝐞𝐫𝐬");
        mainCompanyName.setFont(Font.font("Verdana", FontWeight.BOLD,9.15));

        mainCompany.getChildren().add(mainCompanyName);

        Label mainCompanyInfo = new Label("NOC Authorized Dealer");
        mainCompanyInfo.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
        mainCompany.getChildren().add(mainCompanyInfo);

        Label mainCompanyAddress = new Label("Pokhara-14, Swagatnagar, Chauthe");
        mainCompanyAddress.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
        mainCompany.getChildren().add(mainCompanyAddress);

        Label mainCompanyPhone = new Label("Phone: 501041");
        mainCompanyPhone.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));

        mainCompany.getChildren().add(mainCompanyPhone);

        mainLayout.getChildren().add(mainCompany);


        //node for info of the company for which this closing bill is printed

        HBox companyInfo = new HBox();
        companyInfo.setAlignment(Pos.CENTER_LEFT);

        Label companyName = new Label(model.getCompanyName());
        companyName.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
        companyInfo.getChildren().add(companyName);

        mainLayout.getChildren().add(companyInfo);

        //main content holder (i.e transactions)
        GridPane mainContent = new GridPane();

        //specify the gaps
        mainContent.setVgap(5);
        mainContent.setHgap(5);


        //specify the top row columns names
        Label type = new Label("Type");
        type.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));

        mainContent.add(type, 0,0);

        Label vehicleNo = new Label("V.N");
        vehicleNo.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
        mainContent.add(vehicleNo, 1, 0);

        Label rate = new Label("Rate");
        rate.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
        mainContent.add(rate, 2, 0);

        Label volume = new Label("Volume");
        volume.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
        mainContent.add(volume, 3, 0);

        Label price = new Label("Price");
        price.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
        mainContent.add(price, 4, 0);

        Label date = new Label("Date");
        date.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
        mainContent.add(date, 5, 0);

        //separator
        Separator horizontal = new Separator(Orientation.HORIZONTAL);

        GridPane.setConstraints(horizontal, 0, 1);
        GridPane.setColumnSpan(horizontal, 6);

        mainContent.getChildren().add(horizontal);

        mainLayout.getChildren().add(mainContent);

        Label pageNo = new Label();
        mainLayout.getChildren().add(pageNo);

        int totalSize = 0;
        int pageRows = 15; //no of rows to print in single page

        //compute the total size (i.e no of rows of the closing data)
        for (List<CreditDetailsModel> data : companyData) {
            totalSize += data.size();
        }

        if (totalSize != 0) {

            int pageCount = (int) Math.ceil(totalSize / pageRows);
            System.out.println("Page Count: " + pageCount);
            int count = 1;
            int page = 1;

            PriceFormat priceFormat = new PriceFormat();
            Precision precision = new Precision();

            Separator transactionSep;

            Label label;

            for (List<CreditDetailsModel> mainList : companyData) {

                for (CreditDetailsModel model : mainList) {
                    count++;//start from 2, as we have "column names in row 0, and separator in row 1"

                    //add the respective data

                    label = new Label(model.getItemName());
                    label.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
                    mainContent.add(label, 0, count);

                    label = new Label(model.getVehicleNo());
                    label.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
                    mainContent.add(label, 1, count);

                    label = new Label(priceFormat.format(precision.format(Float.parseFloat(model.getRate()))));
                    label.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
                    mainContent.add(label, 2, count);

                    label = new Label(priceFormat.format(precision.format(Float.parseFloat(model.getVolume()))));
                    label.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
                    mainContent.add(label, 3, count);

                    label = new Label(priceFormat.format(precision.format(Float.parseFloat(model.getPrice()))));
                    label.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
                    mainContent.add(label, 4, count);

                    label = new Label(model.getDate());
                    label.setFont(Font.font("Verdana", FontWeight.NORMAL,7.7));
                    mainContent.add(label, 5, count);

                    //this is the no of rows to print in single page, change this to fit the page
                    if (count >= pageRows) {
                        pageNo.setText("--" + String.valueOf(page) + "--");//the page number

                        if (!(new PrintBill()).printClosingBill(mainLayout)) {//return false if we failed to print the bill
                            return false;
                        }

                        //increment the pageRows range, as we are not going to print company logos in the next page

                        pageRows = pageRows + 10;

                        //remove the company info
                        mainLayout.getChildren().remove(mainCompany);
                        mainLayout.getChildren().remove(companyInfo);
                        mainLayout.getChildren().remove(topImage);

                        //the page is full, reset the count and clear the gridPane
                        count = 1;
                        mainContent.getChildren().clear();

                        mainContent.add(type, 0,0);
                        mainContent.add(vehicleNo, 1, 0);
                        mainContent.add(rate, 2, 0);
                        mainContent.add(volume, 3, 0);
                        mainContent.add(price, 4, 0);
                        mainContent.add(date, 5, 0);

                        GridPane.setConstraints(horizontal, 0, 1);
                        GridPane.setColumnSpan(horizontal, 6);

                        //add the row to the grid pane
                        mainContent.getChildren().add(horizontal);

                        page++;
                    }


                }

                count++;

                if (count != 2) {//check if the count is 2, i.e if it is 2, it is already drawn, so skip it
                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, count);
                    GridPane.setColumnSpan(transactionSep, 6);
                    mainContent.getChildren().add(transactionSep);
                }

            }

            if (count != 1) {//means we have some remaining print to do

                if ((count + 2) <= pageRows) {//add the total amount to the last row, (+2 , one for the separator, and for the total Amount row), the rows fits into the same page


                    //the separator
                    count++;
                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, count);
                    GridPane.setColumnSpan(transactionSep, 6);
                    mainContent.getChildren().add(transactionSep);


                    count++;

                    Label due = new Label("Due: Rs ");
                    due.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(due, 0, count);

                    Label dueAmount = new Label(priceFormat.format(precision.format(task.getDue())));
                    dueAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(dueAmount, 1, count);

                    Label total = new Label("Total: Rs ");
                    total.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(total, 2, count);

                    Label totalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice())));
                    totalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(totalAmount, 3, count);

                    Label grandTotal = new Label("Grand Total: Rs");
                    grandTotal.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(grandTotal, 4, count);

                    Label grandTotalAmount = new Label(priceFormat.format(precision.format(task.getGrandTotal())));
                    grandTotalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(grandTotalAmount, 5, count);

                    pageNo.setText("--" + String.valueOf(page) + "--");
                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                } else {//the rows doesn't fit into the same page, so extend to the next page, (it will fit in the next page)

                    //add the page value and print the current page
                    pageNo.setText("--" + String.valueOf(page) + "--");

                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                    //clear the content for the next page
                    mainContent.getChildren().clear();


                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, 0);
                    GridPane.setColumnSpan(transactionSep, 6);
                    mainContent.getChildren().add(transactionSep);

                    Label due = new Label("Due: Rs ");
                    due.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(due, 0, count);

                    Label dueAmount = new Label(priceFormat.format(precision.format(task.getDue())));
                    dueAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(dueAmount, 1, count);

                    Label total = new Label("Total: Rs ");
                    total.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(total, 2, count);

                    Label totalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice())));
                    totalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(totalAmount, 3, count);

                    Label grandTotal = new Label("Grand Total: Rs");
                    grandTotal.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(grandTotal, 4, count);

                    Label grandTotalAmount = new Label(priceFormat.format(precision.format(task.getGrandTotal())));
                    grandTotalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    mainContent.add(grandTotalAmount, 5, count);

                    pageNo.setText("--" + String.valueOf(page+1) + "--");


                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                }

            } else {
                transactionSep = new Separator(Orientation.HORIZONTAL);
                GridPane.setConstraints(transactionSep, 0, 0);
                GridPane.setColumnSpan(transactionSep, 6);
                mainContent.getChildren().add(transactionSep);

                Label due = new Label("Due: Rs ");
                due.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                mainContent.add(due, 0, count);

                Label dueAmount = new Label(priceFormat.format(precision.format(task.getDue())));
                dueAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                mainContent.add(dueAmount, 1, count);

                Label total = new Label("Total: Rs ");
                total.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                mainContent.add(total, 2, count);

                Label totalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice())));
                totalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                mainContent.add(totalAmount, 3, count);

                Label grandTotal = new Label("Grand Total: Rs");
                grandTotal.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                mainContent.add(grandTotal, 4, count);

                Label grandTotalAmount = new Label(priceFormat.format(precision.format(task.getGrandTotal())));
                grandTotalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                mainContent.add(grandTotalAmount, 5, count);


                pageNo.setText("--" + String.valueOf(page) + "--");

                if (!(new PrintBill()).printClosingBill(mainLayout)) {
                    return false;
                }
            }

        }


        //change to false for debugging, this doesn't delete the table data, for deployment change to true
        return false;
    }

}