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


        //the company details data from the task
        companyData = task.call();


        //create the root layout
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);

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
        mainCompany.getChildren().add(mainCompanyName);

        Label mainCompanyInfo = new Label("NOC Authorized Dealer");
        mainCompany.getChildren().add(mainCompanyInfo);

        Label mainCompanyAddress = new Label("Pokhara-14, Swagatnagar, Chauthe");
        mainCompany.getChildren().add(mainCompanyAddress);

        Label mainCompanyPhone = new Label("Phone: 501041");
        mainCompany.getChildren().add(mainCompanyPhone);

        mainLayout.getChildren().add(mainCompany);


        //node for info of the company for which this closing bill is printed

        HBox companyInfo = new HBox();
        companyInfo.setAlignment(Pos.CENTER_LEFT);

        Label companyName = new Label(model.getCompanyName());
        companyInfo.getChildren().add(companyName);

        mainLayout.getChildren().add(companyInfo);

        //main content holder (i.e transactions)
        GridPane mainContent = new GridPane();

        //specify the gaps
        mainContent.setVgap(10);
        mainContent.setHgap(10);


        //specify the top row columns names
        Label type = new Label("Type");
        mainContent.add(type, 0,0);

        Label vehicleNo = new Label("Vehicle No");
        mainContent.add(vehicleNo, 1, 0);

        Label rate = new Label("Rate");
        mainContent.add(rate, 2, 0);

        Label volume = new Label("Volume");
        mainContent.add(volume, 3, 0);

        Label price = new Label("Price");
        mainContent.add(price, 4, 0);

        Label date = new Label("Date");
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

        //compute the total size (i.e no of rows of the closing data)
        for (List<CreditDetailsModel> data : companyData) {
            totalSize += data.size();
        }

        if (totalSize != 0) {

            int pageCount = (int) Math.ceil(totalSize / 15);
            System.out.println("Page Count: " + pageCount);
            int count = 1;
            int page = 1;

            PriceFormat priceFormat = new PriceFormat();
            Precision precision = new Precision();

            Separator transactionSep;

            for (List<CreditDetailsModel> mainList : companyData) {

                for (CreditDetailsModel model : mainList) {
                    count++;//start from 2, as we have "column names in row 0, and separator in row 1"

                    //add the respective datas
                    mainContent.add(new Label(model.getItemName()), 0, count);
                    mainContent.add(new Label(model.getVehicleNo()), 1, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(Float.parseFloat(model.getRate())))), 2, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(Float.parseFloat(model.getVolume())))), 3, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(Float.parseFloat(model.getPrice())))), 4, count);
                    mainContent.add(new Label(model.getDate()), 5, count);

                    //this is the no of rows to print in single page, change this to fit the page
                    if (count >= 15) {
                        pageNo.setText("--" + String.valueOf(page) + "--");//the page number

                        if (!(new PrintBill()).printClosingBill(mainLayout)) {//return false if we failed to print the bill
                            return false;
                        }

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

                transactionSep = new Separator(Orientation.HORIZONTAL);
                GridPane.setConstraints(transactionSep, 0, count);
                GridPane.setColumnSpan(transactionSep, 6);
                mainContent.getChildren().add(transactionSep);


            }

            if (count != 1) {//means we have some remaining print to do

                if ((count + 2) <= 15) {//add the total amount to the last row, (+2 , one for the separator, and for the total row)


                    //the separator
                    count++;
                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, count);
                    GridPane.setColumnSpan(transactionSep, 6);
                    mainContent.getChildren().add(transactionSep);


                    count++;

                    mainContent.add(new Label("Due: Rs "), 0, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(task.getDue()))), 1, count);
                    mainContent.add(new Label("Total: Rs "), 2, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(task.getTotalPrice()))), 3, count);
                    mainContent.add(new Label("Grand Total: Rs"), 4, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(task.getGrandTotal()))), 5, count);


                    pageNo.setText("--" + String.valueOf(page) + "--");
                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                } else {

                    pageNo.setText("--" + String.valueOf(page) + "--");

                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                    mainContent.getChildren().clear();


                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, 0);
                    GridPane.setColumnSpan(transactionSep, 6);
                    mainContent.getChildren().add(transactionSep);

                    mainContent.add(new Label("Due: Rs "), 0, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(task.getDue()))), 1, count);
                    mainContent.add(new Label("Total: Rs "), 2, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(task.getTotalPrice()))), 3, count);
                    mainContent.add(new Label("Grand Total: Rs"), 4, count);
                    mainContent.add(new Label(priceFormat.format(precision.format(task.getGrandTotal()))), 5, count);

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

                mainContent.add(new Label("Due: Rs "), 0, count);
                mainContent.add(new Label(priceFormat.format(precision.format(task.getDue()))), 1, count);
                mainContent.add(new Label("Total: Rs "), 2, count);
                mainContent.add(new Label(priceFormat.format(precision.format(task.getTotalPrice()))), 3, count);
                mainContent.add(new Label("Grand Total: Rs"), 4, count);
                mainContent.add(new Label(priceFormat.format(precision.format(task.getGrandTotal()))), 5, count);

                pageNo.setText("--" + String.valueOf(page) + "--");

                if (!(new PrintBill()).printClosingBill(mainLayout)) {
                    return false;
                }
            }

        }


        return true;
    }

}