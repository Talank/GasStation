package np.com.spirrotech.model;

import np.com.spirrotech.dao.CreditDetailsModel;
import np.com.spirrotech.utils.nepali_date.NepaliDateGenerator;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GetCompanyDetailsTask extends Tasks {


    private String tableName;

    public GetCompanyDetailsTask(String tableName) {
        this.tableName=tableName;
    }

    @Override
    public void execute() {
        List<List<CreditDetailsModel>> mainList=new ArrayList<>();
        int totalSize,currentSize=0;
        try {

            //get the total no of rows in the company table
            Statement getDetails= Tasks.getConnection().createStatement();
            ResultSet resultSet=getDetails.executeQuery("SELECT count(*) FROM "+tableName);

            resultSet.next();

            totalSize=resultSet.getInt(1);

            System.out.println("Total Size:"+totalSize);


            //select all the data from the table name
            resultSet=getDetails.executeQuery("SELECT * FROM "+tableName);

            List<CreditDetailsModel> smallList=new ArrayList<>();
            int  i=1;//index used to group the transactions having same TransactionID
            boolean create=false;

            NepaliDateGenerator generator=new NepaliDateGenerator();

            CreditDetailsModel model;
            //iterate through all the rows in the table
            while(resultSet.next()) {

                //check if the task is cancelled
                if(isCancelled()) {
                    System.out.println("Task was requested to cancel. Cleaning up config.");
                    mainList.clear();//clear the mainList (free memory)
                    break;
                } else {
                    if(i==resultSet.getInt(1)) {

                        if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {//the row is for oil transaction


                            //add the data to smallList
                            model = new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4));
                            model.setTransactionID(resultSet.getInt(1));
                            model.setSqlDate(resultSet.getDate(7));
                            smallList.add(model);
                        } else {

                            //add the data to smallList
                            model = new CreditDetailsModel("XXXX",resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4));
                            model.setTransactionID(resultSet.getInt(1));
                            model.setSqlDate(resultSet.getDate(7));
                            smallList.add(model);
                        }

                    } else {//the index is different from the initial index, i.e this is the next transaction
                        mainList.add(smallList); //add the list to the mainList
                        create=true; //to indicate to create another object for storing the next transaction set
                        i++; //increment the index
                    }
                    if(create) {

                        smallList=new ArrayList<>(); //create a new ArrayList()

                        //we are adding the result to the smallList, because we are in the iteration and , there is data in the current iteration
                        if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {

                            model = new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4));
                            model.setTransactionID(resultSet.getInt(1));
                            model.setSqlDate(resultSet.getDate(7));
                            smallList.add(model);
                        } else {

                            model = new CreditDetailsModel("XXXX",resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4));
                            model.setTransactionID(resultSet.getInt(1));
                            model.setSqlDate(resultSet.getDate(7));
                            smallList.add(model);
                        }
                        create=false;
                    }

                    //for the progress layout, update the progress in the dialog
                    currentSize++;
                    updateProgress(currentSize,totalSize);
                }

            }

            //add the last transaction arrayList to the main list
            mainList.add(smallList);

            //cleanup
            resultSet.close();
            getDetails.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        taskInterface.onComplete(mainList);
    }

}
