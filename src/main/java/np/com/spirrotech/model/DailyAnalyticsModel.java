package np.com.spirrotech.model;

public class DailyAnalyticsModel {
    private Float pInitial,dInitial,cInitial;

    public DailyAnalyticsModel(Float pInitial, Float dInitial, Float cInitial) {
        this.pInitial = pInitial;
        this.dInitial = dInitial;
        this.cInitial = cInitial;
    }

    public Float getpInitial() {
        return pInitial;
    }

    public void setpInitial(Float pInitial) {
        this.pInitial = pInitial;
    }

    public Float getdInitial() {
        return dInitial;
    }

    public void setdInitial(Float dInitial) {
        this.dInitial = dInitial;
    }

    public Float getcInitial() {
        return cInitial;
    }

    public void setcInitial(Float cInitial) {
        this.cInitial = cInitial;
    }
}
