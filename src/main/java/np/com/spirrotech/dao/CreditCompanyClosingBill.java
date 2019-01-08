package np.com.spirrotech.dao;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import np.com.spirrotech.generic_tasks.GetCompanyClosing;
import np.com.spirrotech.utils.PrintBill;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.application.settings.dialogs.print_conformation.PrintConformationController;
import np.com.spirrotech.utils.nepaliPriceFormat.Precision;
import np.com.spirrotech.utils.nepaliPriceFormat.PriceFormat;


import java.io.IOException;
import java.util.List;

public class CreditCompanyClosingBill {

    private final String TAG = "CreditCompanyClosing";

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
        Image image = new Image("images/logo.png");
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

        Label mainCompanyName = new Label("Shree Om Petroleum Traders");
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
        companyInfo.setAlignment(Pos.CENTER);
        Label company=new Label("Name: ");
        company.setFont(Font.font("Verdana", FontWeight.NORMAL,8.7));
        Label companyName = new Label(model.getCompanyName());
        companyName.setFont(Font.font("Verdana", FontWeight.BOLD,9.1));
        companyInfo.getChildren().add(company);
        companyInfo.getChildren().add(companyName);

        mainLayout.getChildren().add(companyInfo);

        //main content holder (i.e transactions)
        GridPane mainContent = new GridPane();

        mainContent.setAlignment(Pos.CENTER);

        //specify the gaps
        mainContent.setVgap(2);
        mainContent.setHgap(15);


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

        GridPane totalPane = new GridPane();
        totalPane.setVgap(5);
        totalPane.setHgap(15);
        totalPane.setAlignment(Pos.CENTER);
        mainLayout.getChildren().add(totalPane);

        Label pageNo = new Label();
        mainLayout.getChildren().add(pageNo);

        int totalSize = 0;
        int pageRows = 45; //no of rows to print in single page

        //compute the total size (i.e no of rows of the closing data)
        for (List<CreditDetailsModel> data : companyData) {
            totalSize += data.size();
        }
        SystemLogging.log("Size Of the records: " + totalSize);

