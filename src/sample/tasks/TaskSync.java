package sample.tasks;

public class TaskSync {


    private static TaskSync instance;

    private TaskQueue taskQueue;

    private Thread taskThread;

    private TaskSync() {
        taskQueue = new TaskQueue(10);
        taskThread = new Thread(()->performTask());
        taskThread.setDaemon(true);
        taskThread.start();
    }

    public static TaskSync getInstance() {
        if (instance == null) {
            instance = new TaskSync();
        }
        return instance;
    }

    private synchronized void performTask() {
        while (true) {
            if (taskQueue.isEmpty()) {
                try {
                    System.out.println("Waiting For Task.");
                    wait();
                } catch(InterruptedException e) {
                    System.out.println("Interrupted From Another Thread.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Task Detected");
                Tasks tasks = taskQueue.deQueue();
                tasks.execute();
            }
        }
    }

    public synchronized void executeTask(Tasks tasks) {
        System.out.println("Waking The Task Thread");
        taskQueue.enQueue(tasks);
        notify();
    }

}
