package sample;

import org.junit.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.*;

public class TestingClassTest {

    @Test
    public void test() {
       /* TestingClass testingClass=new TestingClass();
        testingClass.displayPrinters();*/

        DecimalFormat format = new DecimalFormat("#.00");
        float number = Float.parseFloat(format.format(2311.47565));

        System.out.println(number);

    }

}