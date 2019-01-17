package np.com.spirrotech.dao;

public class RateModel {
    private float rDiesel,rPetrol;

    public RateModel(float rDiesel, float rPetrol) {
        this.rDiesel = rDiesel;
        this.rPetrol = rPetrol;
    }

    public float getrDiesel() {
        return rDiesel;
    }

    public void setrDiesel(float rDiesel) {
        this.rDiesel = rDiesel;
    }

    public float getrPetrol() {
        return rPetrol;
    }

    public void setrPetrol(float rPetrol) {
        this.rPetrol = rPetrol;
    }

}
