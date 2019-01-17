package np.com.spirrotech.dao;

public class TankCheckModel {

    private int tankNo,counter;
    private float tankLoss;


    public TankCheckModel(int tankNo,float tankLoss,int counter){
        this.tankNo=tankNo;
        this.tankLoss=tankLoss;
        this.counter=counter;
    }


    public int getTankNo(){
        return tankNo;
    }
    public float getTankLoss(){
        return tankLoss;
    }

    public int getCounter(){
        return counter;
    }

}
