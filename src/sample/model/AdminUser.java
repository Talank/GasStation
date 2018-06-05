package sample.model;


import java.security.MessageDigest;

public class AdminUser {
    private  String username="",password="";

    public  void initializeAdmin(String username,String password) {
        this.username = username;
        this.password=password;
    }


    public  boolean validate(AdminUser user) {
        return user.username.equals(username)&&user.password.equals(generatePasswordHash(password));
    }

    public String generatePasswordHash(String password) {

        StringBuilder builder=new StringBuilder();
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-1");

            byte[] digested=messageDigest.digest(password.getBytes());
            for(byte b:digested) {
                builder.append(String.format("%02X",b));
            }

            System.out.println(builder.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
