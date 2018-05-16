package sample.model;

import javafx.stage.Stage;

import java.sql.Date;

public class SystemStatus {
    private static float iDiesel,iPetrol,iCounter,fDiesel,fPetrol,dRate,pRate;
    private static float dieselStock,petrolStock;
    private static String mainHomePath;
    private static String currentDate,transactionDate,billPrinter,analyticsPrinter;
    private static Stage stage;

    public static void initializeInitial(DailyAnalyticsModel model) {
        iDiesel=model.getdInitial();
        iPetrol=model.getpInitial();
        iCounter=model.getcInitial();
    }

    public static float getiDiesel() {
        return iDiesel;
    }

    public static void setiDiesel(float iDiesel) {
        SystemStatus.iDiesel = iDiesel;
    }

    public static float getiPetrol() {
        return iPetrol;
    }

    public static void setiPetrol(float iPetrol) {
        SystemStatus.iPetrol = iPetrol;
    }

    public static float getfDiesel() {
        return fDiesel;
    }

    public static void setfDiesel(float fDiesel) {
        SystemStatus.fDiesel = fDiesel;
    }

    public static float getfPetrol() {
        return fPetrol;
    }

    public static void setfPetrol(float fPetrol) {
        SystemStatus.fPetrol = fPetrol;
    }

    public static float getdRate() {
        return dRate;
    }

    public static void setdRate(float dRate) {
        SystemStatus.dRate = dRate;
    }

    public static float getpRate() {
        return pRate;
    }

    public static void setpRate(float pRate) {
        SystemStatus.pRate = pRate;
    }

    public static float getiCounter() {
        return iCounter;
    }

    public static void setiCounter(float iCounter) {
        SystemStatus.iCounter = iCounter;
    }

    public static String getMainHomePath() {
        return mainHomePath;
    }

    public static void setMainHomePath(String mainHomePath) {
        SystemStatus.mainHomePath = mainHomePath;
    }

    public static float getDieselStock() {
        return dieselStock;
    }

    public static void setDieselStock(float dieselStock) {
        SystemStatus.dieselStock = dieselStock;
    }

    public static float getPetrolStock() {
        return petrolStock;
    }

    public static void setPetrolStock(float petrolStock) {
        SystemStatus.petrolStock = petrolStock;
    }

    public static String getCurrentDate() {
        return currentDate;
    }

    public static void setCurrentDate(String currentDate) {
        SystemStatus.currentDate = currentDate;
    }

    public static String getTransactionDate() {
        return transactionDate;
    }

    public static void setTransactionDate(String transactionDate) {
        SystemStatus.transactionDate = transactionDate;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        SystemStatus.stage = stage;
    }

    public static String getBillPrinter() {
        return billPrinter;
    }

    public static void setBillPrinter(String billPrinter) {
        SystemStatus.billPrinter = billPrinter;
    }

    public static String getAnalyticsPrinter() {
        return analyticsPrinter;
    }

    public static void setAnalyticsPrinter(String analyticsPrinter) {
        SystemStatus.analyticsPrinter = analyticsPrinter;
    }
}
