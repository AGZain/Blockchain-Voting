import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.*;
import java.util.*;

public interface Client extends Remote {
    boolean transactionCompleted(String voteUUID) throws RemoteException;
}
