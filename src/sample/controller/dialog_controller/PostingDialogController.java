package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.CreditBillingController;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.nepali_date.DateConverter;
import sample.nepali_date.NepaliDate;
import sample.nepali_date.NepaliDateFormat;
import sample.nepali_date.NepaliDateGenerator;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class PostingDialogController implements BottomBarInterface {

    @FXML
    private JFXTextField customDieselRate;

    @FXML
    private JFXTextField customPetrolRate;

    @FXML
    private JFXComboBox<Integer> customYear;

    @FXML
    private JFXComboBox<Integer> customMonth;

    @FXML
    private JFXComboBox<Integer> customDay;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;


    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Admin Posting");
        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);

        customDieselRate.setText(String.valueOf(SystemStatus.getdRate()));
        customPetrolRate.setText(String.valueOf(SystemStatus.getpRate()));

        RequiredFieldValidator customDieselValidator=new RequiredFieldValidator();
        customDieselValidator.setMessage("Diesel Rate Cannot Be Empty.");
        customDieselRate.getValidators().add(customDieselValidator);

        RequiredFieldValidator customPetrolValidator=new RequiredFieldValidator();
        customPetrolValidator.setMessage("Petrol Rate Cannot Be Empty");
        customPetrolRate.getValidators().add(customPetrolValidator);

        customDieselRate.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,3}([\\.][0-9]{0,2})?")) {
                customDieselRate.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    customDieselRate.validate();
                } else {
                    customDieselRate.resetValidation();
                }
            }
        });

        customPetrolRate.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,3}([\\.][0-9]{0,2})?")) {
                customPetrolRate.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    customPetrolRate.validate();
                } else {
                    customPetrolRate.resetValidation();
                }
            }
        });

        /*
         * Since, we are only providing posting for current month and previous month
         * We have two cases, if the current month is 1st month, then we should also provide previous year field, other wise, we can
         * only provide the current month and previous month field
         *
         */

        NepaliDateGenerator generator=new NepaliDateGenerator();
        NepaliDateFormat dateFormat=generator.getDateFormat();
        NepaliDate date=new NepaliDate();

        ArrayList<Integer> yearList=new ArrayList<>();
        ArrayList<Integer> monthList=new ArrayList<>();

        if(dateFormat.getnMonth()==1) {

            //the current month is 1st, then add previous year, since user can also add this transaction to previous year

            yearList.add(dateFormat.getnYear());
            yearList.add(dateFormat.getnYear()-1);

            monthList.add(1);

        } else {

            //the current month is other than 1, so add the previous month to the list

            yearList.add(dateFormat.getnYear());

            monthList.add(dateFormat.getnMonth());
            monthList.add(dateFormat.getnMonth()-1);
        }

        customYear.setItems(FXCollections.observableList(yearList));
        customYear.setValue(dateFormat.getnYear());

        customMonth.setItems(FXCollections.observableList(monthList));
        customMonth.setValue(dateFormat.getnMonth());

        //We are adding days only of the current month, as it is selected by default, and we are adding up to current days, i.e if today is 15, add only 15 days

        ArrayList<Integer> dayList=new ArrayList<>();
        for(int i=1;i<=dateFormat.getnDay();i++) {
            dayList.add(i);
        }
        ObservableList<Integer> list=FXCollections.observableList(dayList);

        customDay.setItems(list);
        customDay.setValue(dateFormat.getnDay());

        customYear.valueProperty().addListener((ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)->{
            monthList.clear();
            if(newValue==(dateFormat.getnYear()-1)) {

                //The year has changed so, add month 12 to the list

                monthList.add(12);
            } else {

                //There is only one year, so add the previous month

                monthList.add(dateFormat.getnMonth());
                monthList.add(dateFormat.getnMonth()-1);
            }
            customMonth.setValue(monthList.get(0));
            customDay.setValue(dayList.get(0));
        });

        customMonth.valueProperty().addListener((ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)->{
            list.clear();

            if(dateFormat.getnMonth()==newValue) {

                //the default month is selected, so add up to the current day

                for(int i=1;i<=dateFormat.getnDay();i++) {
                    list.add(i);
                }
            } else {

                //previous month selected, so get the no of days of the previous month and add it to the list

                for(int i=1;i<=date.getDaysOfYearMonth(customYear.getValue(),newValue);i++) {
                    list.add(i);
                }
            }
            customDay.setValue(list.get(0));

        });

    }

    @Override
    public void performAction(MouseEvent e) throws IOException {

        int status=0;
        if(customDieselRate.getText().isEmpty()) {
            status++;
            customDieselRate.validate();
        }

        if(customPetrolRate.getText().isEmpty()) {
            status++;
            customPetrolRate.validate();
        }

        if(status==0) {

            LocalDate date=LocalDate.of(2018,4,14);

            date=date.plusDays((new DateConverter()).getTotalDays(customYear.getValue(),customMonth.getValue(),customDay.getValue()));

            CreditBillingController.getInstance().setAdmin(Float.parseFloat(customDieselRate.getText()),Float.parseFloat(customPetrolRate.getText()),Date.valueOf(date));
            ((Node)e.getSource()).getScene().getWindow().hide();
        }


    }
}

