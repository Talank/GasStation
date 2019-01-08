package np.com.spirrotech.application.home.menu;


import com.jfoenix.controls.JFXRippler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import np.com.spirrotech.application.home.AppHomeController;


import np.com.spirrotech.application.home.dialogs.LoginDialogController;
import np.com.spirrotech.dao.CreditCompanyList;


import javafx.scene.input.MouseEvent;


import java.io.IOException;


public class MainHomeController {


    /*
    * Main Screen For The Application
    *
    * Acts as a routing Class to route to different paths
    *
    * */
    private static MainHomeController instance;//Make Singleton Static np.com.spirrotech.init.ApplicationPreLoader.Reference


    @FXML
    private GridPane rootGrid;


    @FXML
    private VBox instantBillingLayout;

    @FXML
    private VBox creditBillingLayout;

    @FXML
    private VBox creditLayout;

    @FXML
    private VBox profitLossLayout;

    @FXML
    private VBox dailyAnalyticsLayout;

    @FXML
    private VBox settingsLayout;




    @FXML
    private TextField rDiesel,rPetrol;//References for the current diesel and petrol rate

    public MainHomeController() {
        instance=this;//Initialize the reference
    }


    /*
    * Get the current rates of the oil from system status table
    * Set the rates to the respective text fields
    *Also set the current rate to system status class
    * */
    @FXML
    public void initialize() {

        rootGrid.getColumnConstraints().get(0).setPercentWidth(50);
        rootGrid.getColumnConstraints().get(1).setPercentWidth(50);

        JFXRippler instantRippler=new JFXRippler(instantBillingLayout);
        rootGrid.add(instantRippler,0,0);
        GridPane.setMargin(instantRippler,new Insets(5,5,5,5));

        JFXRippler creditRippler=new JFXRippler(creditBillingLayout);
        rootGrid.add(creditRippler,1,0);
        GridPane.setMargin(creditRippler,new Insets(5,5,5,0));

        JFXRippler debtRippler=new JFXRippler(creditLayout);
        rootGrid.add(debtRippler,0,1);
        GridPane.setMargin(debtRippler,new Insets(0,5,5,5));

        JFXRippler profitRippler=new JFXRippler(profitLossLayout);
        rootGrid.add(profitRippler,1,1);
        GridPane.setMargin(profitRippler,new Insets(0,5,5,0));

        JFXRippler dailyRippler=new JFXRippler(dailyAnalyticsLayout);
        rootGrid.add(dailyRippler,0,2);
        GridPane.setMargin(dailyRippler,new Insets(0,5,5,5));

        JFXRippler settingsRippler=new JFXRippler(settingsLayout);
        rootGrid.add(settingsRippler,1,2);
        GridPane.setMargin(settingsRippler,new Insets(0,5,5,0));


    }



    /*
    * Load the Instant Billing View
    * */

    @FXML
    public void loadInstantBilling(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(), "application/instant_billing/instantBilling.fxml");
    }


    /*
    * Load the Credit Billing View
    * Check if the Company Table is empty
    * */
    @FXML
    public void loadCreditBilling(MouseEvent e) throws IOException {
        if (CreditCompanyList.getCompanyList().size() != 0) {
            loadStage((Node)e.getSource(), "application/credit_billing/creditBilling.fxml");
        } else {
            AppHomeController.getInstance().showMessage("No Companies Detected.",2000);
        }
    }

    /*
    * Load the Debt View
    * */


    @FXML
    public void loadDebt(MouseEvent e) throws IOException {

        loadStage((Node)e.getSource(), "application/credit_companies/companies_list/debt.fxml");
    }

    /*
    * Load the Settings View
    *
    * */

    @FXML
    public void loadSettings(MouseEvent e) throws IOException {

        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/home/dialogs/loginDialog.fxml"));
        Parent root=loader.load();
        LoginDialogController controller=loader.getController();
        controller.setStatus(1);
        controller.setStage((Stage)((Node)e.getSource()).getScene().getWindow());
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void loadSystemStatus(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(), "application/system_status/systemStatus.fxml");
    }

    /*
    * Load the daily analytics view
    * */

    @FXML
    public void loadDailyAnalytics(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(), "application/daily_analytics/detail_analytics/dailyTotalAnalytics.fxml");
    }

    /*
    * Load the profit loss view
    * */

    @FXML
    public void loadProfitLoss(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(), "application/profit_loss/profitLoss.fxml");

    }

    /*
    * Called From External Classes To change the rates
    * */


    public void showSettings(Stage stage) throws IOException {
        Parent root=FXMLLoader.load(getClass().getClassLoader().getResource("application/settings/settings.fxml"));
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("sample/css/mainStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }



    /*
    * Get the current instance of the class
    * */

    public static MainHomeController getInstance() {
        return instance;
    }


    /*
    * Helper Function to load the stages
    *
    * */

    private void loadStage(Node node,String path) throws IOException {
        BorderPane borderPane=FXMLLoader.load(getClass().getClassLoader().getResource(path));
        AppHomeController.getInstance().loadStages(borderPane);
    }



}
