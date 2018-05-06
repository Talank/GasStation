package sample.tasks.database_tasks;

import sample.model.RefillModel;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;

public class RefillTask extends Tasks {

    private RefillModel model;

    public RefillTask(RefillModel model) {
        this.model=model;
    }

    @Override
    public void execute() {
        try {
            PreparedStatement insertRefill=connection.prepareStatement("INSERT INTO Refill VALUES (?,?,?,?,?,?,?)");
            insertRefill.setFloat(1,model.getExpectedDiesel());
            insertRefill.setFloat(2,model.getActualDiesel());
            insertRefill.setFloat(3,model.getExpectedPetrol());
            insertRefill.setFloat(4,model.getActualPetrol());
            insertRefill.setFloat(5,model.getDieselLoss());
            insertRefill.setFloat(6,model.getPetrolLoss());
            insertRefill.setDate(7,model.getDate());
            insertRefill.execute();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
