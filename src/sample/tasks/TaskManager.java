package sample.tasks;

/**
 * This class manages all the tasks performed by the system
 */

public class TaskManager {


    private TaskQueue taskQueue;

    private static Runnable instantTask;

    private boolean isThreadFree = true;
    private static boolean isInstantTaskAvailable = false;

    private static TaskManager instance;
    private Thread mainThread;



    private TaskManager() {
        taskQueue = new TaskQueue(10);
        mainThread = new Thread(()->{
            performTask();
        });
        mainThread.setDaemon(true);
        mainThread.start();

    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    private synchronized void performTask() {
        while (true) {
            if (taskQueue.isEmpty()) {
                isThreadFree = true;
                System.out.println("No Task Detected, Entering Blocked state");
                try {
                    wait();
                } catch(InterruptedException e) {
                    System.out.println("Task Thread wakeup, Checking Instant Tasks");
                }

                if (isInstantTaskAvailable) {
                    isInstantTaskAvailable = false;
                    System.out.println("Instant Task Available.Performing Instant Task.");
                    instantTask.run();
                }
            } else {
                isThreadFree = false;
                System.out.println("Task Detected, Executing Task");
                taskQueue.deQueue().execute();
            }
        }
    }




    /**
     * Initializes the {@link TaskQueue} and creates a thread to poll the queue
     * @param threadSize the no of threads to create
     */

/*    public static void init(int threadSize) {
        maxThread=threadSize;
        taskQueue=new TaskQueue(10);

        mainThread=new Thread(()->{
            while(true) {
                if(taskQueue.isEmpty()) {
                    isThreadFree = true;
                    try {
                        System.out.println("No Task Detected.Sleep:"+TIMEOUT+" ms");
                         Thread.sleep(TIMEOUT);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted:Checking For Tasks.");
                        if (isInstantTaskAvailable) {
                            isInstantTaskAvailable = false;
                            System.out.println("Instant Task Available.Performing Instant Task.");
                            instantTask.run();
                        }
                    }
                } else {
                    isThreadFree = false;
                    System.out.println("Task Detected, Executing Task.");
                    Tasks task=taskQueue.deQueue();
                    task.execute();
                }
            }

        });
        mainThread.setDaemon(true);
        mainThread.start();
    }*/

    /**
     * Executes the task by interrupting the thread
     * @param tasks the task to be performed
     */

    public void executeTask(Tasks tasks) {
        taskQueue.enQueue(tasks);
        mainThread.interrupt();
    }

    public synchronized void executeInstantTask(Runnable instantTasks) {

        /*
        * Check if the thread is free, if so run on the free thread, otherwise run on new thread
        * */

        if (isThreadFree) {
            instantTask = instantTasks;
            isInstantTaskAvailable = true;
            notify();
        } else {
            Thread mainThread=new Thread(instantTasks);
            mainThread.setDaemon(true);
            mainThread.start();
        }

    }

}
