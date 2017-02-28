/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <bla@mail.dk> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */
package ClientServer;

public interface NetworkReceiver {
    void onNewMessage(String message);
    
    void onUnexpectedDisconnect();
    
    void onNewClient(String id);
    
    void onIdChange(String old, String newId);
}
