package sample.tasks;

public interface TaskInterface<T> {

    void onComplete(T data);

    void onError();

}
