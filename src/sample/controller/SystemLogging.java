package sample.controller;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemLogging {
    private static Logger logger=Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void initializeLogger() {
        try {
            logger.setLevel(Level.FINER);
            logger.addHandler(new FileHandler("OilPumpLog.txt"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
