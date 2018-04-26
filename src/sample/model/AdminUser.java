package sample.model;

import sample.model.databases.DatabaseHandler;

public class AdminUser {
    private  String username="",password="";

    public  void initializeAdmin(String username,String password) {
        this.username = username;
        this.password=password;
    }


    public  boolean validate() {
        AdminUser user=DatabaseHandler.getAdminParameters();
        if(user.username.equals(username)&&user.password.equals(password))
            return true;
        return false;
    }
}
