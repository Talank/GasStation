package sample;

import org.junit.Test;

import static org.junit.Assert.*;

public class SystemValidationTest {

    @Test
    public void testSystem() {
        SystemValidation validation=new SystemValidation();
        validation.generateHash();

    }

}