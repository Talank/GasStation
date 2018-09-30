package sample;


import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.io.IOException;
import java.security.Key;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**+
 * This class verifies the application status, so that the application cannot be pirated
 *
 */

public class SystemValidation {

    private String serialNumber="";

    public SystemValidation() {

        try {
            String processorID, diskSerialNumber;
            Process process=Runtime.getRuntime().exec(new String[] {"wmic","cpu","get","processorid"});
            process.getOutputStream().close();
            Scanner sc=new Scanner(process.getInputStream());
            sc.next();
            processorID = sc.next();
            process = Runtime.getRuntime().exec(new String[] {"wmic","diskdrive", "get", "serialnumber"});
            process.getOutputStream().close();
            sc = new Scanner(process.getInputStream());
            sc.next();
            diskSerialNumber = sc.next();

            System.out.println("ProcessorID:" + processorID);
            System.out.println("DiskDrive: " + diskSerialNumber);

            StringBuilder processorKey=new StringBuilder(processorID);
            int length=processorKey.length();

            if (length <= 16) {
                length=16-length;
                for(int i=0;i<length;i++) {
                    processorKey.append("X");
                }
            } else {
                processorKey=processorKey.delete(15,length-1);
            }

            StringBuilder driveKey = new StringBuilder(diskSerialNumber);
            length = driveKey.length();

            if (length <= 16) {
                length=16-length;
                for(int i=0;i<length;i++) {
                    driveKey.append("X");
                }
            } else {
                driveKey=driveKey.delete(15,length-1);
            }

            processorID = processorKey.toString();
            diskSerialNumber = driveKey.toString();

            System.out.println("Formatted ProcessorID: " + processorID);
            System.out.println("Formatted DiskDrive: " + diskSerialNumber);

            char[] charArray = diskSerialNumber.toCharArray();

            for (int i = 0; i < 16; i++) {
                if (i%2 == 0) {
                    charArray[i] = processorID.charAt(i);
                }
            }

            serialNumber = new String(charArray);

        } catch(Exception e) {
            e.printStackTrace();
        }


    }


    public boolean validate(String originalString) {

        try {
            if(!serialNumber.isEmpty()) {
                Cipher cipher=Cipher.getInstance("AES");

                System.out.println("Key String:"+serialNumber);

                Key key=new SecretKeySpec(serialNumber.getBytes(),"AES");

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

    public void testing() {
        try {
            String processorID, diskSerialNumber;
            Process process=Runtime.getRuntime().exec(new String[] {"wmic","cpu","get","processorid"});
            process.getOutputStream().close();
            Scanner sc=new Scanner(process.getInputStream());
            sc.next();
            //processorID = sc.next();
            processorID = "BFEBFBFF000506E3";
            process = Runtime.getRuntime().exec(new String[] {"wmic","diskdrive", "get", "serialnumber"});
            process.getOutputStream().close();
            sc = new Scanner(process.getInputStream());
            sc.next();
            //diskSerialNumber = sc.next();
            diskSerialNumber = "161633443794";

            System.out.println("ProcessorID:" + processorID);
            System.out.println("DiskDrive: " + diskSerialNumber);
            try {
                Cipher cipher=Cipher.getInstance("AES");
                StringBuilder processorKey=new StringBuilder(processorID);
                int length=serialNumber.length();

                if (length <= 16) {
                    length=16-length;
                    for(int i=0;i<length;i++) {
                        processorKey.append("X");
                    }
                } else {
                    processorKey=processorKey.delete(15,length-1);
                }

                StringBuilder driveKey = new StringBuilder(diskSerialNumber);
                length = driveKey.length();

                if (length <= 16) {
                    length=16-length;
                    for(int i=0;i<length;i++) {
                        driveKey.append("X");
                    }
                } else {
                    driveKey=driveKey.delete(15,length-1);
                }

                processorID = processorKey.toString();
                diskSerialNumber = driveKey.toString();

                System.out.println("Formatted ProcessorID: " + processorID);
                System.out.println("Formatted DiskDrive: " + diskSerialNumber);

                char[] charArray = diskSerialNumber.toCharArray();

                for (int i = 0; i < 16; i++) {
                    if (i%2 == 0) {
                        charArray[i] = processorID.charAt(i);
                    }
                }

                String plainText = new String(charArray);
                System.out.println("Plain Text:" + plainText);

                Key key=new SecretKeySpec(plainText.getBytes(),"AES");

                cipher.init(Cipher.ENCRYPT_MODE,key);

                byte[] encryptedData=cipher.doFinal("Spirrotech".getBytes());

                for(byte b:encryptedData) {
                    System.out.println(b);
                }


            } catch(Exception e) {
                e.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

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
