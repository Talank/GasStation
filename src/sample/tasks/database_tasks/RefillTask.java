package sample.tasks.database_tasks;

import sample.model.RefillModel;
import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.Statement;

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

            connection.setAutoCommit(false);

            Statement insertRefill=connection.createStatement();

            if(mode==0) {
                insertRefill.addBatch("INSERT INTO DieselRefill VALUES ("+model.getExpected()+","+model.getActual()+","+model.getLoss()+",'"+SystemStatus.getCurrentDate()+"')");
                insertRefill.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(SystemStatus.getDieselStock()+model.getActual())+"', DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=14");

            } else {
                insertRefill.addBatch("INSERT INTO PetrolRefill VALUES ("+model.getExpected()+","+model.getActual()+","+model.getLoss()+",'"+SystemStatus.getCurrentDate()+"')");
                insertRefill.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(SystemStatus.getPetrolStock()+model.getActual())+"', DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=15");

            }

            insertRefill.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
