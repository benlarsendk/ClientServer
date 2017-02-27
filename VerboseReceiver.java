package ClientServer;

/**
 * Created by bla on 27-02-2017.
 */
public class VerboseReceiver implements NetworkReceiver {
    @Override
    public void onNewMessage(final String message) {
        System.out.println("Some message: " + message);
    }

    @Override
    public void onUnexpectedDisconnect() {
        System.out.println("I should totally handle this unexpected disconnect.");
    }
}
