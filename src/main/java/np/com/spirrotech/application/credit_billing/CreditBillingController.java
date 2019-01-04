package np.com.spirrotech.application.credit_billing;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import np.com.spirrotech.application.home.AppHomeController;

import np.com.spirrotech.library.toolbar.ToolBar;

import np.com.spirrotech.model.CreditCompanyList;
import np.com.spirrotech.model.DebtListModel;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.model.nepaliPriceFormat.Precision;
import np.com.spirrotech.model.nepaliPriceFormat.PriceFormat;
import np.com.spirrotech.model.transaction_model.CreditTransactionBill;
import np.com.spirrotech.model.transaction_model.Transaction;

import np.com.spirrotech.utils.tasks.TaskManager;


import java.io.IOException;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;


/**
 * This is the controller for the credit billing
 */

public class CreditBillingController {

    private boolean calcNowV =false,calcNowP=false,calcOldV=false,calcOldP=false, listSelected =false,force=false;
    private Precision precision;
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


    /**
     * Initialize the view
     */

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
                        price.setText(precision.format(Float.parseFloat(volume.getText())*rate));
                } else if(calcOldP) {
                    if(!price.getText().isEmpty())
                        volume.setText(precision.format(Float.parseFloat(price.getText())/rate));
                }
            }

        });

        precision=new Precision();

        companyName.setItems(FXCollections.observableList(CreditCompanyList.getCompanyList()));
        companyName.setValue(CreditCompanyList.getCompanyList().get(0));

        initializeOil();
        initializeExtra();

    }

    /**
     * Initialize view linked to extras
     */

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
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
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


    /**
     * Initializes the views related to oil transaction
     */

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
            if(!newValue.matches("[-a-zA-Z0-9]{0,10}")) {
                vehicleNumber.setText(oldValue);
            } else {
                if(newValue.isEmpty()&&!force) {
                    vehicleNumber.validate();
                } else {
                    vehicleNumber.resetValidation();
                }
            }

        });

        volume.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
                volume.setText(oldValue);
            } else {
                if(newValue.isEmpty()&&!force) {
                    volume.validate();
                } else {
                    volume.resetValidation();
                    if(calcNowV) {
                        price.setText(precision.format(Float.parseFloat(volume.getText())*rate));
                    }

                }
            }


        });

        volume.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcOldV=true;
            calcNowP=false;
            calcNowV=newPropertyValue;
        });


        price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                price.setText(oldValue);
            else
            if(newValue.isEmpty()&&!force)
                price.validate();
            else {
                price.resetValidation();
                if(calcNowP) {
                    volume.setText(precision.format(Float.parseFloat(price.getText())/rate));
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

    /**
     *
     * @return true if items in list is selected
     */

    private boolean isListSelected() {
        return !transactionListView.getSelectionModel().isEmpty();
    }

    /**
     * Clears all the fields related to oil transaction
     */

    private void clearOil() {
        force=true;
        vehicleNumber.setText("");
        volume.setText("");
        price.setText("");
    }

    /**
     * Clears all the field related to extra transaction
     */

    private void clearExtra() {
        force=true;
        itemName.setText("");
        rates.setText("");
        quantity.setText("");
    }

    /**
     * Clears all the fields in the credit billing vies
     */

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

    /**
     * Checks the oil input fields for validity
     * @return the status of the check, 0 if all the fields are valid
     */

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

        if(vehicleNumber.getText().isEmpty()||(vehicleNumber.getText().length()<1)) {
            vehicleNumber.validate();
            status++;
        }
        return status;
    }

    /**
     * Checks the extra fields inputs
     * @return 0 if all the fields are valid
     */

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

    /**
     *
     * @return true if the list is empty
     */

    private boolean isListEmpty() {
        return transactionListView.getItems().isEmpty();
    }

    /**
     *
     * @return the current instance
     */

    public static CreditBillingController getInstance() {
        return instance;
    }


    /**
     * Sets the admin mode for this transaction
     * @param dRate the supplied new diesel rate
     * @param pRate the supplied new petrol rate
     * @param date the supplied date
     */

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


    /**
     * loads the posting view
     * @param e
     * @throws IOException
     */

    @FXML
    public void loadAdmin(MouseEvent e) throws IOException {

        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("np/com/spirrotech/application/credit_billing/dialogs/posting/postingDialog.fxml"));
        Parent root=loader.load();
        Stage stage=new Stage();
        stage.setTitle("Posting Dialog");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("np/com/spirrotech/resources/images/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();


    }

    /**
     * Removes item from the list
     * @param e
     */

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

    /**
     * Clears the selections of the list
     * @param e
     */

    @FXML
    public void clearSelection(MouseEvent e) {
        transactionListView.getSelectionModel().clearSelection();
        if(mode==0||mode==1) {
            clearOil();
        } else {
            clearExtra();
        }
    }

    /**
     * Adds new item to the list
     * @param e
     */

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


    /**
     * Prints the bill, and also stores the transactions to the database
     * @param e
     * @throws IOException
     */

    @FXML
    public void printBill(MouseEvent e)throws IOException {
        if(!isListEmpty()) {


            DebtListModel model=CreditCompanyList.getCompany(companyName.getValue());

            List<Transaction> newList=new ArrayList<>(transactionListView.getItems());//Create A shallow copy
            StoreCreditTransactionTask task=new StoreCreditTransactionTask(newList,model);

            if(admin) {
                task.setAdminDate(true,date);
            }
            TaskManager.getInstance().executeTask(task);

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
            transactionBill.setPostingDate(date);
            transactionBill.print();

            //display the printing bill
            AppHomeController.getInstance().showMessage("Printing Bill",1000);

        } else {
            AppHomeController.getInstance().showMessage("No Transaction Detected",1000);
        }

    }

    @FXML
    public void saveBill(MouseEvent event) {
        if(!isListEmpty()) {


            //get the current company from the company select field
            DebtListModel model=CreditCompanyList.getCompany(companyName.getValue());

            List<Transaction> newList=new ArrayList<>(transactionListView.getItems());//Create A shallow copy

            //the task to store the task
            StoreCreditTransactionTask task=new StoreCreditTransactionTask(newList,model);

            if(admin) {//set the admin date of the transaction
                task.setAdminDate(true,date);
            }

            //store the transactions
            TaskManager.getInstance().executeTask(task);

            //clear the current lists of all the items
            transactionListView.getItems().clear();

            //reset the textFields
            masterClear();

            //reset the admin
            admin=false;
            adminButton.setBorder(new Border(new BorderStroke(Color.TRANSPARENT,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.EMPTY)));

            //reset the rate of the transaction
            if(((JFXRadioButton)selection.getSelectedToggle()).getText().equals("Diesel")) {
                rate=SystemStatus.getdRate();
            } else if(((JFXRadioButton)selection.getSelectedToggle()).getText().equals("Petrol")) {
                rate=SystemStatus.getpRate();
            }

            //reset the transaction total
            Transaction.setTotal(0);


            //display the success message
            AppHomeController.getInstance().showMessage("Transaction Saved!",1000);

        } else {
            AppHomeController.getInstance().showMessage("No Transaction Detected",1000);
        }
    }

}