package sample.model;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import sample.OutputTasks.GetCompanyClosing;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;


import java.sql.SQLException;
import java.text.DecimalFormat;

import static javafx.geometry.Pos.TOP_LEFT;
import static javafx.scene.layout.StackPane.setAlignment;

public class CreditCompanyClosingBill {
    private DebtListModel model;

    public CreditCompanyClosingBill(DebtListModel model) {
        this.model=model;
    }

    public boolean printBill() throws SQLException {
//        Printer printer = Printer.getDefaultPrinter();
//        Paper a4 = Paper.A4;
//        PageLayout pageLayout = printer.createPageLayout(a4, PageOrientation.REVERSE_PORTRAIT, Printer.MarginType.DEFAULT);

        String tableName= model.getCompanyName();
        Label label=new Label();

        /** For displaying logo */
        Image image = new Image("sample/resources/logo.png");
//        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
//        imageView.setTranslateX(50);
//        imageView.setTranslateY(-50);


        /**Unicode of the string for bold,mini-word etc
         * font serif
         * https://yaytext.com/bold-italic/*/
        String PetrolPumpName="𝐒𝐡𝐫𝐞𝐞 𝐎𝐦 𝐏𝐞𝐭𝐫𝐨𝐥𝐞𝐮𝐦 𝐓𝐫𝐚𝐝𝐞𝐫𝐬";

        String authorizedDealer = "NOC Authorized Dealer";

        StringBuilder builder=new StringBuilder();
        builder.append("\t\t\t\t\t\t\t\t"+authorizedDealer+"\n");
        builder.append("\t\t\t\t\t\t\t\t"+PetrolPumpName+"\n");
        builder.append("\t\t\t\t\t\t\t\tPokhara-14, Swagatnagar, Chauthe");
        builder.append("\tPhone: 501041\n\n");
        builder.append("Company Name: "+tableName+"\n\n");
        builder.append("Type     \t\t");
        builder.append("Vehicle No. \t\t");
        builder.append("Rate     \t\t");
        builder.append("Volume   \t\t");
        builder.append("Price    \t\t");
        builder.append("Date\n");

        GetCompanyClosing companyClosing= new GetCompanyClosing(model);
        StringBuilder companyData = companyClosing.call();
        builder.append(companyData);

        builder.append("\nTotal : Rs."+new DecimalFormat("##.##").format(Double.parseDouble(companyClosing.getTotalPrice())));
        builder.append("\tDue : Rs."+new DecimalFormat("##.##").format(Double.parseDouble(companyClosing.getDue())));
        builder.append("\nGrand Total : Rs."+new DecimalFormat("##.##").format(Double.parseDouble(companyClosing.getGrandTotal())));


        label.setGraphic(imageView);
        label.setContentDisplay(ContentDisplay.LEFT);
        label.setAlignment(Pos.TOP_CENTER);
        label.setPadding(new Insets(0,100,100,0));

        //label.setContentDisplay(ContentDisplay.LEFT);
        label.setText(builder.toString());
        label.setContentDisplay(ContentDisplay.TOP);
        label.setPadding(new Insets(5));
        //label.setAlignment(Pos.TOP_LEFT);

        label.setStyle("-fx-font-size: 8; -fx-wrap-text:true;");
        //label.setPadding(new Insets(5));
        label.setAlignment(Pos.CENTER_LEFT);
        label.setMaxWidth(Double.MAX_VALUE);
        //label.setTextAlignment(TextAlignment.CENTER);

        PrintBill printBill = new PrintBill();

        if(printBill.printClosingBill(label)) {
            SystemLogging.getLogger().fine("Credit Company Successfully closed");
            return true;
        } else {
            SystemLogging.getLogger().fine("Credit Company failed to close");
            Platform.runLater(()->{
                AppHomeController.getInstance().showMessage("Error Printing Bill",1000);
            });
        }
        return false;
    }

}