import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.*;
import java.util.*;

public interface Server extends Remote {
    String registerNeighbor(String host) throws RemoteException;
    List<nodeAddress> getAllNodesOnNetwork() throws RemoteException;
}
