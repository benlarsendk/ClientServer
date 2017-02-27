/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <bla@mail.dk> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */
package ClientServer;

import java.net.Socket;

/**
 * This is the class for the server-representation of a client.
 */
public class ServerClient extends Client {
    private final Server server_;

    /**
     * CTOR for the serverclient.
     *
     * @param clientSocket
     * @param networkReceiver
     * @param server
     */
    public ServerClient(final Socket clientSocket, NetworkReceiver networkReceiver, Server server) {
        super(networkReceiver);
        clientSocket_ = clientSocket;
        server_ = server;
    }


    /**
     * Advices the server that a client has been lost. Doesn't matter if it's expected or not - he's gone.
     */
    @Override
    protected void handleExpectedDisconnect() {
        server_.lossOfClient(this);
        this.canceled_ = true;
        finalizeSockets();
    }

    /**
     * Advices the server that a client has been lost. Doesn't matter if it's expected or not - he's gone.
     * Advices the receiver that an unexpected disconnect has happened
     */
    @Override
    protected void handleUnexpectedDisconnect() {
        server_.lossOfClient(this);
        this.canceled_ = true;
        finalizeSockets();
        networkReceiver.onUnexpectedDisconnect();
    }

}
