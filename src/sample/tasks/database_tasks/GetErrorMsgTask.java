package sample.tasks.database_tasks;

import sample.tasks.Tasks;

import java.sql.SQLException;

public class GetErrorMsgTask extends Tasks {

    private String msg;
     public GetErrorMsgTask(String msg){
         this.msg=msg;
     }

    @Override
    public void execute() {
            if(taskInterface!=null)
                taskInterface.onComplete(msg);

    }
}
