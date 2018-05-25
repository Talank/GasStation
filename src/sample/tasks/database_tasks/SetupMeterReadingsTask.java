package sample.tasks.database_tasks;

        import sample.model.SystemStatus;
        import sample.model.databases.DatabaseHandler;
        import sample.nepali_date.NepaliDateGenerator;
        import sample.tasks.Tasks;

        import java.sql.Date;
        import java.sql.PreparedStatement;
        import java.sql.SQLException;
        import java.sql.Statement;

public class SetupMeterReadingsTask extends Tasks {

    private String dieselReading,petrolReading;

    public SetupMeterReadingsTask(String dieselReading,String petrolReading) {
        this.dieselReading=dieselReading;
        this.petrolReading=petrolReading;
    }


    @Override
    public void execute() {
        try {


            if(SystemStatus.getiDiesel()!=Float.parseFloat(dieselReading)&&SystemStatus.getiPetrol()!=Float.parseFloat(petrolReading)) {

                Statement updateStock=connection.createStatement();
                updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+dieselReading+"', DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=11");
                updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+petrolReading+"', DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=12");
                updateStock.executeBatch();

                SystemStatus.setiDiesel(Float.parseFloat(dieselReading));
                SystemStatus.setiPetrol(Float.parseFloat(petrolReading));

            } else {

                if(SystemStatus.getiDiesel()!=Float.parseFloat(dieselReading)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+dieselReading+"', DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=11");
                    updateStock.execute();
                    SystemStatus.setiDiesel(Float.parseFloat(dieselReading));
                }

                if(SystemStatus.getiPetrol()!=Float.parseFloat(petrolReading)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+petrolReading+"', DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=12");
                    updateStock.execute();
                    SystemStatus.setiPetrol(Float.parseFloat(petrolReading));
                }

            }

            connection.commit();

        } catch(SQLException e) {
            System.out.println(e.getMessage());

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
        }

    }
}
