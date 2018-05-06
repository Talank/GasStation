package sample.tasks;

public class TaskStack {




    private int size,top=-1;
    private Tasks tasks[];
    public TaskStack(int size) {
        this.size=size;
        tasks=new Tasks[size];
    }

    public synchronized void push(Tasks task) {
        if(!isFull())
            tasks[++top]=task;
        else
            System.out.println("Stack OVerFlow");
    }

    public synchronized Tasks pop() {
        if(!isEmpty())
            return tasks[top--];
        else
            return null;
    }
    public synchronized boolean isEmpty() {

        return top==-1;

    }
    public synchronized boolean isFull() {
        return top==(size-1);
    }


}
