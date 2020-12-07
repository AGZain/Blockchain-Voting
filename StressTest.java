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

    public void addVoter(String uniqueVoterId) throws Exception{
        for(Server server : servers) 
            server.addVoter(uniqueVoterId);
    }

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

    public boolean transactionCompleted(String voteUUID) throws RemoteException {

        if(voteUUIDs.contains(voteUUID)) {
            voteUUIDs.remove(voteUUID);
            // System.out.println(voteUUID + ": a miner has completed this transaction.");
            return true;
        } else {
            // System.out.println(voteUUID + ": a transaction does not exist. Maybe it has already been completed?");
            return false;
        }
    }



}