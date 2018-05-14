package sample.nepali_date;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class NepaliDateGeneratorTest {

    @Test
    public void test() {
        NepaliDateGenerator generator=new NepaliDateGenerator(LocalDate.of(2018,8,26));
        //generator.convertDate();
    }

}