package sample;

import com.sun.javafx.application.LauncherImpl;

public class MainApplication {

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class,ApplicationPreLoader.class,args);
    }

}
