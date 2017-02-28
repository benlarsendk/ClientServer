package ClientServer;

import java.io.IOException;

public class Example {
    static String LOCALHOST = "127.0.0.1";

    public static void main(String[] args) throws InterruptedException, IOException {

        /* These receivers can handle incomming messages or errors*/
        VerboseReceiver serverReceiver = new VerboseReceiver();
        VerboseReceiver client_1_Receiver = new VerboseReceiver();
        VerboseReceiver client_2_Receiver = new VerboseReceiver();


        /*Define the client server. This would of course normally be on 2+ different hosts.*/
        Server server = new Server(serverReceiver);
        Client client_1 = new Client(client_1_Receiver);
        Client client_2 = new Client(client_2_Receiver);

        server.startListening(1234); // Set the server in listen state

        client_1.connectAndListen(LOCALHOST, 1234); // Connect - gets standard ID 0
        client_2.connectAndListen(LOCALHOST, 1234); // Connect - gets standard ID 1

        client_1.transmitMessage("Hi', I'm Client #1");
        client_2.transmitMessage("Hi', I'm Client #2");

        client_1.setId("ABC123"); // Sets the ID to 25, notfies the server.

        // Due to the asynchronous nature of the software, we sleep a little.
        Thread.sleep(50);
        System.out.println("Client 2id: " + client_2.getId());

        server.transmitMessage("ABC123", "Hi #1"); // We changed the ID from the clientside.*/
        server.transmitMessage("1", "Hi #2");


        /* DISCONNECT - RECONNECT*/

        client_1.disconnect(); // The client informs the server of an upcomming disconnect.
        client_1.connectAndListen(LOCALHOST, 1234);
        client_1.setId("ABC123"); // The server can not remember the ID.

        Thread.sleep(50);

        client_1.transmitMessage("Sorry server, I f***** up.");
        server.transmitMessage("ABC123", "Totally OK, client #1");
        
        // BAD DISCONNECT
        
        /* This would normally make the server throw an exception. In this case it will simply call the
        *  network receivers evenhandler, for unexepected disconnects*/
        client_1.getClientSocket().close();
        
        // Client 2 is still working
        server.transmitMessage("1", "Ya'll OK?");
        client_2.transmitMessage("#Persistent");
        

    }

}
