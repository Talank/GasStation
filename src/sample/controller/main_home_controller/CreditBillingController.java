package sample.controller.main_home_controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sample.controller.main_home_controller.lists_controllers.TransactionListCell;
import sample.library.toolbar.ToolBar;

import sample.model.CreditCompanyList;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.transaction_model.CreditTransactionBill;
import sample.model.transaction_model.Transaction;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;


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

    @FXML
    private JFXButton itemsCount;

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

    @FXML
    public void initialize() {

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
                    price.setText(String.valueOf(newValue.getPrice()));
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
                    rate=SystemStatus.getdRate();
                    mode=0;
                } else {
                    rate=SystemStatus.getpRate();
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
                    if(force) {
                        force=false;
                    }
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
                    if(force) {
                        force=false;
                    }
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

            if(force) {
                force=false;
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

                if(force) {
                    force=false;
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
        force=false;
    }

    private void clearExtra() {
        force=true;
        itemName.setText("");
        rates.setText("");
        quantity.setText("");
        force=false;
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
                if(transactionListView.getSelectionModel().getSelectedItems().size()==0)
                    transaction=new Transaction();
                else
                    transaction=transactionListView.getSelectionModel().getSelectedItem();
                transaction.extraTransaction(itemName.getText(),Float.parseFloat(rates.getText()),Integer.parseInt(quantity.getText()));
                Transaction.setName(companyName.getValue());

                transaction.calcExtraPrice();
                Transaction.setTotal(Transaction.getTotal()+transaction.getPrice());

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
                    rate=SystemStatus.getpRate();
                }
                else {
                    type="Diesel";
                    rate=SystemStatus.getdRate();
                }
                Transaction transaction;
                if(transactionListView.getSelectionModel().getSelectedItems().size()==0)
                    transaction=new Transaction();
                else
                    transaction=transactionListView.getSelectionModel().getSelectedItem();
                transaction.oilTransaction(type,vehicleNumber.getText(),Float.parseFloat(volume.getText()),Float.parseFloat(price.getText()),mode);
                transaction.setRate(rate);
                Transaction.setName(companyName.getValue());
                Transaction.setTotal(Transaction.getTotal()+transaction.getPrice());
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
            snackBar.show("Printing And Storing Bill.",2000);

            int[] ids=CreditCompanyList.getID(companyName.getValue());
            if(ids[0]!=-1&&ids[1]!=-1) {
                DatabaseHandler.storeCreditTransaction(transactionListView.getItems(),ids[1],ids[0]);
                CreditTransactionBill transactionBill=new CreditTransactionBill(transactionListView.getItems());
                transactionBill.print();
            }

        } else {
            snackBar.show("No Transaction Detected.",2000);
        }

    }
}
