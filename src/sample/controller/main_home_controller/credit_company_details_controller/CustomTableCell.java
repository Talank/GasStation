package sample.controller.main_home_controller.credit_company_details_controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.model.CreditDetailsModel;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.UpdateCreditTransactionsTask;

import java.util.ArrayList;
import java.util.List;


public class CustomTableCell extends TableCell<CreditDetailsModel, String> {

    private TextField editField;

    private Precision precision;

    private PriceFormat priceFormat;

    private static List<List<CreditDetailsModel>> companyDetails;

    private static String companyName;

    private ChangeListener<String> changeListener;

    public CustomTableCell() {
        precision = new Precision();
        priceFormat = new PriceFormat();
    }

    //store the reference to the current model of the company
    public static void setCompanyDetails(List<List<CreditDetailsModel>> companyDetail) {
        companyDetails = companyDetail;
    }

    public static void setCompanyName(String companyNames) {
        companyName = companyNames;
    }


    private void createTextField() {
        editField = new TextField(parseCellString());
        editField.setMinWidth(getWidth() - getGraphicTextGap()*2);

        //initialize the change listener for the text field
        changeListener = ((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
                editField.setText(oldValue);
            }
        });



        //handle the button press,
        editField.setOnKeyPressed((t -> {
            if (t.getCode() == KeyCode.ENTER) {
                //update the database with the current data
                updateModel();
                commitEdit(editField.getText());
            } else if (t.getCode() == KeyCode.ESCAPE) {
                //cancel the edit
                cancelEdit();
            }
        }));

    }

    private void updateModel() {
        CreditDetailsModel model = getTableView().getItems().get(getTableRow().getIndex());
        if (getTableColumn().getId().equals("rate")) {

            //store the old model so that it can be used as compound key
            CreditDetailsModel oldModel = new CreditDetailsModel(model.getVehicleNo(), model.getItemName(), model.getDate(), model.getVolume(), model.getPrice(), model.getRate());
            oldModel.setTransactionID(model.getTransactionID());


            //update the model, so that it can be obtained from other location
            model.setRate(editField.getText());
            model.setPrice(String.valueOf(Float.parseFloat(model.getVolume()) * Float.parseFloat(editField.getText())));


            //perform the  update of the credit transaction
            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, true));


        } else if (getTableColumn().getId().equals("quantity")) {

            //store the old model so that it can be used as compound key
            CreditDetailsModel oldModel = new CreditDetailsModel(model.getVehicleNo(), model.getItemName(), model.getDate(), model.getVolume(), model.getPrice(), model.getRate());
            oldModel.setTransactionID(model.getTransactionID());

            model.setVolume(editField.getText());
            model.setPrice(String.valueOf(Float.parseFloat(model.getRate()) * Float.parseFloat(editField.getText())));

            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, true));

        } else if (getTableColumn().getId().equals("vehicleNo")) {

            CreditDetailsModel oldModel = new CreditDetailsModel(model.getVehicleNo(), model.getItemName(), model.getDate(), model.getVolume(), model.getPrice(), model.getRate());
            oldModel.setTransactionID(model.getTransactionID());

            model.setVehicleNo(editField.getText());

            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, false));


        } else if (getTableColumn().getId().equals("itemName")) {

            CreditDetailsModel oldModel = new CreditDetailsModel(model.getVehicleNo(), model.getItemName(), model.getDate(), model.getVolume(), model.getPrice(), model.getRate());
            oldModel.setTransactionID(model.getTransactionID());

            model.setItemName(editField.getText());

            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, false));
        }


    }

    private String parseCellString() {
        //remove the comma from the item and set the text to the text field
        StringBuilder builder = new StringBuilder(getItem());

        String item = getItem();
        //loop through all the ',' in the number if any
        while (item.contains(",")) {
            //remove the ',' from the string
            builder.deleteCharAt(item.indexOf(','));

            //update the item
            item = builder.toString();
        }

        return item;
    }

    @Override
    public void startEdit() {
        super.startEdit();

        //initialize the text field
        if (editField == null) {
            createTextField();
        }

        try {
            editField.textProperty().removeListener(changeListener);
        } catch(Exception e) {
            e.printStackTrace();
        }

        //check if the column is for numerical data, then add the field validators to the text field
        if (getTableColumn().getId().equals("rate") || getTableColumn().getId().equals("quantity")) {
            editField.textProperty().addListener(changeListener);
        }

        //set the current cell layout to the text field
        setGraphic(editField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        //select all the data inside the text field
        editField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        //check if the column is for numerical data, then convert the data by adding comma
        if (getTableColumn().getId().equals("rate") || getTableColumn().getId().equals("quantity")) {
            setText(priceFormat.format(precision.format(Float.parseFloat(getItem()))));
        } else {
            setText(getItem());
        }

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            //the current cell is being edited
            if (isEditing()) {
                if (editField != null) {
                    editField.setText(parseCellString());
                }

                setGraphic(editField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {

                //check if the column is for numerical data, then convert the data by adding comma
                if (getTableColumn().getId().equals("rate") || getTableColumn().getId().equals("quantity")) {
                    item = priceFormat.format(precision.format(Float.parseFloat(item)));
                }

                setText(item);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }

    }
}
