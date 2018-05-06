package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.model.*;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class InitializeSystem extends Tasks {

    @Override
    public void execute() {

        SystemLogging.initializeLogger();//Initialize The Logger
        SystemStatus.setMainHomePath("../../view/main_home_views/mainHome.fxml");

        ArrayList<DebtListModel> arrayList=new ArrayList<>();
        try {
            Statement getDebitList=connection.createStatement();
            ResultSet resultSet=getDebitList.executeQuery("SELECT * FROM CreditCompany");
            while(resultSet.next()) {
                arrayList.add(new DebtListModel(resultSet.getString(2),resultSet.getDate(5).toString(),resultSet.getString(4),resultSet.getInt(1),resultSet.getInt(3)));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        CreditCompanyList.setCompanyList(arrayList);

        try {
            Statement getValues=connection.createStatement();
            ResultSet resultSet=getValues.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>10 AND ID<16");

            resultSet.next();
            SystemStatus.setiDiesel(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setiPetrol(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setiCounter(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setDieselStock(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setPetrolStock(Float.parseFloat(resultSet.getString(1)));
            resultSet.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        try {

            connection.setAutoCommit(false);
            Statement resetSystem=connection.createStatement();
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=1");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=2");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=3");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=4");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=5");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=6");

            resetSystem.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            Statement getRate=connection.createStatement();
            ResultSet resultSet=getRate.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>8 AND ID<11");
            resultSet.next();

            SystemStatus.setdRate((Float.parseFloat(resultSet.getString(1))));
            resultSet.next();
            SystemStatus.setpRate(Float.parseFloat(resultSet.getString(1)));
            resultSet.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
