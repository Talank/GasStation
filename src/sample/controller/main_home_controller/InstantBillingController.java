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
import sample.model.SystemStatus;
import sample.library.toolbar.ToolBar;
import sample.model.transaction_model.InstantTransactionBill;
import sample.model.transaction_model.Transaction;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.StoreInstantTransactionTask;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/*
* This class is responsible for controlling the instant view
* It contains all the logic for input validation
*
* The class has two views,one for oil and other for extra placed in a StackPane Layout
* These two views are swapped as per the selection radio buttons
*
* */

public class InstantBillingController {

    private boolean calcNowV =false,calcNowP=false,calcOldV=false,calcOldP=false, listSelected =false,force=false;
    private DecimalFormat decimalFormat;
    private float rate;


    @FXML
    private JFXButton itemsCount;//sample.ApplicationPreLoader.Reference for the no of items added to the list

    @FXML
    private JFXRadioButton diesel,petrol,extra;//sample.ApplicationPreLoader.Reference for the radio buttons to select the type of items

    @FXML
    public ToolBar toolBar;//Generalized Toolbar reference

    @FXML
    private JFXTextField customerName,volume,price,vehicleNumber,itemName,quantity,rates;//References for all the textFields

    @FXML
    private TextField total;//TextField to store the total amounts

    @FXML
    private VBox extraBill,dieselPetrol;//Two views for oil and extra items

    @FXML
    private ToggleGroup selection;//Group for the radio buttons

    @FXML
    private BorderPane root;//Root the view,needed for the snackBar

    @FXML
    private ListView<Transaction> transactionListView;//List to store all the items in the transaction

    private ObservableList<Transaction> listModels;//Model for the list

    private ArrayList<Transaction> arrayList;

    private JFXSnackbar snackBar;

    private int mode=0;

    /*
    * Transaction class uses a static variables name and total ,to represent the name and total
    * As name and total for a entire transaction is same
    * */

    @FXML
    public void initialize() throws IOException {


//      Initialize the transaction class
        Transaction.setName("");
        Transaction.setTotal(0f);

//      General Toolbar setting title and path for the back button
        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.setTitle("Instant Billing");

//      Set the Diesel as default selection
        diesel.setSelected(true);

//      Get the current rate
        rate=SystemStatus.getdRate();

//      Initialize the snackBar
        snackBar =new JFXSnackbar(root);

//      Just Typical Initialization of the listView in javafx
        transactionListView.setCellFactory(lv->new TransactionListCell());
        transactionListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        arrayList=new ArrayList<>();
        listModels=FXCollections.observableList(arrayList);

        transactionListView.setItems(listModels);

//      Called when selection changes in the list
        transactionListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Transaction> observable, Transaction oldValue, Transaction newValue)->{
            /*
            * Check the new value
            * because it is null when the list is deselected
            * leads to null pointer exception
            * */
            if(newValue!=null) {

//              Get type of transaction to swap the views if it doesn't the view selected by the radio buttons
                if(newValue.getType().equals("Diesel")||newValue.getType().equals("Petrol")) {

//                  Set the Values contained in the transaction
                    vehicleNumber.setText(newValue.getVehicleNo());
                    price.setText(String.valueOf(newValue.getPrice()));
                    volume.setText(String.valueOf(newValue.getVolume()));

                    /*
                    * The main problem here is that,when we change the radio buttons selection
                    * It triggers the selection change listener in the below code
                    *
                    * listSelected is used to check,if the radio button is clicked by user or it is changed due to selecting the list
                    *
                    *
                    * */
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

        /*
        * Listener to listen radio buttons change
        * The problem is it is fired,when we change the selection in the above  list listener
        *
        * */

        selection.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov,Toggle old_toggle, Toggle new_toggle)->{

//          Simple statement to handle the radio buttons selection
            if(((JFXRadioButton)new_toggle).getText().equals("Extra")) {

                dieselPetrol.setVisible(false);
                extraBill.setVisible(true);
                mode=2;
            } else {
                extraBill.setVisible(false);
                dieselPetrol.setVisible(true);

//              Keep track of the current rate
                if(((JFXRadioButton)new_toggle).getText().equals("Diesel")) {
                    rate=SystemStatus.getdRate();
                    mode=0;
                } else {
                    rate=SystemStatus.getpRate();
                    mode=1;
                }

            }

            /*
            * Check if the radio buttons is changed by user or by selecting the list
            *
            * if it is changed by user and ,if a list item is selected ,then clear the selection and provide new fresh transaction to enter
            *
            * Otherwise user changed the radio buttons ,so if user changed between diesel and petrol calculate the respective values
            *
            * */

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

//      Format to display number with 4 precision label
        decimalFormat=new DecimalFormat("#.####");

        initializeOil();

        initializeExtra();

    }


    /*
    * Initialize All the textFields used in the extra transaction
    *
    * Bind the RequiredFieldValidators to validate the user input
    *
    * For real time validation,listener is added to each textField
    *
    * Boolean Force is used to check if user clicked the 'X' Button to clear the list,so in this case do not validate
    *
    *
    * */

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
            if(!newValue.matches("[a-zA-Z ]*")) {//We must accept "" text,otherwise system goes in recursive loop and results in stack overflow
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

    /*
    * Initialize all the textFields associated with Oil Transaction
    *
    * Same as initializeExtra()
    *
    * We calculate the price when user inputs volume and vice versa
    * */

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
                if(newValue.isEmpty()&&!force) {
                    customerName.validate();
                } else {
                    customerName.resetValidation();
                }
            }
        });

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

