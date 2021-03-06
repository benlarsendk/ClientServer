/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <bla@mail.dk> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * This is the class for the remote client.
 */
public class Client extends SocketHelper {
    protected boolean canceled_ = false;
    protected Socket clientSocket_;
    private String id_ = "0";
    DataOutputStream outToServer = null;
    DataInputStream inFromServer = null;


    /**
     * This constructor is used for a remote client
     *
     * @param networkReceiver
     */
    public Client(NetworkReceiver networkReceiver) {
        super(networkReceiver);
    }

    /**
     * This method stops listening, and disconnects the client.
     */
    public void stopListening() {
        this.canceled_ = true;
        disconnect();
    }

    /**
     * Disconnects the client from the server, or tells the remote client that the server is disconnecting
     * without sending the disconnect flag.
     * This method tries to send the expect disconnect flag.
     */
    public void disconnect() {
        try {
            if (clientSocket_ != null && clientSocket_.isBound()) {
                System.out.println("Client expecting disconnect.");
                EXPECT_DISCONNECT_FLAG(true);
                transmitMessage(EXPECT_DISCONNECT_MSG);
                Thread.sleep(500);
            }
            finalizeSockets();
        } catch (InterruptedException e) {
            System.out.println("Coudln't inform receiver about expected disconnect."); // Handle ?

        }
    }

    /**
     * This method transmits a message to  the server.
     *
     * @param message
     */
    public void transmitMessage(String message) {
        if (clientSocket_ == null || !clientSocket_.isBound()) {
            System.out.println("Not connected, can't transmit. Make sure you are connected to the host\nClientsocket " +
                    "is null or not bound");
            return;
        }

        try {
            if (outToServer == null)
                outToServer = new DataOutputStream(clientSocket_.getOutputStream());


            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket_.getOutputStream())), true);
            pw.println(message);
            pw.flush();

        } catch (IOException e) {
            System.out.println("Error while writing to socket - message not delivered");

        }

    }

    /**
     * Closes the client socket.
     */
    public void finalizeSockets() {
        if (clientSocket_ != null && clientSocket_.isBound()) {
            try {
                if (!clientSocket_.isClosed()) {
                    clientSocket_.close();
                    inFromServer = null;
                    outToServer = null;
                }
            } catch (IOException e) {
                System.out.println("Couldn't close clientsocket");
            }
            clientSocket_ = null;
        }

    }

    /**
     * This method connects to a host/port and listens on the socket.
     *
     * @param host
     * @param port
     */
    public void connectAndListen(String host, int port) {
        this.canceled_ = false;
        try {
            System.out.println("Conecting to " + host + ":" + port);
            clientSocket_ = new Socket(host, port);
            clientSocket_.setSoTimeout(2000);
            listen();
        } catch (IOException e) {
            System.out.println("Socket error - restarting");
        }

    }

    /**
     * Listens on the on the client socket.
     */
    public void listen() {
        this.canceled_ = false;
        new Thread(() -> {
            while (!canceled_) {
                try {
                    if (clientSocket_ == null) {
                        System.out.println("Socket is null, returning from listening");
                        return;
                    }
                    clientSocket_.setSoTimeout(0);
                    if (inFromServer == null) {
                        inFromServer = new DataInputStream(clientSocket_.getInputStream());
                    }


                    String msg = new BufferedReader(new InputStreamReader(clientSocket_.getInputStream())).readLine();


                    if (msg == null) {
                        super.handleDisconnect();
                        return;
                    }
                    if (handleMessage(msg))
                        continue;
                    networkReceiver.onNewMessage(msg);
                } catch (IOException e) {
                    super.handleDisconnect();
                }
            }
        }).start();

    }

    /**
     * Handles a message
     * returns true if the message is handled, false if not.
     *
     * @param msg
     * @return
     */
    private boolean handleMessage(final String msg) {
        if (msg.equals(EXPECT_DISCONNECT_MSG)) {
            EXPECT_DISCONNECT_FLAG(true);
            return true;
        }
        try {
            String[] data = msg.split(DEL);
            if (data[0].equals(SET_ID_MSG)) {
                String tmpId = data[ID];
                networkReceiver.onIdChange(this.id_, tmpId);
                this.id_ = tmpId;
                return true;
            }

        } catch (Exception e) {
            System.out.println("ID error.");
            return false;
        }

        return false;
    }

    /**
     * @return the clientsocket.
     */
    public Socket getClientSocket() {
        return clientSocket_;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id_;
    }

    /**
     * @param id sets the id, and transmits to the server
     */
    public void setId(final String id) {
        transmitMessage(SET_ID_MSG + DEL + id);
        id_ = id;
    }

    public void setIdWithoutTransmission(final String id) {
        id_ = id;
    }

    /**
     * The method is called when an expected disconnect has happened.
     * This can be due to server disconnecting with the expected disconnect flag or a remote client doing the same.
     */
    @Override
    protected void handleExpectedDisconnect() {
        canceled_ = true;
        System.out.println("Expected disconnect\nKilling sockets");
        finalizeSockets();
    }

    /**
     * This method is called when an unexpected disconnect has happened.
     * This can be due to the client has disconnected at the remote site
     * without raising the expected disconnect flag.
     */
    @Override
    protected void handleUnexpectedDisconnect() {
        System.out.println("Client: Unexpected disconnect\nKilling sockets.");
        finalizeSockets();
        networkReceiver.onUnexpectedDisconnect();
    }
}






