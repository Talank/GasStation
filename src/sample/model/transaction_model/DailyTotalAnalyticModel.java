package sample.model.transaction_model;

import javafx.beans.property.SimpleStringProperty;

public class DailyTotalAnalyticModel {

    private SimpleStringProperty iDieselVol,iPetrolVol,cDieselVol,cPetrolVol,iExtra,cExtra,extraIncome, oilIncome,total,date;



    public DailyTotalAnalyticModel() {

        iPetrolVol=new SimpleStringProperty();
        iDieselVol=new SimpleStringProperty();
        cDieselVol=new SimpleStringProperty();
        cPetrolVol=new SimpleStringProperty();
        extraIncome=new SimpleStringProperty();
        oilIncome =new SimpleStringProperty();
        total=new SimpleStringProperty();
        date=new SimpleStringProperty();
        iExtra=new SimpleStringProperty();
        cExtra=new SimpleStringProperty();
    }

    public DailyTotalAnalyticModel(String iDieselVol,String iPetrolVol,String cDieselVol, String cPetrolVol, String extraIncome, String creditOilIncome, String total, String date) {
        this.iDieselVol=new SimpleStringProperty(iDieselVol);
        this.iPetrolVol=new SimpleStringProperty(iPetrolVol);
        this.cDieselVol = new SimpleStringProperty(cDieselVol);
        this.cPetrolVol = new SimpleStringProperty(cPetrolVol);
        this.extraIncome = new SimpleStringProperty(extraIncome);
        this.oilIncome = new SimpleStringProperty(creditOilIncome);
        this.total = new SimpleStringProperty(total);
        this.date = new SimpleStringProperty(date);
    }


    public String getIDieselVol() {
        return iDieselVol.get();
    }

    public void setIDieselVol(String iDieselVol) {
        this.iDieselVol.set(iDieselVol);
    }

    public String getIPetrolVol() {
        return iPetrolVol.get();
    }

    public void setIPetrolVol(String iPetrolVol) {
        this.iPetrolVol.set(iPetrolVol);
    }


    public String getCDieselVol() {
        return cDieselVol.get();
    }

    public void setCDieselVol(String cDieselVol) {
        this.cDieselVol.set(cDieselVol);
    }

    public String getCPetrolVol() {
        return cPetrolVol.get();
    }

    public void setCPetrolVol(String cPetrolVol) {
        this.cPetrolVol.set(cPetrolVol);
    }

    public String getExtraIncome() {
        return extraIncome.get();
    }

    public void setExtraIncome(String extraIncome) {
        this.extraIncome.set(extraIncome);
    }

    public String getOilIncome() {
        return oilIncome.get();
    }

    public void setOilIncome(String oilIncome) {
        this.oilIncome.set(oilIncome);
    }

    public String getTotal() {
        return total.get();
    }

    public void setTotal(String total) {
        this.total.set(total);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getIExtra() {
        return iExtra.get();
    }

    public void setIExtra(String iExtra) {
        this.iExtra.set(iExtra);
    }

    public String getCExtra() {
        return cExtra.get();
    }

    public void setCExtra(String cExtra) {
        this.cExtra.set(cExtra);
    }
}
