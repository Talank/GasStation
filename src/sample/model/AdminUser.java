package sample.model;



public class AdminUser {
    private  String username="",password="";

    public  void initializeAdmin(String username,String password) {
        this.username = username;
        this.password=password;
    }


    public  boolean validate(AdminUser user) {
        if(user.username.equals(username)&&user.password.equals(password))
            return true;
        return false;
    }
}
