package sample.tasks;

import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.io.IOException;
import java.util.Scanner;

public class TaskQueue {

    private int rear=0,front=0;
    private boolean enQueued =false;
    private Tasks[] tasks;


    public TaskQueue(int size) {
        tasks=new Tasks[size];
    }

    public synchronized void enQueue(Tasks task) {

        if(isFull()) {
            System.out.println("Queue OverFlow!!!");
        } else {
            enQueued=true;
            tasks[rear]=task;
            rear=(rear+1)%tasks.length;
        }
    }


    public synchronized boolean isFull() {
        return ((rear==front)&& enQueued);
    }

    public synchronized boolean isEmpty() {
        return ((front==rear)&&!enQueued);
    }

    public synchronized Tasks deQueue() {
        if(isEmpty()) {
            System.out.println("Queue UnderFlow!!!");
        } else {
            enQueued=false;
            Tasks data=tasks[front];
            front=(front+1)%tasks.length;
            return data;
        }
        return null;
    }


}
