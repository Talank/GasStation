package sample.model;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import sample.OutputTasks.GetCompanyClosing;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;

import javax.swing.*;
import java.sql.SQLException;

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
        imageView.setTranslateX(50);
        imageView.setTranslateY(-75);

        //imageView.preserveRatioProperty();

        /**Unicode of the string for bold,mini-word etc
         * font serif
         * https://yaytext.com/bold-italic/*/

        String PetrolPumpName = "𝐒𝐡𝐫𝐞𝐞 𝐎𝐡𝐦 𝐎𝐢𝐥 𝐏𝐮𝐦𝐩";

        String authorizedDealer = "NOC Authorized Dealer";

        StringBuilder builder=new StringBuilder();
        builder.append("\t\t\t"+authorizedDealer+"\n");
        builder.append("\t\t\t"+PetrolPumpName+"\n");
        builder.append("\t\t\tPokhara-14, Swagatnagar, Chauthe\n");
        builder.append("\t\t\tPhone: 501041\n\n");
        builder.append("Company Name: "+tableName+"\n\n");
        builder.append("Type     \t");
        builder.append("Vehicle No. \t");
        builder.append("Rate     \t");
        builder.append("Volume   \t");
        builder.append("Price    \t");
        builder.append("Date\n");

        GetCompanyClosing companyClosing= new GetCompanyClosing(model);
        StringBuilder companyData = companyClosing.call();
        builder.append(companyData);

        builder.append("\nTotal : Rs."+companyClosing.getTotalPrice());
        builder.append("\tDue : Rs."+companyClosing.getDue());
        builder.append("\nTotal : Rs."+companyClosing.getGrandTotal());


        label.setGraphic(imageView);
        label.setText(builder.toString());
        label.setStyle("-fx-font-size: 13; -fx-wrap-text:true;");
        label.setPadding(new Insets(5));
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        //label.setTextAlignment(TextAlignment.CENTER);
        if(PrintBill.print(label)) {
            SystemLogging.getLogger().fine("Credit Company Successfully closed");
            return true;
        } else {
            SystemLogging.getLogger().fine("Credit Company failed to close");
            AppHomeController.getInstance().showMessage("Error Printing Bill",1000);
        }
        return false;
    }

}