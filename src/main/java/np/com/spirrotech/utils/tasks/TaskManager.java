package np.com.spirrotech.utils.tasks;

/**
 * This class manages all the tasks performed by the system
 */

public class TaskManager {

    private static TaskManager instance;

    private TaskManager() {


    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }



    /**
     * The task is performed by the main thread, it is kept for backward compatibility, the application is now single threaded
     * @param tasks the task to be performed
     */

    public void executeTask(Tasks tasks) {
        System.out.println("Main Thread Performing The Task");
        tasks.execute();
    }

    public void executeInstantTask(Tasks tasks) {
        System.out.println("Temporary Thread performing Instant Task");
        Thread mainThread=new Thread(() -> {
            tasks.execute();
        });
        mainThread.setDaemon(true);
        mainThread.start();

    }

}
