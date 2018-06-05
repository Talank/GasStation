package sample.model.nepaliPriceFormat;

import org.junit.Test;

import static org.junit.Assert.*;

public class PriceFormatTest {

    @Test
    public void check() {
        PriceFormat format=new PriceFormat("12454343.94364");
        System.out.println("Output:"+format.getResult());
    }

}