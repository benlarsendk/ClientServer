/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <bla@mail.dk> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */
package ClientServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class for the server.
 */
public class Server extends SocketHelper {
    long clientId = 0;
    Server server_ = this;
    private int port = 0;
    private ServerSocket serverSocket_;
    private List<ServerClient> ServerClientList_ = new ArrayList<>();
    private boolean canceled_ = false;

    public Server(final NetworkReceiver networkReceiver) {
        super(networkReceiver);
    }

    /**
     * Stops listening and disconnects all ServerClients.
     */
    public void stopListening() {
        canceled_ = true;
        disconnect();
    }

    /**
     * Disconnects all ServerClients, by sending them the exepct disconnect flag
     * kills all sockets.
     */
    public void disconnect() {
        System.out.println("Disonnecting server.");
        for (ServerClient ServerClient : ServerClientList_) {
            if (ServerClient.getClientSocket() != null && ServerClient.getClientSocket().isBound()) {
                EXPECT_DISCONNECT_FLAG(true);
                ServerClient.transmitMessage(EXPECT_DISCONNECT_MSG);
            }
        }
        ServerClientList_.clear();
        finalizeSockets();

    }

    /**
     * Closes the serversocket
     */
    protected void finalizeSockets() {
        if (serverSocket_ != null && serverSocket_.isBound()) {
            try {
                if (!serverSocket_.isClosed())
                    serverSocket_.close();
            } catch (IOException e) {
                System.out.println("Couldn't close serversocket");
            }
            serverSocket_ = null;
        }
    }

    public void lossOfClient(final ServerClient serverClient) {
        this.ServerClientList_.remove(serverClient);
    }

    /**
     * Transmits a message to a ServerClient identified by its id.
     *
     * @param id  the ServerClient id
     * @param msg the message for the ServerClient.
     */
    public void transmitMessage(int id, String msg) {
        for (ServerClient ServerClient : ServerClientList_) {
            if (ServerClient.getId() == id) {
                ServerClient.transmitMessage(msg);
                return;
            }
        }
        System.out.println("Couldn't find ServerClient.");
    }

    /**
     * An expected disconnect is the case, if the server had an error while accepting a new ServerClient, but a
     * ServerClient have
     * already send a expectedDisconnect flag.
     * To solve this, the server removes the ServerClient from the ServerClientList.
     */
    @Override
    protected void handleExpectedDisconnect() {

    }

    /**
     * An unexpected disconnect is the case, if the server had an error while accepting a new ServerClient.
     * To solve the server tries to send the disconnect flag to all ServerClients, and resets itself.
     */
    @Override
    protected void handleUnexpectedDisconnect() {
        disconnect();
        startListening(port);
    }

    /**
     * The method keeps listening for new ServerClients, and when it receives a ServerClient, it listens to it.
     *
     * @param port what port to listen on.
     */
    public void startListening(int port) {
        this.port = port;
        canceled_ = false;
        new Thread(() -> {
            try {
                System.out.println("Listening for incomming connections on port " + port);
                serverSocket_ = new ServerSocket(port);
                while (!canceled_) {
                    ServerClient serverClient = new ServerClient(serverSocket_.accept(), networkReceiver, server_);
                    serverClient.listen();
                    serverClient.setIdWithoutTransmission(clientId++);
                    this.ServerClientList_.add(serverClient);
                    System.out.println("Client connected from:" + serverClient.getClientSocket().getInetAddress() +
                                               serverClient.getClientSocket().getPort());
                }

            } catch (IOException e) {
                handleDisconnect();
            }
        }).start();
    }
}
