package sample.nepali_date;

import org.junit.Test;

import static org.junit.Assert.*;

public class ParseNepaliDateTest {


    @Test
    public void test() {
        ParseNepaliDate date=new ParseNepaliDate("TUESDAY,JESTHA 1,2075");

        date=new ParseNepaliDate("TUESDAY,ASHAR 12,2075");



    }

}