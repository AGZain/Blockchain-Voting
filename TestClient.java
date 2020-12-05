
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;


public class TestClient implements Client {
    Server server;
    String uuid;
    String testClient = "testclient";
    public TestClient(Server server) throws RemoteException {
        super();
        this.server = server;
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
            Client obj = new TestClient();
            Client stub = (Client) UnicastRemoteObject.exportObject(obj, 0);
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
            this.server = server;
            server.registerApplication(uuid, "127.0.0.1", testClient);
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void submitVote(String vote) {
        try{
            System.out.println("sending vote..");
            server.receiveVote(vote);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public void testMethod() throws RemoteException {
        System.out.println("Test");
    }

}

