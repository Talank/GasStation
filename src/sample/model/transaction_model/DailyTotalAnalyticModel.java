package sample.model.transaction_model;

import javafx.beans.property.SimpleStringProperty;

import java.awt.geom.FlatteningPathIterator;

public class DailyTotalAnalyticModel {

    private SimpleStringProperty iDieselVol,iPetrolVol,cDieselVol,cPetrolVol,cExtra,extraIncome, oilIncome,total,date;



    public DailyTotalAnalyticModel() {

        iPetrolVol=new SimpleStringProperty();
        iDieselVol=new SimpleStringProperty();
        cDieselVol=new SimpleStringProperty();
        cPetrolVol=new SimpleStringProperty();
        extraIncome=new SimpleStringProperty();
        oilIncome =new SimpleStringProperty();
        total=new SimpleStringProperty();
        date=new SimpleStringProperty();
        cExtra=new SimpleStringProperty();
    }

    public DailyTotalAnalyticModel(String iDieselVol,String iPetrolVol,String cDieselVol, String cPetrolVol, String extraIncome, String date) {
        this.iDieselVol=new SimpleStringProperty(iDieselVol);
        this.iPetrolVol=new SimpleStringProperty(iPetrolVol);
        this.cDieselVol = new SimpleStringProperty(cDieselVol);
        this.cPetrolVol = new SimpleStringProperty(cPetrolVol);
        this.extraIncome = new SimpleStringProperty(extraIncome);
        this.oilIncome = new SimpleStringProperty();
        this.total = new SimpleStringProperty();
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

    public void setOilIncome(float dRate,float pRate) {
        float total=(Float.parseFloat(iDieselVol.get())+Float.parseFloat(cDieselVol.get()))*dRate+(Float.parseFloat(iPetrolVol.get())+Float.parseFloat(cPetrolVol.get()))*pRate;
        this.oilIncome.set(String.valueOf(total));
    }

    public String getTotal() {
        return total.get();
    }

    public void setTotal() {
        float total=Float.parseFloat(oilIncome.get())+Float.parseFloat(extraIncome.get());
        this.total.set(String.valueOf(total));
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }


    public String getCExtra() {
        return cExtra.get();
    }

    public void setCExtra(String cExtra) {
        this.cExtra.set(cExtra);
    }
}
