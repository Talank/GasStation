package sample.controller.main_home_controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.AppHomeController;
import sample.controller.dialog_controller.LoginDialogController;
import sample.controller.main_home_controller.lists_controllers.TransactionListCell;
import sample.library.toolbar.ToolBar;

import sample.model.CreditCompanyList;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.model.transaction_model.CreditTransactionBill;
import sample.model.transaction_model.Transaction;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.StoreCreditTransactionTask;

import java.awt.*;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/*
* This Class is similar to Instant Billing Controller
*
* Refer Instant Billing Controller for comments
*
*
* */

public class CreditBillingController {

    private boolean calcNowV =false,calcNowP=false,calcOldV=false,calcOldP=false, listSelected =false,force=false;
    private DecimalFormat decimalFormat;
    private float rate;
    private PriceFormat priceFormat;

    @FXML
    private JFXButton itemsCount,adminButton;

    @FXML
    private JFXRadioButton diesel,petrol,extra;

    @FXML
    public ToolBar toolBar;

    @FXML
    private JFXTextField volume,price,vehicleNumber,itemName,quantity,rates;

    @FXML
    private TextField total;

    @FXML
    private VBox extraBill,dieselPetrol;

    @FXML
    private JFXComboBox<String> companyName;

    @FXML
    private ToggleGroup selection;

    @FXML
    private BorderPane root;

    private JFXSnackbar snackBar;

    @FXML
    private ListView<Transaction> transactionListView;

    private ObservableList<Transaction> listModels;

    private ArrayList<Transaction> arrayList;

    private int mode=0;

    public static CreditBillingController instance;

    public boolean admin=false;

    public float dRate,pRate;

    public Date date;



