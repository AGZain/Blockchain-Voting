//This is the interface for the server, which are the nodes on the blockchain itself
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.*;
import java.util.*;

public interface Server extends Remote {
    void registerNeighbor(String host, String name) throws RemoteException;
    List<nodeAddress> getAllNodesOnNetwork() throws RemoteException;
    void registerApplication(String uuid, String address, String name) throws RemoteException;
    void receiveVote(String vote, String applicationUUID, String voteUUID) throws RemoteException;
    void receiveBlockAndPOW(String POW, Block block) throws RemoteException;
    List<Block> getLatestBlockChain() throws RemoteException;
    String getLatestHash() throws RemoteException;
    void addVoter(String uniqueVoterId) throws RemoteException;
    void addCandidate(String candidate) throws RemoteException;
    HashSet<String> getVoters() throws RemoteException;
    List<String> getCandidates() throws RemoteException;
    boolean verifyVoter(String voterId) throws RemoteException;
}
