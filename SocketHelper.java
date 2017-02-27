/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <bla@mail.dk> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */
package ClientServer;

abstract class SocketHelper {
    protected final NetworkReceiver networkReceiver;
    protected final int DISCONNECT_TIME_MS = 5000;
    protected final String EXPECT_DISCONNECT_MSG = "EXPECT_DISCONNECT";
    protected final String SET_ID_MSG = "SET_ID";
    protected final int ID = 1;
    protected final String DEL = ":";
    private boolean EXPECT_DISCONNECT_FLAG = false;

    /**
     * Default CTOR to set the receiver for messages and errors.
     *
     * @param networkReceiver: the network receiver
     */
    public SocketHelper(NetworkReceiver networkReceiver) {
        this.networkReceiver = networkReceiver;
    }

    /**
     * Simply handles a disconnect for any reason.
     * Calls the appropriate handler method on the server/client
     * In case it was an expected disconnect, it resets the disconnectflag.
     */
    protected void handleDisconnect() {
        if (EXPECT_DISCONNECT_FLAG) {
            EXPECT_DISCONNECT_FLAG(false);
            handleExpectedDisconnect();
        } else {
            handleUnexpectedDisconnect();
        }
    }

    /**
     * Sets the disconnect flag.
     * Starts a new thread that resets the flag after DISCONNECT_TIME_MS time.
     *
     * @param expect_disconnect_flag: True or false
     */
    protected void EXPECT_DISCONNECT_FLAG(boolean expect_disconnect_flag) {
        if (!expect_disconnect_flag) {
            EXPECT_DISCONNECT_FLAG = false;
        } else {
            if (EXPECT_DISCONNECT_FLAG) return; // Shouldn't happen.
            else {
                System.out.println("Expecting disconnect.");
                EXPECT_DISCONNECT_FLAG = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(DISCONNECT_TIME_MS); // Give the disconnecter a couple of seconds to disconnect,
                        // or reset flag
                    } catch (InterruptedException e) {
                        EXPECT_DISCONNECT_FLAG = false;
                    }
                    EXPECT_DISCONNECT_FLAG = false;
                }).start();
            }
        }
    }

    protected abstract void handleExpectedDisconnect();

    protected abstract void handleUnexpectedDisconnect();

}