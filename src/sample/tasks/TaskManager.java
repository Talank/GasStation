package sample.tasks;

import javafx.scene.Parent;

import java.io.IOException;

public class TaskManager {


    private static TaskQueue taskQueue;
    private static int maxThread=0;
    private static Thread mainThread;

    private static final int MAX_RETRY=5;

    public static void init(int threadSize) {
        maxThread=threadSize;
        taskQueue=new TaskQueue(10);

        mainThread=new Thread(()->{
            while(true) {
                if(taskQueue.isEmpty()) {
                    try {
                        System.out.println("No Task Detected.Sleep:5s");
                         Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted:Checking For Tasks.");
                     //   e.printStackTrace();
                    }
                } else {
                    System.out.println("Task Detected, Executing Task.");
                    Tasks task=taskQueue.deQueue();
                    if(task.getRetryStatus()<=MAX_RETRY) {
                        try {
                            task.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Task Failed Many Times. Stopping Task.");
                    }
                }
            }

        });
        mainThread.setDaemon(true);
        mainThread.start();
    }

    public static void executeTask(Tasks tasks) {
        taskQueue.enQueue(tasks);
        mainThread.interrupt();
    }

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
