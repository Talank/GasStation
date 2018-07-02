package sample;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.print.Printer;

public class TestingClass {


    public void displayPrinters() {
        ObservableSet<Printer> printers=Printer.getAllPrinters();
        for(Printer printer:printers) {
            System.out.println("Name:"+printer.getName());
        }
    }

    public void test() {

    }

    public void loadFakeCompanyData() {

    }

}
