package sample.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class AdminUserTest {

    @Test
    public void generatePasswordHash() {

        AdminUser user=new AdminUser();
        user.generatePasswordHash("12345");

    }
}