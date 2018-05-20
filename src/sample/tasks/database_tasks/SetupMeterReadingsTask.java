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


            if(SystemStatus.getDieselStock()!=Float.parseFloat(dieselReading)&&SystemStatus.getPetrolStock()!=Float.parseFloat(petrolReading)) {
                connection.setAutoCommit(false);

                Statement updateStock=connection.createStatement();
                updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+dieselReading+"', DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=11");
                updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+petrolReading+"', DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=12");
                updateStock.executeBatch();

                connection.commit();

                connection.setAutoCommit(true);
                SystemStatus.setDieselStock(Float.parseFloat(dieselReading));
                SystemStatus.setPetrolStock(Float.parseFloat(petrolReading));


            } else {

                if(SystemStatus.getDieselStock()!=Float.parseFloat(dieselReading)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+dieselReading+"', DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=11");
                    updateStock.execute();
                    SystemStatus.setDieselStock(Float.parseFloat(dieselReading));
                }

                if(SystemStatus.getPetrolStock()!=Float.parseFloat(petrolReading)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+petrolReading+"', DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=12");
                    updateStock.execute();
                    SystemStatus.setPetrolStock(Float.parseFloat(petrolReading));
                }

            }


        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
