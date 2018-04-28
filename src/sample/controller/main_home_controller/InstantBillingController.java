package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.input.MouseEvent;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sample.controller.SystemLogging;
import sample.controller.main_home_controller.lists_controllers.TransactionListCell;
import sample.model.databases.DatabaseHandler;
import sample.model.InstantBill;
import sample.model.SystemStatus;
import sample.library.toolbar.ToolBar;
import sample.model.transaction_model.Transaction;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class InstantBillingController {

    private static boolean calcNowV =false,calcNowP=false,calcOldV=false,calcOldP=false,calcNowQ,calcNowR, listSelected =false,force=false;
    private DecimalFormat decimalFormat;
    private float rate;

    @FXML
    private JFXButton back,print,itemsCount;

    @FXML
    private JFXRadioButton diesel,petrol,extra;

    @FXML
    public ToolBar toolBar;

    @FXML
    private JFXTextField customerName,volume,price,vehicleNumber,eCustomerName,itemName,quantity,rates;

    @FXML
    private TextField total;

    @FXML
    private VBox extraBill,dieselPetrol;

    @FXML
    private ToggleGroup selection;

    @FXML
    private BorderPane root;

    @FXML
    private ListView<Transaction> transactionListView;

    private ObservableList<Transaction> listModels;

    private ArrayList<Transaction> arrayList;

    private JFXSnackbar snackbar;

    private int mode=0;


    @FXML
    public void initialize() throws IOException {

        Transaction.setName("");
        Transaction.setTotal(0f);


        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.setTitle("Instant Billing");


        diesel.setSelected(true);
        rate=SystemStatus.getdRate();

        snackbar=new JFXSnackbar(root);

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
            if(!listSelected) {
                transactionListView.getSelectionModel().clearSelection();
                clearOil();
                clearExtra();
            }
            listSelected =false;
            if(((JFXRadioButton)new_toggle).getText().equals("Extra")) {
                if(!customerName.getText().isEmpty())
                    eCustomerName.setText(customerName.getText());
                dieselPetrol.setVisible(false);
                extraBill.setVisible(true);
                mode=2;
            } else {
                extraBill.setVisible(false);
                dieselPetrol.setVisible(true);

                if(!eCustomerName.getText().isEmpty())
                    customerName.setText(eCustomerName.getText());

                if(((JFXRadioButton)new_toggle).getText().equals("Diesel")) {
                    rate=SystemStatus.getdRate();
                    mode=0;
                } else {
                    rate=SystemStatus.getpRate();
                    mode=1;
                }
            /*    if(calcOldV) {
                    if(!volume.getText().isEmpty())
                        price.setText(decimalFormat.format(Float.parseFloat(volume.getText())*rate));
                } else if(calcOldP) {
                    if(!price.getText().isEmpty())
                        volume.setText(decimalFormat.format(Float.parseFloat(price.getText())/rate));
                }*/

            }

        });

        decimalFormat=new DecimalFormat("#.####");

        initializeOil();

        initializeExtra();

    }


    private void initializeExtra() {

        RequiredFieldValidator eCustomerValidator=new RequiredFieldValidator();
        eCustomerValidator.setMessage("Customer Name Cannot Be Empty");
        eCustomerName.getValidators().add(eCustomerValidator);

        RequiredFieldValidator itemNameValidator=new RequiredFieldValidator();
        itemNameValidator.setMessage("Item Name Cannot Be Empty");
        itemName.getValidators().add(itemNameValidator);

        RequiredFieldValidator quantityValidator=new RequiredFieldValidator();
        quantityValidator.setMessage("Quantity Cannot Be Empty");
        quantity.getValidators().add(quantityValidator);

        RequiredFieldValidator ratesValidator=new RequiredFieldValidator();
        ratesValidator.setMessage("Rate Cannot Be Empty");
        rates.getValidators().add(ratesValidator);

        eCustomerName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[a-zA-Z ]*")) {
                eCustomerName.setText(oldValue);
            } else {
                if(newValue.isEmpty()&&!force) {
                    eCustomerName.validate();
                } else {
                    eCustomerName.resetValidation();
                }
            }
        });

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
                  /*  if(calcNowQ) {
                        if(!rates.getText().isEmpty()) {
                            total.setText(decimalFormat.format(Integer.parseInt(quantity.getText())*Float.parseFloat(rates.getText())));
                        }
                    }*/
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
                  /*  if(calcNowR) {
                        if(!quantity.getText().isEmpty()) {
                            total.setText(decimalFormat.format(Integer.parseInt(quantity.getText())*Float.parseFloat(rates.getText())));
                        }
                    }*/
                }
            }
        });

        rates.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcNowR=newPropertyValue;
        });

        quantity.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcNowQ=newPropertyValue;
        });


    }

    private void initializeOil() {
        RequiredFieldValidator customerNameValidator=new RequiredFieldValidator();
        customerNameValidator.setMessage("Customer Name Cannot Be Empty");
        customerName.getValidators().add(customerNameValidator);

        RequiredFieldValidator volumeValidator=new RequiredFieldValidator();
        volumeValidator.setMessage("Volume Cannot Be Empty");
        volume.getValidators().add(volumeValidator);

        RequiredFieldValidator priceValidator=new RequiredFieldValidator();
        priceValidator.setMessage("Price Cannot Be Empty");
        price.getValidators().add(priceValidator);

        RequiredFieldValidator vehicleNumberValidator=new RequiredFieldValidator();
        vehicleNumberValidator.setMessage("Vehicle Number Cannot Be Empty");
        vehicleNumber.getValidators().add(vehicleNumberValidator);

        customerName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[a-zA-Z ]*")) {//Recursion May Exists,Just a Work Around.
                customerName.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    customerName.validate();
                } else {
                    customerName.resetValidation();
                }
            }
        });

        vehicleNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.isEmpty()&&!force)
                vehicleNumber.validate();
            else
                vehicleNumber.resetValidation();
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
            }

        });

        price.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcOldP=true;
            calcOldV=false;
            calcNowP=newPropertyValue;
        });
    }


    @FXML
    public void printBill(MouseEvent e)throws IOException {
        if(checkOilInput()==0) {
            snackbar.show("Printing Bill",2000);
            InstantBill instantBill=new InstantBill(customerName.getText().trim(),price.getText(),volume.getText(),vehicleNumber.getText().trim(),diesel.isSelected());
            instantBill.printBill();
        }

    }

    private int checkOilInput() {
        int status=0;
        if(customerName.getText().trim().isEmpty()) {
            customerName.validate();
            status++;
        }

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

        if(eCustomerName.getText().trim().isEmpty()) {
            eCustomerName.validate();
            status++;
        }

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

    @FXML
    public void insertTransaction(MouseEvent e) {
        int status=0;

        if(price.getText().trim().isEmpty()) {
            price.validate();
            status++;
        }

        if(volume.getText().trim().isEmpty()) {
            volume.validate();
            status++;
        }

        if(status==0) {

            if(DatabaseHandler.storeInstantTransaction(Float.parseFloat(price.getText()),Float.parseFloat(volume.getText()))) {
                snackbar.show("Transaction Inserted Into Database",2000);
               SystemLogging.getLogger().fine("Instant Transaction Stored SuccessFully.");
            } else {
                snackbar.show("Error Inserting Transaction",2000);
                SystemLogging.getLogger().severe("Error Storing Instant Transaction!");
            }
        }
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
                Transaction.setName(eCustomerName.getText());
                Transaction.setTotal(Transaction.getTotal()+transaction.getRate()*transaction.getQuantity());

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
                if(mode==1)
                    type="Petrol";
                else
                    type="Diesel";
                Transaction transaction;
                if(transactionListView.getSelectionModel().getSelectedItems().size()==0)
                    transaction=new Transaction();
                else
                    transaction=transactionListView.getSelectionModel().getSelectedItem();
                transaction.oilTransaction(type,vehicleNumber.getText(),Float.parseFloat(volume.getText()),Float.parseFloat(price.getText()));
                Transaction.setName(customerName.getText());
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
}
