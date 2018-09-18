package sample.library;

public class SettingsConstants {

    public static final int DIESEL_RATE = 9,
    PETROL_RATE = 10,
    USER_NAME = 7,
    PASSWORD = 8,
    COUNTER_STOCK = 13,
    DIESEL_METER_READING = 11,
    PETROL_METER_READING = 12,
    DIESEL_STOCK = 14,
    PETROL_STOCK = 15,
    BILL_PRINTER = 16,
    ANALYTICS_PRINTER = 17,
    CREDIT_DIESEL_VOLUME = 4,
    CREDIT_PETROL_VOLUME = 5,
    CREDIT_EXTRA_PRICE = 6;

    public static final String[] CHANGE_OIL_RATE = {
            "Oil Rate Update Error",
            "Error Changing Oil Rates.\n"+"Please Retry Again."
    },
    CHANGE_USER_PARAMS = {
            "User Parameter Error",
            "Error Changing User Parameters.\n"+"Please Retry Again."
    },
    CHANGE_COUNTER_STOCK = {
            "Counter Setup Error",
            "Error Setting Up Counter Value.\n"+"Please Retry Again."
    },
    CHANGE_METER_READING = {
            "Meter Readings Error",
            "Error Setting Up Final Meter Readings.\n" + "Please Retry Again."
    },
    CHANGE_OIL_STOCK = {
            "Oil Stock Error",
            "Error Setting up Oil Stock.\n"+"Please Retry Again."
    },
    CHANGE_PRINTER = {
            "Printer Setup Error",
            "Error Setting Up Printers.\n"+"Please Retry Again."
    },
    CLEAR_EXPENDITURE = {
            "Expenditure Error",
            "Error Clearing Expenditure.\n"+"Please Retry Again."
    },
    CLEAR_DAILY_ANALYTICS = {
            "Daily Analytics Error",
            "Error Clearing Daily Analytics.\n"+"Please Retry Again."
    },
    CLEAR_DIESEL_REFILL = {
            "Diesel Refill Error",
            "Error Clearing Diesel Refill.\n"+"Please Retry Again."
    },
    CLEAR_PETROL_REFILL = {
            "Petrol Refill Error",
            "Error Clearing Petrol Refill.\n"+"Please Retry Again."
    };

    public static final String EXPENDITURE_TABLE = "Expenditure",
    DAILY_ANALYTICS_TABLE = "DailyAnalytics",
    DIESEL_REFILL_TABLE = "DieselRefill",
    PETROL_REFILL_TABLE = "PetrolRefill";


}
