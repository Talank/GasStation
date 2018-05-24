package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
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
import sample.nepali_date.DateConvertor;
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


        NepaliDateGenerator generator=new NepaliDateGenerator();
        NepaliDateFormat dateFormat=generator.getDateFormat();
        NepaliDate date=new NepaliDate();

        ArrayList<Integer> yearList=new ArrayList<>();
        ArrayList<Integer> monthList=new ArrayList<>();

        if(dateFormat.getnMonth()==1) {
            yearList.add(dateFormat.getnYear());
            yearList.add(dateFormat.getnYear()-1);

            monthList.add(1);

        } else {
            yearList.add(dateFormat.getnYear());

            monthList.add(dateFormat.getnMonth());
            monthList.add(dateFormat.getnMonth()-1);
        }

        customYear.setItems(FXCollections.observableList(yearList));
        customYear.setValue(dateFormat.getnYear());

        customMonth.setItems(FXCollections.observableList(monthList));
        customMonth.setValue(dateFormat.getnMonth());

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
                monthList.add(12);
            } else {
                monthList.add(dateFormat.getnMonth());
                monthList.add(dateFormat.getnMonth()-1);
            }
            customMonth.setValue(monthList.get(0));
            customDay.setValue(dayList.get(0));
        });

        customMonth.valueProperty().addListener((ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)->{
            list.clear();

            if(dateFormat.getnMonth()==newValue) {
                for(int i=1;i<=dateFormat.getnDay();i++) {
                    list.add(i);
                }
            } else {
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

            date=date.plusDays(DateConvertor.getTotalDays(customYear.getValue(),customMonth.getValue(),customDay.getValue()));

            CreditBillingController.getInstance().setAdmin(Float.parseFloat(customDieselRate.getText()),Float.parseFloat(customPetrolRate.getText()),Date.valueOf(date));
            ((Node)e.getSource()).getScene().getWindow().hide();
        }


    }
}

