package sample.controller;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* To Handle the Logging From the application
*
*Static class is used instead of instantiate the class
*We can also create a logger using class name,but using global logger name is preferred approach
*
* */

public class SystemLogging {


    private static Logger logger=Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); //Get the Global Logger For the Application

    public static void initializeLogger() {
        try {
            logger.setLevel(Level.FINER);//Log Levels Starting from the fine to severe

            //Create log file with max size 1MB and Create only one file,and recycle it,if it is full
            logger.addHandler(new FileHandler("OilPumpLog.txt",1024*1024,1,true));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //Get the global logger
    public static Logger getLogger() {
        return logger;
    }
}
