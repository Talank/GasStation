package np.com.spirrotech.utils.tasks;

public interface TaskInterface<T> {

    void onComplete(T data);

    void onError(String msg);

    void updateProgress(double current, double total);

}
