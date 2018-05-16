package sample;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestingClassTest {

    @Test
    public void test() {
        TestingClass testingClass=new TestingClass();
        testingClass.displayPrinters();
    }

}