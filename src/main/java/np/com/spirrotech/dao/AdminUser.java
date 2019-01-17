package np.com.spirrotech.dao;


import java.security.MessageDigest;

public class AdminUser {
    private  String username="",password="";

    public AdminUser(){

    }

    public  void initializeAdmin(String username,String password) {
        this.username = username;
        this.password=password;
    }


    public  boolean validate(AdminUser user) {

        return user.username.trim().equals(username)&&user.password.trim().equals(generatePasswordHash(password));
    }

    public String generatePasswordHash(String password) {

        StringBuilder builder=new StringBuilder();
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-1");

            byte[] digested=messageDigest.digest(password.getBytes());
            for(byte b:digested) {
                builder.append(String.format("%02X",b));
            }

            System.out.println("The value is "+builder.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
