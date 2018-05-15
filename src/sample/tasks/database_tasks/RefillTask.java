package sample.tasks.database_tasks;

import sample.model.RefillModel;
import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;

public class RefillTask extends Tasks {

    private RefillModel model;

    private int mode;

    public RefillTask(RefillModel model,int mode) {
        this.model=model;
        this.mode=mode;
    }

    @Override
    public void execute() {
        try {

            PreparedStatement insertRefill;

            if(mode==0) {
                insertRefill=connection.prepareStatement("INSERT INTO DIESEL_REFILL VALUES (?,?,?,?)");
            } else {
                insertRefill=connection.prepareStatement("INSERT INTO PETROL_REFILL VALUES (?,?,?,?)");
            }
            insertRefill.setFloat(1,model.getExpected());
            insertRefill.setFloat(2,model.getActual());
            insertRefill.setFloat(3,model.getLoss());
            insertRefill.setString(4,SystemStatus.getCurrentDate());
            insertRefill.execute();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
