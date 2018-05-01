package sample.model;

import java.sql.Date;

public class RefillModel {

    private Float expectedDiesel,actualDiesel,expectedPetrol,actualPetrol;
    private Float dieselLoss,petrolLoss;
    private Date date;

    public RefillModel(Float expectedDiesel, Float actualDiesel, Float expectedPetrol, Float actualPetrol, Float dieselLoss, Float petrolLoss, Date date) {
        this.expectedDiesel = expectedDiesel;
        this.actualDiesel = actualDiesel;
        this.expectedPetrol = expectedPetrol;
        this.actualPetrol = actualPetrol;
        this.dieselLoss = dieselLoss;
        this.petrolLoss = petrolLoss;
        this.date = date;
    }

    public Float getExpectedDiesel() {
        return expectedDiesel;
    }

    public void setExpectedDiesel(Float expectedDiesel) {
        this.expectedDiesel = expectedDiesel;
    }

    public Float getActualDiesel() {
        return actualDiesel;
    }

    public void setActualDiesel(Float actualDiesel) {
        this.actualDiesel = actualDiesel;
    }

    public Float getExpectedPetrol() {
        return expectedPetrol;
    }

    public void setExpectedPetrol(Float expectedPetrol) {
        this.expectedPetrol = expectedPetrol;
    }

    public Float getActualPetrol() {
        return actualPetrol;
    }

    public void setActualPetrol(Float actualPetrol) {
        this.actualPetrol = actualPetrol;
    }

    public Float getDieselLoss() {
        return dieselLoss;
    }

    public void setDieselLoss(Float dieselLoss) {
        this.dieselLoss = dieselLoss;
    }

    public Float getPetrolLoss() {
        return petrolLoss;
    }

    public void setPetrolLoss(Float petrolLoss) {
        this.petrolLoss = petrolLoss;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
