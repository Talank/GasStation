package sample.tasks.database_tasks;

        import sample.library.ErrorMessageDialogGenerator;
        import sample.library.SnackBarToastGenerator;
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

                PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                updateStock.setString(1,dieselReading);
                updateStock.setDate(2,SystemStatus.getCurrentDate());
                updateStock.setInt(3,11);

                updateStock.addBatch();

                updateStock.setString(1,petrolReading);
                updateStock.setInt(3,12);

                updateStock.addBatch();

                updateStock.executeBatch();

                updateStock.close();

            } else {

                if(SystemStatus.getiDiesel()!=Float.parseFloat(dieselReading)) {

                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
                    updateStock.setString(1,dieselReading);
                    updateStock.setDate(2,SystemStatus.getCurrentDate());
                    updateStock.setInt(3,11);
                    updateStock.execute();

                    updateStock.close();
                }

                if(SystemStatus.getiPetrol()!=Float.parseFloat(petrolReading)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
                    updateStock.setString(1,petrolReading);
                    updateStock.setDate(2,SystemStatus.getCurrentDate());
                    updateStock.setInt(3,12);

                    updateStock.execute();

                    updateStock.close();
                }

            }

            connection.commit();

            SystemStatus.setiDiesel(Float.parseFloat(dieselReading));
            SystemStatus.setiPetrol(Float.parseFloat(petrolReading));

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Meter Reading Saved",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            System.out.println(e.getMessage());

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Setting Up Final Meter Readings.\n"+"Please Retry Again.";
            String title="Meter Readings Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }

    }
}
