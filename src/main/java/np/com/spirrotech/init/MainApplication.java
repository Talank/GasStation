package np.com.spirrotech.init;

import com.sun.javafx.application.LauncherImpl;

public class MainApplication {

    /**+
     * This is the entry point for the application
     * It loads the application along with the preLoader
     * @param args command line arguments
     */

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class,ApplicationPreLoader.class,args);
    }

}
