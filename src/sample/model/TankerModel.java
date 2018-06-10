package sample.model;

public class TankerModel {

    private Float avg;
    private int count;
    private String type,tankerNumber;


    public TankerModel(Float avg, int count, String type, String tankerNumber) {
        this.avg = avg;
        this.count = count;
        this.type = type;
        this.tankerNumber = tankerNumber;
    }

    public Float getAvg() {
        return avg;
    }

    public void setAvg(Float avg) {
        this.avg = avg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTankerNumber() {
        return tankerNumber;
    }

    public void setTankerNumber(String tankerNumber) {
        this.tankerNumber = tankerNumber;
    }

    public float getLoss() {
        return avg*count;
    }
}