        if (totalSize != 0) {

            int pageCount = (int) Math.ceil(totalSize / pageRows);
            System.out.println("Page Count: " + pageCount);
            int count = 1;
            int page = 1;
            double renderCount = 1;

            PriceFormat priceFormat = new PriceFormat();
            Precision precision = new Precision();

            Separator transactionSep;

            Label label;


            for (List<CreditDetailsModel> mainList : companyData) {

                for (CreditDetailsModel model : mainList) {
                    count++;//start from 2, as we have "column names in row 0, and separator in row 1"
                    renderCount++;//increment the render count by one, as this is a legit row

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
                    if (renderCount >= pageRows) {
                        pageNo.setText("--" + page + "--");//the page number

                        if (!(new PrintBill()).printClosingBill(mainLayout)) {//return false if we failed to print the bill
                            return false;
                        }

                        //increment the pageRows range, as we are not going to print company logos in the next page

                        pageRows =55;

                        //remove the company info
                        mainLayout.getChildren().remove(mainCompany);
                        mainLayout.getChildren().remove(companyInfo);
                        mainLayout.getChildren().remove(topImage);

                        //the page is full, reset the count and clear the gridPane
                        count = 1;
                        renderCount = 1;
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
                renderCount = renderCount + 0.35;//increment the render count by 0.35 as the separator only occupies about 0.35 of the total rows

                if (count != 2) {//check if the count is 1, i.e if it is 1, it is already drawn in above code, so skip it
                    transactionSep = new Separator(Orientation.HORIZONTAL);

                    GridPane.setConstraints(transactionSep, 0, count);
                    GridPane.setColumnSpan(transactionSep, 6);
                    mainContent.getChildren().add(transactionSep);
                }

            }

            if (count != 2) {//means we have some remaining print to do

                if ((renderCount + 3.5) <= pageRows) {//add the total amount to the last row, (+5 , 2 for the separator, and 1 for the total Amount row, 2 for page number), the rows fits into the same page


                    totalPane.getChildren().clear();


                    //the separator
                    count = 0;//this is the row of the totalPane, not the mainContent, so reset the count
                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, count);
                    GridPane.setColumnSpan(transactionSep, 6);
                    totalPane.getChildren().add(transactionSep);


                    count++;

                    Label due = new Label("Due: Rs ");
                    due.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(due, 0, count);

                    Label dueAmount = new Label(priceFormat.format(precision.format(task.getDue() - task.getPrepaid())));
                    dueAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(dueAmount, 1, count);

                    Label total = new Label("Total: Rs ");
                    total.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(total, 2, count);

                    Label totalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice())));
                    totalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(totalAmount, 3, count);

                    Label grandTotal = new Label("Grand Total: Rs");
                    grandTotal.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(grandTotal, 4, count);

                    Label grandTotalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice() + task.getDue() - task.getPrepaid())));
                    grandTotalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(grandTotalAmount, 5, count);

                    count++;

                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, count);
                    GridPane.setColumnSpan(transactionSep, 6);
                    totalPane.getChildren().add(transactionSep);



                    pageNo.setText("--" + page + "--");
                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                } else {//the rows doesn't fit into the same page, so extend to the next page, (it will fit in the next page)

                    //add the page value and print the current page
                    pageNo.setText("--" + page + "--");

                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                    //If the no of rows is less than the pageRows, and the rows and the totalAmount doesn't fit in the same page, then the second page would have image, but for other cases it might throw exception
                    try {
                        //remove the company info
                        mainLayout.getChildren().remove(mainCompany);
                        mainLayout.getChildren().remove(companyInfo);
                        mainLayout.getChildren().remove(topImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //clear the content for the next page
                    mainContent.getChildren().clear();

                    totalPane.getChildren().clear();

                    //reset the count
                    count = 1;


                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, 0);
                    GridPane.setColumnSpan(transactionSep, 6);
                    totalPane.getChildren().add(transactionSep);

                    Label due = new Label("Due: Rs ");
                    due.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(due, 0, count);

                    Label dueAmount = new Label(priceFormat.format(precision.format(task.getDue() - task.getPrepaid())));
                    dueAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(dueAmount, 1, count);

                    Label total = new Label("Total: Rs ");
                    total.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(total, 2, count);

                    Label totalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice())));
                    totalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(totalAmount, 3, count);

                    Label grandTotal = new Label("Grand Total: Rs");
                    grandTotal.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(grandTotal, 4, count);

                    Label grandTotalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice() + task.getDue() - task.getPrepaid())));
                    grandTotalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                    totalPane.add(grandTotalAmount, 5, count);


                    count++;

                    transactionSep = new Separator(Orientation.HORIZONTAL);
                    GridPane.setConstraints(transactionSep, 0, count);
                    GridPane.setColumnSpan(transactionSep, 6);
                    totalPane.getChildren().add(transactionSep);

                    pageNo.setText("--" + (page+1) + "--");


                    if (!(new PrintBill()).printClosingBill(mainLayout)) {
                        return false;
                    }

                }

            } else {//this means the data fit the previous page exactly


                //clear the mainContent as it contains the top labels
                mainContent.getChildren().clear();

                totalPane.getChildren().clear();


                //make count 1
                count = 1;

                transactionSep = new Separator(Orientation.HORIZONTAL);
                GridPane.setConstraints(transactionSep, 0, 0);
                GridPane.setColumnSpan(transactionSep, 6);
                totalPane.getChildren().add(transactionSep);

                Label due = new Label("Due: Rs ");
                due.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                totalPane.add(due, 0, count);

                Label dueAmount = new Label(priceFormat.format(precision.format(task.getDue() - task.getPrepaid())));//assign due - prepaid
                dueAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                totalPane.add(dueAmount, 1, count);

                Label total = new Label("Total: Rs ");
                total.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                totalPane.add(total, 2, count);

                Label totalAmount = new Label(priceFormat.format(precision.format(task.getTotalPrice())));
                totalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                totalPane.add(totalAmount, 3, count);

                Label grandTotal = new Label("Grand Total: Rs");
                grandTotal.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                totalPane.add(grandTotal, 4, count);

                Label grandTotalAmount = new Label(priceFormat.format(precision.format((task.getTotalPrice() + task.getDue() - task.getPrepaid()))));
                grandTotalAmount.setFont(Font.font("Verdana", FontWeight.BOLD,7.7));
                totalPane.add(grandTotalAmount, 5, count);

                count++;

                transactionSep = new Separator(Orientation.HORIZONTAL);
                GridPane.setConstraints(transactionSep, 0, count);
                GridPane.setColumnSpan(transactionSep, 6);
                totalPane.getChildren().add(transactionSep);


                pageNo.setText("--" + page + "--");

                if (!(new PrintBill()).printClosingBill(mainLayout)) {
                    return false;
                }
            }

        }

        try {
            //load the conformation dialog
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/print_conformation/printConformationDialog.fxml"));
            Parent root=loader.load();
            PrintConformationController controller=loader.getController();
            controller.initializeData(model, task.getPrepaid());
            Stage stage=new Stage();
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }



        //change to false for debugging, this doesn't delete the table data, for deployment change to true
        return true;
    }

}