    @FXML
    public void initialize() {
        instance=this;

        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.setTitle("Credit Billing");

        snackBar =new JFXSnackbar(root);

        diesel.setSelected(true);
        rate=SystemStatus.getdRate();

        transactionListView.setCellFactory(lv->new TransactionListCell());
        transactionListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        arrayList=new ArrayList<>();
        listModels=FXCollections.observableList(arrayList);

        transactionListView.setItems(listModels);

        transactionListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Transaction> observable, Transaction oldValue, Transaction newValue)->{
            if(newValue!=null) {
                if(newValue.getType().equals("Diesel")||newValue.getType().equals("Petrol")) {

                    vehicleNumber.setText(newValue.getVehicleNo());

                    priceFormat=new PriceFormat(String.valueOf(newValue.getPrice()));
                    price.setText(priceFormat.getResult());
                    volume.setText(String.valueOf(newValue.getVolume()));
                    if(newValue.getType().equals("Diesel")) {
                        if(selection.getSelectedToggle()!=diesel) {
                            listSelected =true;
                        }
                        diesel.setSelected(true);
                    } else {
                        if(selection.getSelectedToggle()!=petrol)
                            listSelected =true;
                        petrol.setSelected(true);
                    }

                } else {
                    if(selection.getSelectedToggle()!=extra)
                        listSelected =true;
                    itemName.setText(newValue.getType());
                    rates.setText(String.valueOf(newValue.getRate()));
                    quantity.setText(String.valueOf(newValue.getQuantity()));
                    extra.setSelected(true);
                }

            }

        });


        selection.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov,Toggle old_toggle, Toggle new_toggle)->{
            if(((JFXRadioButton)new_toggle).getText().equals("Extra")) {

                dieselPetrol.setVisible(false);
                extraBill.setVisible(true);
                mode=2;
            } else {
                extraBill.setVisible(false);
                dieselPetrol.setVisible(true);

                if(((JFXRadioButton)new_toggle).getText().equals("Diesel")) {

                    if(admin) {
                        rate=dRate;
                    } else {
                        rate=SystemStatus.getdRate();
                    }
                    mode=0;
                } else {
                    if(admin) {
                        rate=pRate;
                    } else {
                        rate=SystemStatus.getpRate();
                    }
                    mode=1;
                }

            }

            if(isListSelected()) {
                if(!listSelected) {
                    transactionListView.getSelectionModel().clearSelection();
                    clearOil();
                    clearExtra();
                }
                listSelected=false;
            } else {
                if(calcOldV) {
                    if(!volume.getText().isEmpty())
                        price.setText(decimalFormat.format(Float.parseFloat(volume.getText())*rate));
                } else if(calcOldP) {
                    if(!price.getText().isEmpty())
                        volume.setText(decimalFormat.format(Float.parseFloat(price.getText())/rate));
                }
            }

        });

        decimalFormat=new DecimalFormat("#.####");

        companyName.setItems(FXCollections.observableList(CreditCompanyList.getCompanyList()));
        companyName.setValue(CreditCompanyList.getCompanyList().get(0));

        initializeOil();
        initializeExtra();

    }

    private void initializeExtra() {


        RequiredFieldValidator itemNameValidator=new RequiredFieldValidator();
        itemNameValidator.setMessage("Item Name Cannot Be Empty");
        itemName.getValidators().add(itemNameValidator);

        RequiredFieldValidator quantityValidator=new RequiredFieldValidator();
        quantityValidator.setMessage("Quantity Cannot Be Empty");
        quantity.getValidators().add(quantityValidator);

        RequiredFieldValidator ratesValidator=new RequiredFieldValidator();
        ratesValidator.setMessage("Rate Cannot Be Empty");
        rates.getValidators().add(ratesValidator);

        itemName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[a-zA-Z ]*")) {
                itemName.setText(oldValue);
            } else {
                if(newValue.isEmpty()&&!force) {
                    itemName.validate();
                } else {
                    itemName.resetValidation();
                }
            }
        });

        quantity.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,5}")) {
                quantity.setText(oldValue);
            } else {
                if(newValue.isEmpty()&&!force) {
                    quantity.validate();
                } else {
                    quantity.resetValidation();
                    if(force) {
                        force=false;
                    }
                }
            }
        });

        rates.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                rates.setText(oldValue);
            } else {
                if(newValue.isEmpty()&&!force) {
                    rates.validate();
                } else {
                    rates.resetValidation();
                }
            }
        });

    }


    private void initializeOil() {

        RequiredFieldValidator volumeValidator=new RequiredFieldValidator();
        volumeValidator.setMessage("Volume Cannot Be Empty");
        volume.getValidators().add(volumeValidator);

        RequiredFieldValidator priceValidator=new RequiredFieldValidator();
        priceValidator.setMessage("Price Cannot Be Empty");
        price.getValidators().add(priceValidator);

        RequiredFieldValidator vehicleNumberValidator=new RequiredFieldValidator();
        vehicleNumberValidator.setMessage("Vehicle Number Cannot Be Empty");
        vehicleNumber.getValidators().add(vehicleNumberValidator);


        vehicleNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.isEmpty()&&!force)
                vehicleNumber.validate();
            else {
                vehicleNumber.resetValidation();
            }

        });

        volume.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?"))
                volume.setText(oldValue);
            else
            if(newValue.isEmpty()&&!force)
                volume.validate();
            else {
                volume.resetValidation();
                if(calcNowV) {
                    price.setText(decimalFormat.format(Float.parseFloat(volume.getText())*rate));
                }

            }

        });

        volume.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcOldV=true;
            calcNowP=false;
            calcNowV=newPropertyValue;
        });


        price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?"))
                price.setText(oldValue);
            else
            if(newValue.isEmpty()&&!force)
                price.validate();
            else {
                price.resetValidation();
                if(calcNowP) {
                    volume.setText(decimalFormat.format(Float.parseFloat(price.getText())/rate));
                }

                if(force) {
                    force=false;
                }
            }

        });

        price.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcOldP=true;
            calcOldV=false;
            calcNowP=newPropertyValue;
        });
    }

    private boolean isListSelected() {
        return !transactionListView.getSelectionModel().isEmpty();
    }


    private void clearOil() {
        force=true;
        vehicleNumber.setText("");
        volume.setText("");
        price.setText("");
    }

    private void clearExtra() {
        force=true;
        itemName.setText("");
        rates.setText("");
        quantity.setText("");
    }

    private void masterClear() {
        force=true;
        total.setText("0.0");
        vehicleNumber.setText("");
        volume.setText("");
        itemName.setText("");
        rates.setText("");
        quantity.setText("");
        price.setText("");
        itemsCount.setText("0");

        vehicleNumber.resetValidation();
        volume.resetValidation();
        itemName.resetValidation();
        rates.resetValidation();
        quantity.resetValidation();
        price.resetValidation();


    }

    private int checkOilInput() {
        int status=0;

        if(companyName.getValue()==null)
            status++;

        if(price.getText().trim().isEmpty()) {
            price.validate();
            status++;
        }

        if(volume.getText().trim().isEmpty()) {
            volume.validate();
            status++;
        }

        if(vehicleNumber.getText().trim().isEmpty()) {
            vehicleNumber.validate();
            status++;
        }
        return status;
    }

    private int checkExtraInput() {
        int status=0;

        if(companyName.getValue()==null)
            status++;

        if(rates.getText().trim().isEmpty()) {
            rates.validate();
            status++;
        }

        if(quantity.getText().trim().isEmpty()) {
            quantity.validate();
            status++;
        }

        if(itemName.getText().trim().isEmpty()) {
            itemName.validate();
            status++;
        }

        return status;
    }

    private boolean isListEmpty() {
        return transactionListView.getItems().isEmpty();
    }

    public static CreditBillingController getInstance() {
        return instance;
    }

    public void setAdmin(float dRate,float pRate,Date date) {
        transactionListView.getItems().clear();
        masterClear();
        this.dRate=dRate;
        this.pRate=pRate;
        this.date=date;
        admin=true;
        if(((JFXRadioButton)selection.getSelectedToggle()).getText().equals("Diesel")) {
            rate=dRate;
        } else if(((JFXRadioButton)selection.getSelectedToggle()).getText().equals("Petrol")) {
            rate=pRate;
        }

        adminButton.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(50),new BorderWidths(5),new Insets(-10))));
    }




    @FXML
    public void loadAdmin(MouseEvent e) throws IOException {

        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/postingDialog.fxml"));
        Parent root=loader.load();
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();


    }


    @FXML
    public void deleteItem(MouseEvent e) {
        ObservableList<Transaction> deleteList=transactionListView.getSelectionModel().getSelectedItems();
        if(deleteList.size()!=0) {
            Transaction transaction;
            for(int i=0;i<deleteList.size();i++) {
                transaction=deleteList.get(i);
                if(transaction.getType().equals("Diesel")||transaction.getType().equals("Petrol")) {
                    Transaction.setTotal(Transaction.getTotal()-transaction.getPrice());
                } else {
                    Transaction.setTotal(Transaction.getTotal()-transaction.getRate()*transaction.getQuantity());
                }
            }
            if(deleteList.size()==listModels.size()) {
                clearOil();
                clearExtra();
            }

            listModels.removeAll(deleteList);
            total.setText(String.valueOf(Transaction.getTotal()));
            itemsCount.setText(String.valueOf(listModels.size()));
        }

    }

    @FXML
    public void clearSelection(MouseEvent e) {
        transactionListView.getSelectionModel().clearSelection();
        if(mode==0||mode==1) {
            clearOil();
        } else {
            clearExtra();
        }
    }

    @FXML
    public void addNewItem(MouseEvent e) {
        if(mode==2) {
            if(checkExtraInput()==0) {
                Transaction transaction;
                if(transactionListView.getSelectionModel().getSelectedItems().size()==0) {
                    transaction=new Transaction();
                    transaction.extraTransaction(itemName.getText(),Float.parseFloat(rates.getText()),Integer.parseInt(quantity.getText()));
                } else {
                    transaction=transactionListView.getSelectionModel().getSelectedItem();
                    Transaction.setTotal(Transaction.getTotal()-transaction.getPrice());
                    transaction.extraTransaction(itemName.getText(),Float.parseFloat(rates.getText()),Integer.parseInt(quantity.getText()));
                }
                transaction.calcExtraPrice();
                Transaction.setTotal(Transaction.getTotal()+transaction.getPrice());
                Transaction.setName(companyName.getValue());

                total.setText(String.valueOf(Transaction.getTotal()));
                if(transactionListView.getSelectionModel().getSelectedItems().size()==0) {
                    listModels.add(transaction);
                    clearExtra();
                }


                itemsCount.setText(String.valueOf(listModels.size()));
            }
        } else {
            if(checkOilInput()==0) {
                String type;
                float rate;
                if(mode==1) {
                    type="Petrol";
                    if(admin) {
                        rate=pRate;
                    } else {
                        rate=SystemStatus.getpRate();
                    }

                }
                else {
                    type="Diesel";
                    if(admin) {
                        rate=dRate;
                    } else {
                        rate=SystemStatus.getdRate();
                    }
                }
                Transaction transaction;

                if(transactionListView.getSelectionModel().getSelectedItems().size()==0) {
                    transaction=new Transaction();
                    transaction.oilTransaction(type,vehicleNumber.getText(),Float.parseFloat(volume.getText()),Float.parseFloat(price.getText()),mode);
                    transaction.setRate(rate);
                    Transaction.setTotal(Transaction.getTotal()+transaction.getPrice());
                } else {
                    transaction=transactionListView.getSelectionModel().getSelectedItem();
                    Transaction.setTotal(Transaction.getTotal()-transaction.getPrice()+Float.parseFloat(price.getText()));
                    transaction.oilTransaction(type,vehicleNumber.getText(),Float.parseFloat(volume.getText()),Float.parseFloat(price.getText()),mode);
                    transaction.setRate(rate);

                }

                Transaction.setName(companyName.getValue());

                total.setText(String.valueOf(Transaction.getTotal()));

                if(transactionListView.getSelectionModel().getSelectedItems().size()==0) {
                    listModels.add(transaction);
                    clearOil();
                }

                itemsCount.setText(String.valueOf(listModels.size()));

            }
        }

    }



    @FXML
    public void printBill(MouseEvent e)throws IOException {
        if(!isListEmpty()) {
            AppHomeController.getInstance().showMessage("Printing Bill",1000);

            DebtListModel model=CreditCompanyList.getCompany(companyName.getValue());

            List<Transaction> newList=new ArrayList<>(transactionListView.getItems());//Create A shallow copy
            StoreCreditTransactionTask task=new StoreCreditTransactionTask(newList,model);

            if(admin) {
                task.setAdminDate(true,date);
            }
            TaskManager.executeTask(task);

            transactionListView.getItems().clear();
            masterClear();

            admin=false;

            adminButton.setBorder(new Border(new BorderStroke(Color.TRANSPARENT,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.EMPTY)));

            if(((JFXRadioButton)selection.getSelectedToggle()).getText().equals("Diesel")) {
                rate=SystemStatus.getdRate();
            } else if(((JFXRadioButton)selection.getSelectedToggle()).getText().equals("Petrol")) {
                rate=SystemStatus.getpRate();
            }

            CreditTransactionBill transactionBill=new CreditTransactionBill(newList);
            transactionBill.print();

        } else {
            AppHomeController.getInstance().showMessage("No Transaction Detected",1000);
        }

    }

}
