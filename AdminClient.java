import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.*;    


public class AdminClient {

    String clientName = "adminClient";
    List<Server> servers = new ArrayList<Server>();
    String uuid;

    public static void main(String args[]) {
        AdminClient adminClient = new AdminClient();
        adminClient.connectToServers("127.0.0.1", "test");
        adminClient.mainMenu();
    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        String option;
        System.out.println("\nWelcome to the management panel of the votign system. What would you like to do?");
        System.out.println("A. Add a candidate");
        System.out.println("B. Add a voter");
        System.out.print("Make a selection: ");
        option = scanner.next();
        if(option.equals("A")){
            addCandidateMenu();
        } else if(option.equals("B")) {
            addVoterMenu();
        } else{
            mainMenu();
        }
    }

    public void addCandidateMenu() {
        Scanner scanner = new Scanner(System.in);
        String name;
        System.out.print("\n\nEnter name of candidate: ");
        name = scanner.next();
        //SEND NAME TO BLOCKCHAIN
        try {
            for(Server server : servers) {
                server.addCandidate(name);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        mainMenu();
    }

    public void addVoterMenu() {
        Scanner scanner = new Scanner(System.in);
        String name, sin;
        String uniqueVoterId;
        System.out.print("\n\nEnter name of voter: ");
        name = scanner.next();
        System.out.print("\nEnter social insurance number of voter: ");
        sin = scanner.next();
        uniqueVoterId = UUID.randomUUID().toString();
        System.out.println("Unique Voter ID: " + uniqueVoterId);
        //SEND UNIQUE VOTER ID TO BLOCKCHAIN
        try {
            for(Server server : servers) {
                server.addVoter(uniqueVoterId);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }

        mainMenu();
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
                // newServer.registerApplication(uuid, "127.0.0.1", clientName);
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}