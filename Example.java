package ClientServer;

public class Example {
    static String LOCALHOST = "127.0.0.1";

    public static void main(String[] args) throws InterruptedException {

        /* These receivers can handle incomming messages or errors*/
        VerboseReceiver serverReceiver = new VerboseReceiver();
        VerboseReceiver client_1_Receiver = new VerboseReceiver();
        VerboseReceiver client_2_Receiver = new VerboseReceiver();


        /*Define the client server. This would of course normally be on 2+ different hosts.*/
        Server server = new Server(serverReceiver);
        Client client_1 = new Client(client_1_Receiver);
        Client client_2 = new Client(client_2_Receiver);

        server.startListening(1234); // Set the server in listen state

        client_1.connectAndListen(LOCALHOST, 1234); // Connect - gets standard ID 1
        client_2.connectAndListen(LOCALHOST, 1234); // Connect - gets standard ID 2

        client_1.transmitMessage("Hi', I'm Client #1");
        client_2.transmitMessage("Hi', I'm Client #2");

        client_1.setId(25); // Sets the ID to 25, notfies the server.

        // Due to the asynchronous nature of the software, we sleep a little.
        Thread.sleep(50);

        server.transmitMessage(1, "Hi #1");
        server.transmitMessage(25, "Hi #2"); // We changed the ID from the clientside.*/



        /* DISCONNECT - RECONNECT*/

        client_1.disconnect(); // The client informs the server of an upcomming disconnect.
        client_1.connectAndListen(LOCALHOST,1234);
        client_1.setId(25); // The server can not remember the ID.

        Thread.sleep(50);

        client_1.transmitMessage("Sorry server, I f***** up.");
        server.transmitMessage(25,"Totally OK, client #1");


    }

}
