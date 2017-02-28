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

    @Override
    public void onNewClient(final String id) {
        System.out.println("Oh, a new client with ID: " + id);
    }

    @Override
    public void onIdChange(final String old, final String newId) {
        System.out.println("Old client: " + old + " got new ID: " + newId);
    }
}
