package np.com.spirrotech.application.credit_companies.company_transactions;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import np.com.spirrotech.model.CreditDetailsModel;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.model.nepaliPriceFormat.Precision;
import np.com.spirrotech.model.nepaliPriceFormat.PriceFormat;
import np.com.spirrotech.utils.nepali_date.DateConverter;
import np.com.spirrotech.utils.nepali_date.NepaliDateGenerator;
import np.com.spirrotech.utils.nepali_date.ParseNepaliDate;
import np.com.spirrotech.utils.tasks.TaskManager;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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
        editField = new TextField();
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
        //store the old model so that it can be used as compound key
        CreditDetailsModel oldModel = new CreditDetailsModel(model.getVehicleNo(), model.getItemName(), model.getDate(), model.getVolume(), model.getPrice(), model.getRate());
        oldModel.setTransactionID(model.getTransactionID());
        if (getTableColumn().getId().equals("rate")) {


            //update the model, so that it can be obtained from other location
            model.setRate(editField.getText());
            model.setPrice(String.valueOf(Float.parseFloat(model.getVolume()) * Float.parseFloat(editField.getText())));


            //perform the  update of the credit transaction
            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, 0));


        } else if (getTableColumn().getId().equals("quantity")) {

            model.setVolume(editField.getText());
            model.setPrice(String.valueOf(Float.parseFloat(model.getRate()) * Float.parseFloat(editField.getText())));

            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, 0));

        } else if (getTableColumn().getId().equals("vehicleNo")) {

            model.setVehicleNo(editField.getText());

            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, 2));


        } else if (getTableColumn().getId().equals("itemName")) {

            model.setItemName(editField.getText());
            //Here if the item is changed from petrol to diesel or vice-versa then, change the rate and price respectively

            if (editField.getText().equalsIgnoreCase("petrol")){
                model.setRate(String.valueOf(SystemStatus.getpRate()));
            }

            if (editField.getText().equalsIgnoreCase("diesel")){
                model.setRate(String.valueOf(SystemStatus.getdRate()));
            }
            model.setPrice(String.valueOf(Float.parseFloat(model.getVolume()) * Float.parseFloat(model.getRate())));

            TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, 3));


        } else if (getTableColumn().getId().equals("date")) {

            if (dateParser(model)) {
                TaskManager.getInstance().executeTask(new UpdateCreditTransactionsTask(oldModel, model, companyName, 1));
            } else {
                cancelEdit();
            }


        }


    }

    private boolean dateParser(CreditDetailsModel model) {

        try {

            //parse the date from the text in the cell
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            //build a calender to represent the provided date
            Calendar editedDate =  Calendar.getInstance();
            editedDate.setTime(format.parse(editField.getText()));


            //parse the current nepali date
            ParseNepaliDate parser = new ParseNepaliDate(SystemStatus.getNepaliCurrentDate());

            //check if the year difference is within 1 year
            if ((parser.getnYear() - editedDate.get(Calendar.YEAR)) < 2) {

                //this is the date which we used as a reference to convert the date to nepali
                LocalDate date=LocalDate.of(2018,4,14);

                //convert the date to english date
                date=date.plusDays((new DateConverter()).getTotalDays(editedDate.get(Calendar.YEAR),(editedDate.get(Calendar.MONTH) + 1),editedDate.get(Calendar.DAY_OF_MONTH)));

                //check if the date is not more than today's date
                if (LocalDate.now().compareTo(date) >= 0) {
                    //set the date
                    model.setSqlDate(Date.valueOf(date));

                    //format the date and set the new formatted date to the cell
                    NepaliDateGenerator generator = new NepaliDateGenerator(date);
                    editField.setText(generator.toString());

                    //copy all the entry inside the current transaction id
                    List<CreditDetailsModel> modelList = new ArrayList<>();
                    for (List<CreditDetailsModel> list : companyDetails) {
                        if (list.get(0).getTransactionID() == model.getTransactionID()) {
                            modelList = list;
                            break;
                        }
                    }

                    //modify the date of all the entry that is present within the current transaction
                    for (CreditDetailsModel detailsModel : modelList) {
                        detailsModel.setSqlDate(Date.valueOf(date));
                        detailsModel.setDate(generator.toString());
                    }

                    return true;
                }


            }




        } catch(ParseException e) {
            e.printStackTrace();
        }

        return false;
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

        //load the formatted text in the text field
        if (getTableColumn().getId().equals("date")) {
            ParseNepaliDate parseNepaliDate = new ParseNepaliDate(getItem());
            editField.setText(parseNepaliDate.getnYear() + "-" + parseNepaliDate.getnMonth() + "-" + parseNepaliDate.getnDay());
        } else {
            editField.setText(parseCellString());
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

                    if (getTableColumn().getId().equals("date")) {
                        ParseNepaliDate parseNepaliDate = new ParseNepaliDate(item);
                        editField.setText(parseNepaliDate.getnYear() + "-" + parseNepaliDate.getnMonth() + "-" + parseNepaliDate.getnDay());
                    } else {
                        editField.setText(parseCellString());
                    }


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
