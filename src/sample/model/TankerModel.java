package sample.model;

public class TankerModel {

    Float avg;
    int count;
    String type;


    public TankerModel(Float avg,int count,String type){
        this.avg=avg;
        this.count=count;
        this.type=type;
    }

    public Float getAvg(){
        return avg;
    }
    public int getCount(){
        return count;
    }
    public String getType(){
        return type;
    }
}

