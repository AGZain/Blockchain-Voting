//Used for the application client since it requires RMI so that nodes on the network can send messages to the client.
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.*;
import java.util.*;

public interface Client extends Remote {
    boolean transactionCompleted(String voteUUID) throws RemoteException;
}
