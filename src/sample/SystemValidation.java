package sample;


import java.security.Key;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SystemValidation {

    private String serialNumber="";

    public SystemValidation() {
        try {
            Process process=Runtime.getRuntime().exec(new String[] {"wmic","bios","get","serialnumber"});
            process.getOutputStream().close();
            Scanner sc=new Scanner(process.getInputStream());
            sc.next();
            serialNumber = sc.next();
            System.out.println("System Serial Number:"+serialNumber);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public boolean validate(String originalString) {

        try {
            if(!serialNumber.isEmpty()) {
                Cipher cipher=Cipher.getInstance("AES");
                StringBuilder keyString=new StringBuilder(serialNumber);
                int length=serialNumber.length();
                if(length<=16) {
                    length=16-length;
                    for(int i=0;i<length;i++) {
                        keyString.append("X");
                    }
                } else {
                    keyString=keyString.delete(16,length-1);
                }

                System.out.println("Key String:"+keyString.toString());

                Key key=new SecretKeySpec(keyString.toString().getBytes(),"AES");

                byte[] encryptedData=Reference.serialByte;

                cipher.init(Cipher.DECRYPT_MODE,key);


                String computedHash=new String(cipher.doFinal(encryptedData));

                if(computedHash.equals(originalString)) {
                    System.out.println("Hash Matched");
                    return true;
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void generateHash() {

        try {
            Cipher cipher=Cipher.getInstance("AES");
            StringBuilder keyString=new StringBuilder(serialNumber);
            int length=serialNumber.length();
            if(length<=16) {
                length=16-length;
                for(int i=0;i<length;i++) {
                    keyString.append("X");
                }
            } else {
                keyString=keyString.delete(16,length-1);
            }
            Key key=new SecretKeySpec(keyString.toString().getBytes(),"AES");

            cipher.init(Cipher.ENCRYPT_MODE,key);

            byte[] encryptedData=cipher.doFinal("Spirrotech".getBytes());

            for(byte b:encryptedData) {
                System.out.println(b);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }


    }


    public String getSystemSerialNumber() {
        return serialNumber;
    }


}
