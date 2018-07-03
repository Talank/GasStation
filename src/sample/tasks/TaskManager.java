package sample.tasks;

import javafx.scene.Parent;

/**
 * This class manages all the tasks performed by the system
 */

public class TaskManager {


    private static TaskQueue taskQueue;
    private static int maxThread=0;
    private static Thread mainThread;
    private static final int TIMEOUT=20000;

    private static final int MAX_RETRY=5;

    /**
     * Initializes the {@link TaskQueue} and creates a thread to poll the queue
     * @param threadSize the no of threads to create
     */

    public static void init(int threadSize) {
        maxThread=threadSize;
        taskQueue=new TaskQueue(10);

        mainThread=new Thread(()->{
            while(true) {
                if(taskQueue.isEmpty()) {
                    try {
                        System.out.println("No Task Detected.Sleep:"+TIMEOUT+" ms");
                         Thread.sleep(TIMEOUT);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted:Checking For Tasks.");
                     //   e.printStackTrace();
                    }
                } else {
                    System.out.println("Task Detected, Executing Task.");
                    Tasks task=taskQueue.deQueue();
                    if(task.getRetryStatus()<=MAX_RETRY) {
                        task.execute();
                    } else {
                        System.out.println("Task Failed Many Times. Stopping Task.");
                    }
                }
            }

        });
        mainThread.setDaemon(true);
        mainThread.start();
    }

    /**
     * Executes the task by interrupting the thread
     * @param tasks the task to be performed
     */

    public static void executeTask(Tasks tasks) {
        taskQueue.enQueue(tasks);
        mainThread.interrupt();
    }

    /**
     * @deprecated
     * @param tasks
     * @param delay
     */

    public static void scheduleTask(Tasks tasks,long delay) {
        new Thread(()->{
            try {
                Thread.sleep(delay);
                executeTask(tasks);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


}
