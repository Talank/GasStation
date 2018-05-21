package sample.tasks.database_tasks;

import sample.model.DebtListModel;
import sample.tasks.TaskInterface;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;



public class GetCompanyList extends Tasks {

    private TaskInterface taskInterface;

    public void addTaskInterfaceListener(TaskInterface taskInterface) {
        this.taskInterface=taskInterface;
    }



    @Override
    public void execute() {
        ArrayList<DebtListModel> arrayList=new ArrayList<>();
        try {
            Statement getDebitList=connection.createStatement();
            ResultSet resultSet=getDebitList.executeQuery("SELECT * FROM CreditCompany");
            while(resultSet.next()) {
                arrayList.add(new DebtListModel(resultSet.getString(2),resultSet.getString(8),resultSet.getString(7),resultSet.getInt(1),resultSet.getInt(3),resultSet.getInt(4),resultSet.getInt(5),resultSet.getInt(6)));
            }
            taskInterface.onComplete(arrayList);
        } catch(Exception e) {
            e.printStackTrace();
            taskInterface.onError();
        }
        for(DebtListModel model:arrayList) {
            System.out.println("Company Name:"+model.getCompanyName());
        }
    }


}
