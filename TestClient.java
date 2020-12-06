
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.*;    


public class TestClient implements Client {
    Server currServer;
    List <Server> servers = new ArrayList<Server>();
    String uuid;
    String testClient = "testclient";
    HashSet<String> voteUUIDs = new HashSet<String>();
    List<String> voteOptions = new ArrayList<String>();

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
            testClient.mainMenu();


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
            this.currServer = server;
            voteOptions = currServer.getCandidates();
        }catch (Exception exception) {
            exception.printStackTrace();
        }
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

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        String option;
        System.out.println("\n\nPlease select an option: ");
        System.out.println("A. Vote");
        System.out.println("B. See vote statistics");
        System.out.print("Make a selection: ");

        option = scanner.next();
        if(option.equals("A")){
            votingPage();
        } else if(option.equals("B")) {
            voteStats();
        } else{
            mainMenu();
        }
    }

    public void votingPage() {
        Scanner scanner = new Scanner(System.in);
        int vote;
        String id;
        System.out.print("\nPlease enter your voter id: ");
        id = scanner.next();
        try {
            if(!currServer.verifyVoter(id)) {
                votingPage();
                return;
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        
        System.out.println("Pick option to vote for: ");
        for(int optionNumber = 0; optionNumber < voteOptions.size(); optionNumber++) {
            System.out.println(optionNumber + ". " + voteOptions.get(optionNumber));
        }

        System.out.print("Select your vote: ");
        vote = scanner.nextInt();
        System.out.println("\n\n\nYou selected: " + voteOptions.get(vote) + ". \nThank you for your vote\n\n\n");
        submitVote(voteOptions.get(vote), id);
        mainMenu();
    }

    public void voteStats() {
        List<Block> blocks;
        HashMap<String, Integer> votes = new HashMap<String, Integer>();
        try {
            blocks = currServer.getLatestBlockChain();
            String winner = "";
            int winnerVotes = 0;
            for(Block block : blocks) {
                String vote = block.getData();
                if(vote.equals("GENESIS-BLOCK"))
                    continue;
                
                if(!votes.containsKey(vote)) {
                    votes.put(vote, 1);
                } else {
                    votes.put(vote, votes.get(vote) + 1);
                }
                if(votes.get(vote) > winnerVotes) {
                    winnerVotes = votes.get(vote);
                    winner = vote;
                } else if(votes.get(vote) == winnerVotes) {
                    winner += " and " + vote;
                }
            }
            System.out.println(votes);
            System.out.println("\nWinner so far is: " + winner + "\n\n");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        mainMenu();
    }

}

