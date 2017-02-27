/**
 * Created by bla on 27-02-2017.
 */
public interface NetworkReceiver {
    public void onNewMessage(String message);
    public void onUnexpectedDisconnect();
}
