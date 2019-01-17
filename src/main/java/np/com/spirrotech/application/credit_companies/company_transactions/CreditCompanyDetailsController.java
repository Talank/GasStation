package np.com.spirrotech.application.credit_companies.company_transactions;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import np.com.spirrotech.application.home.AppHomeController;
import np.com.spirrotech.library.conformation_dialog.ConformationDialogController;
import np.com.spirrotech.library.output_task_loading.OutputTaskLoading;
import np.com.spirrotech.library.toolbar.BackButtonInterface;
import np.com.spirrotech.library.toolbar.ToolBar;
import np.com.spirrotech.dao.CreditDetailsModel;
import np.com.spirrotech.model.DeleteTransactionTask;
import np.com.spirrotech.model.GetCompanyDetailsTask;
import np.com.spirrotech.utils.databases.DatabaseHandler;
import np.com.spirrotech.utils.nepaliPriceFormat.Precision;
import np.com.spirrotech.utils.nepaliPriceFormat.PriceFormat;
import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.utils.tasks.Tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreditCompanyDetailsController implements BackButtonInterface {

    @FXML
    private ToolBar toolBar;


    @FXML
    private OutputTaskLoading progressLayout;

    @FXML
    private TableView<CreditDetailsModel> detailsTableView;

    @FXML
    private TableColumn<CreditDetailsModel,String> vehicleNo;

    @FXML
    private TableColumn<CreditDetailsModel,String> itemName;

    @FXML
    private TableColumn<CreditDetailsModel,String> rate;

    @FXML
    private TableColumn<CreditDetailsModel,String> quantity;

    @FXML
    private TableColumn<CreditDetailsModel,String> price;

    @FXML
    private TableColumn<CreditDetailsModel,Integer> sn;

    @FXML
    private TableColumn<CreditDetailsModel,String> date;


    private GetCompanyDetailsTask task;

    private PriceFormat priceFormat;
    private Precision precision;

    private String companyName;

    //The list to hold the data of the company transactions

    private List<List<CreditDetailsModel>> companyDetails;


    @FXML
    public void initialize() {

        task=null;

        toolBar.setTitle("Company Transactions");
        toolBar.setBackPath("application/credit_companies/companies_list/debt.fxml");
        toolBar.addBackButtonInterface(this);

        progressLayout.setVisible(true);
        detailsTableView.setVisible(false);

        precision = new Precision();
        priceFormat = new PriceFormat();

        detailsTableView.setEditable(true);
        detailsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Callback<TableColumn<CreditDetailsModel,String>, TableCell<CreditDetailsModel,String>> cellFactory = (s -> {
            return new CustomTableCell();
        });

        vehicleNo.setCellFactory(cellFactory);
        itemName.setCellFactory(cellFactory);
        rate.setCellFactory(cellFactory);
        quantity.setCellFactory(cellFactory);
        price.setCellFactory(cellFactory);

        //make the index auto increment
        sn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(detailsTableView.getItems().indexOf(column.getValue()) + 1));

        vehicleNo.setCellValueFactory(new PropertyValueFactory<>("vehicleNo"));
   //     vehicleNo.setStyle("-fx-alignment: CENTER;");
        itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
    //    itemName.setStyle("-fx-alignment: CENTER;");
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));

     //   rate.setStyle("-fx-alignment: CENTER;");

        quantity.setCellValueFactory(new PropertyValueFactory<>("volume"));

    //    quantity.setStyle("-fx-alignment: CENTER;");

        price.setCellValueFactory(new PropertyValueFactory<>("price"));


        /*price.setCellFactory(new Callback<TableColumn<CreditDetailsModel, String>, TableCell<CreditDetailsModel, String>>() {
            @Override
            public TableCell<CreditDetailsModel, String> call(TableColumn<CreditDetailsModel, String> param) {
                return new TableCell<CreditDetailsModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }

                    }
                };
            }
        });*/
       // price.setStyle("-fx-alignment: CENTER;");

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        date.setCellFactory(cellFactory);
     //   date.setStyle("-fx-alignment: CENTER;");


    }

    @Override
    public void cleanUp() {
        if(task!=null)
            //cancel the task
            task.setCancelled(true);
    }

    @FXML
    void deleteTransaction(MouseEvent event) throws IOException {

        //check if at least one item is selected
        if (detailsTableView.getSelectionModel().getSelectedItems().size() != 0) {
            //show the conformation dialog
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("library/conformation_dialog/conformationDialog.fxml"));
            Parent root= loader.load();
            ConformationDialogController controller = loader.getController();
            controller.setTitle("Delete Conformation");
            controller.setConformationLabel("The Transactions will be deleted?");
            ArrayList<CreditDetailsModel> models = new ArrayList<>();
            //add currently selected transactions to the array list for deletion
            models.addAll(detailsTableView.getSelectionModel().getSelectedItems());

            Tasks task = new DeleteTransactionTask(models, companyName);

            //add listener to listen for task completion
            task.addTaskInterfaceListener(new TaskInterface() {
                @Override
                public void onComplete(Object data) {

                    Platform.runLater(() -> {
                        //remove the selected items from the table view
                        detailsTableView.getItems().removeAll(detailsTableView.getSelectionModel().getSelectedItems());
                    });

                }

                @Override
                public void onError(String msg) {

                }

                @Override
                public void updateProgress(double current, double total) {

                }
            });

            //set the task to be performed on conformation
            controller.setTask(task);

            Stage stage=new Stage();
            stage.setTitle("Conformation");
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            AppHomeController.getInstance().showMessage("No Transaction Selected!!",1000);
        }



    }

    public void setParameters(String companyName) {
        this.companyName = companyName;

        toolBar.setTitle(companyName + " Transactions");

        task=new GetCompanyDetailsTask(DatabaseHandler.generateTableName(companyName));

        task.addTaskInterfaceListener(new TaskInterface() {
            @Override
            public void onComplete(Object data) {
                Platform.runLater(() -> {
                    //store the data received from the database, we need this data in order to detect the changes when the field is edited
                    companyDetails = (List<List<CreditDetailsModel>>) data;

                    CustomTableCell.setCompanyDetails(companyDetails);

                    CustomTableCell.setCompanyName(companyName);

                    //loop through the data and set the data to the columns of the tables
                    for(List<CreditDetailsModel> model: companyDetails) {
                        detailsTableView.getItems().addAll(model);
                    }

                    //hide the progress layout
                    progressLayout.setVisible(false);

                    //show the table view
                    detailsTableView.setVisible(true);
                });
            }

            @Override
            public void onError(String message) {

            }

            @Override
            public void updateProgress(double current, double total) {

                Platform.runLater(() -> {
                    //set the progress of the operation
                    progressLayout.setProgress(current / total);
                });

            }
        });

        TaskManager.getInstance().executeInstantTask(task);

    }
}
