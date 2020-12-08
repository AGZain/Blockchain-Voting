//this is a stress test of the network, it sends a significant number of messages to the network in order to add a load
import java.util.*;
import java.util.UUID;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class StressTest implements Client{


    List<Server> servers = new ArrayList<Server>();
    Server currServer;
    String uuid;
    HashSet<String>voteUUIDs = new HashSet<String>();

    public StressTest() throws RemoteException {
        super();
    }
    //main method
    public static void main(String args[]) throws Exception{
        for(int i = 0; i < 500; i++) {
            StressTest applicationClient = new StressTest();
            applicationClient.uuid = UUID.randomUUID().toString();
            applicationClient.StartServer(applicationClient.uuid);
            applicationClient.connectToServers("127.0.0.1", "test");
            applicationClient.addVoter(Integer.toString(i));
            applicationClient.submitVote("testVote", Integer.toString(i));
        }
    }
    //add a unique voter to the blockchain network
    public void addVoter(String uniqueVoterId) throws Exception{
        for(Server server : servers) 
            server.addVoter(uniqueVoterId);
    }
    //acctually submit a vote for each voter
    public void submitVote(String vote, String voteUUID) {
        try{
            voteUUIDs.add(voteUUID);
            System.out.println("sending vote for ID " + voteUUID);
            for(Server server : servers) {
                server.receiveVote(vote, this.uuid, voteUUID);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    //start a server so that the blockchain network can communicate with this stress test application
    public void StartServer(String name) {
        System.setSecurityManager(new SecurityManager());
        try {
            Client stub = (Client) UnicastRemoteObject.exportObject(this, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.bind(name, stub);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    //connect to all the nodes on the blockchain network.
    public void connectToServers(String address, String name) {
        try {
            Registry registry = LocateRegistry.getRegistry(address);
            Server server = (Server) registry.lookup(name);
            List<nodeAddress> nodes = server.getAllNodesOnNetwork();
            for(nodeAddress node : nodes) {
                Registry reg = LocateRegistry.getRegistry(node.getAddress());
                Server newServer = (Server) reg.lookup(node.getName());

                servers.add(newServer);
                newServer.registerApplication(uuid, "127.0.0.1", uuid);
            }
            this.currServer = server;
            
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    //if a node on the network requests to be the winner, agree if it is the first node
    public boolean transactionCompleted(String voteUUID) throws RemoteException {

        if(voteUUIDs.contains(voteUUID)) {
            voteUUIDs.remove(voteUUID);
            return true;
        } else {
            return false;
        }
    }



}