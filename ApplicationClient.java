//This is the application layer, what voters will use to cast votes

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.*;    


public class ApplicationClient implements Client {
    Server currServer;
    List <Server> servers = new ArrayList<Server>();
    String uuid;
    String clientName;
    HashSet<String> voteUUIDs = new HashSet<String>();
    List<String> voteOptions = new ArrayList<String>();

    public ApplicationClient() throws RemoteException {
        super();
    }

    public static void main(String args[]) {
        try {
            ApplicationClient applicationClient = new ApplicationClient();
            applicationClient.uuid = UUID.randomUUID().toString();
            applicationClient.clientName = applicationClient.uuid; 
            applicationClient.StartServer(applicationClient.clientName);
            applicationClient.connectToServers(args[0], args[1]);
            applicationClient.mainMenu();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    //use to start the application server so that nodes on the blockchain network can message this application
    //@param String name - name of the client (which will be a unique ID UUID)
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

    //Connect to any node on the blockchain network
    //@param String address, String name - the address and name of any node on the network
    public void connectToServers(String address, String name) {
        try {
            Registry registry = LocateRegistry.getRegistry(address);
            Server server = (Server) registry.lookup(name);
            List<nodeAddress> nodes = server.getAllNodesOnNetwork();
            for(nodeAddress node : nodes) {
                Registry reg = LocateRegistry.getRegistry(node.getAddress());
                Server newServer = (Server) reg.lookup(node.getName());

                servers.add(newServer);
                newServer.registerApplication(uuid, "127.0.0.1", clientName);
            }
            this.currServer = server;
            voteOptions = currServer.getCandidates();
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    //Used to submit a vote, need the vote itself and the voters unique ID
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
    
    //When a response is sent from a node on the blockchain network, this method replies to the winner
    public boolean transactionCompleted(String voteUUID) throws RemoteException {

        if(voteUUIDs.contains(voteUUID)) {
            voteUUIDs.remove(voteUUID);
            return true;
        } else {
            return false;
        }
    }

    //Main menu so user can pick options
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

    //Allows users to cast a vote
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

    //View the statistics of the votes
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

