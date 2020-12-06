import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.*;
import java.util.*;

public interface Server extends Remote {
    void registerNeighbor(String host, String name) throws RemoteException;
    List<nodeAddress> getAllNodesOnNetwork() throws RemoteException;
    void registerApplication(String uuid, String address, String name) throws RemoteException;
    void receiveVote(String vote, String applicationUUID, String voteUUID) throws RemoteException;
}