        /*
         * Get who received focus first
         *
         * Used when user switches between diesel and petrol
         *
         * if user last edit was in volume then,we keep the volume constant and change the price when he changes the selection between petrol and diesel
         * and vice versa
         *
         * */

        volume.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcOldV=true;
            calcNowP=false;
            calcNowV=newPropertyValue;
        });


        /*
        * In this listener only force is set to false,
        * because the clearOil() calls the price in last
        *
        * */


        price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?"))
                price.setText(oldValue);
            else
            if(newValue.isEmpty()&&!force)
                price.validate();
            else {
                price.resetValidation();

                if(force) {
                    force=false;
                }

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


//  Function to check if the list is selected or not
    private boolean isListSelected() {
        return !transactionListView.getSelectionModel().isEmpty();
    }

//  Function to check if the list is emtpy
    private boolean isListEmpty() {
        return transactionListView.getItems().isEmpty();
    }


    /*
    *
    * This Function is called when Print button is clicked
    * We check if the list is empty
    * Then we pass all the items in the list to InstantTransactionBill class that computes and formats the bill
    *
    * */

    @FXML
    public void printBill(MouseEvent e)throws IOException {

        if(!isListEmpty()) {
            List<Transaction> newList=new ArrayList<>(transactionListView.getItems());
            executeTransaction();
            InstantTransactionBill instantTransactionBill=new InstantTransactionBill(newList);
            instantTransactionBill.print();
            snackBar.show("Printing Bill",2000);
        } else {
            snackBar.show("No Transaction Detected.",2000);
        }

    }

//  Function to validate the Oil Input Fields

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


//  Function to validate extra Input fields
    private int checkExtraInput() {
        int status=0;

        if(customerName.getText().trim().isEmpty()) {
            customerName.validate();
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

    /*
    * This Function is called when insert is clicked
    *
    * Check if the list is empty
    * Store the transactions in the databases
    *
    * */

    @FXML
    public void insertTransaction(MouseEvent e) {
        if(!isListEmpty()) {
            executeTransaction();
            /*if(DatabaseHandler.storeInstantTransaction(transactionListView.getItems())) {
                snackBar.show("Transaction Inserted Into Database",2000);


                SystemLogging.getLogger().fine("Instant Transaction Stored SuccessFully.");
            } else {
                snackBar.show("Error Inserting Transaction",2000);
                SystemLogging.getLogger().severe("Error Storing Instant Transaction!");
            }*/
        }
    }

    private void executeTransaction() {
        List<Transaction> newList=new ArrayList<>(transactionListView.getItems());//Create A shallow copy
        StoreInstantTransactionTask task=new StoreInstantTransactionTask();
        task.setInstantTransaction(newList);
        TaskManager.executeTask(task);
        transactionListView.getItems().clear();
        masterClear();
    }

    /*
    * This is function is called when '-' button is clicked
    *
    * Get the selected Items (Items may be multiples)
    *
    * Get the item type and remove them from list and also decrease the total price
    *
    * */

    @FXML
    public void deleteItem(MouseEvent e) {

        if(!isListEmpty()&&isListSelected()) {

            Transaction transaction=transactionListView.getSelectionModel().getSelectedItem();
            if(transaction.getType().equals("Diesel")||transaction.getType().equals("Petrol")) {
                Transaction.setTotal(Transaction.getTotal()-transaction.getPrice());
            } else {
                Transaction.setTotal(Transaction.getTotal() - transaction.getRate() * transaction.getQuantity());
            }


//          Remove the items from list,change the total value and also the itemsCount
            listModels.remove(transaction);
            total.setText(String.valueOf(Transaction.getTotal()));
            itemsCount.setText(String.valueOf(listModels.size()));

            /*
             * If all the items in the list is selected
             *
             * Then there will be no value in the textFields to put and they will invalidate
             *
             * To prevent that we set Forced=true
             *
             * otherwise if at least one item will be present after deletion that item will be selected and textFields will contain that transaction values
             * */

            if(isListEmpty()) {
                clearOil();
                clearExtra();
            }

        }

    }

    /*
    * This Function is called when 'X' is clicked
    *
    * Check which view the user currently is and clear that view
    *
    *
    * */

    @FXML
    public void clearSelection(MouseEvent e) {
        transactionListView.getSelectionModel().clearSelection();
        if(mode==0||mode==1) {
            clearOil();
        } else {
            clearExtra();
        }
    }

//  Simple Function to clear the fields using force=true otherwise the fields will invalidate

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
        customerName.setText("");
        total.setText("0.0");
        vehicleNumber.setText("");
        volume.setText("");
        itemName.setText("");
        rates.setText("");
        quantity.setText("");
        price.setText("");
        itemsCount.setText("0");

    }

    /*
    * This Function is called when '+' is clicked
    *
    * Check the mode and validate all the fields
    *
    * After validation add the transaction to the list
    *
    * To prevent duplicate items insertion,When a item in list is selected and '+' is clicked
    * then the same item is edited rather than adding the same items to the list
    *
    * */

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
                Transaction.setName(customerName.getText());
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
                } else {
                    type="Diesel";
                    rate=SystemStatus.getdRate();
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


                Transaction.setName(customerName.getText());

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
