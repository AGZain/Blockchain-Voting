
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.*;    


public class TestClient implements Client {
    // Server server;
    List <Server> servers = new ArrayList<Server>();
    String uuid;
    String testClient = "testclient";
    HashSet<String> voteUUIDs = new HashSet<String>();

    public TestClient(Server server) throws RemoteException {
        super();
        // this.server = server;
    }

    public TestClient() throws RemoteException {
        super();
    }

    public static void main(String args[]) {
        try {
            TestClient testClient = new TestClient();
            testClient.StartServer("testclient");
            testClient.connectToServers("127.0.0.1", "test");
            testClient.submitVote("Bob the builder");
            // server.receiveVote("Bob The Builder");


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void StartServer(String name) {
        System.setSecurityManager(new SecurityManager());
        try {
            // Client obj = new TestClient();
            Client stub = (Client) UnicastRemoteObject.exportObject(this, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.bind(name, stub);

            System.out.println("client has started on " + name);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public void connectToServers(String address, String name) {
        try {
            this.uuid = UUID.randomUUID().toString();
            Registry registry = LocateRegistry.getRegistry(address);
            Server server = (Server) registry.lookup(name);
            List<nodeAddress> nodes = server.getAllNodesOnNetwork();
            for(nodeAddress node : nodes) {
                Registry reg = LocateRegistry.getRegistry(node.getAddress());
                Server newServer = (Server) reg.lookup(node.getName());

                servers.add(newServer);
                newServer.registerApplication(uuid, "127.0.0.1", testClient);

            }
            // this.server = server;
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void submitVote(String vote) {
        try{
            String voteUUID = UUID.randomUUID().toString();
            voteUUIDs.add(voteUUID);
            System.out.println("sending vote.. with UUID " + voteUUID);
            for(Server server : servers) {
                server.receiveVote(vote, this.uuid, voteUUID);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean transactionCompleted(String voteUUID) throws RemoteException {

        if(voteUUIDs.contains(voteUUID)) {
            voteUUIDs.remove(voteUUID);
            System.out.println(voteUUID + ": a miner has completed this transaction.");
            return true;
        } else {
            System.out.println(voteUUID + ": a transaction does not exist. Maybe it has already been completed?");
            return false;
        }
    }

}

