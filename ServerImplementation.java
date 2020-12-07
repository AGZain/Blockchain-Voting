//This class will contain the server classes that other servers will use to communicate with this server with. 

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.util.*;

public class ServerImplementation extends Thread implements Server {
    
    //ListOfNeighoughrServers
    List<nodeAddress> nodes = new ArrayList<nodeAddress>();
    List<Server> servers = new ArrayList<Server>();
    nodeAddress thisNode;
    BlockChain blockchain;
    HashMap<String, Client> clients = new HashMap<String, Client>();
    HashSet<String> voters = new HashSet<String>();
    List<String> candidates = new ArrayList<String>();

    public ServerImplementation() throws RemoteException{
        super();
    }

    public ServerImplementation(boolean genesisNeeded) throws RemoteException {
        super();
        blockchain = new BlockChain();
        if(genesisNeeded) {
            System.out.println("Create Genesis block");
            Block genesis = new Block(new java.util.Date().toString(),
                                    0,
                                    "GENESIS-BLOCK",
                                    "000000",
                                    0,
                                    new ArrayList<Integer>());
            try {
                blockchain.blocks.add(genesis); 
                blockchain.latestHash = blockchain.generateHash(genesis);
            } catch(Exception exception) {
                exception.printStackTrace();
            }   
        }

        blockchain.start();
    }

    public void StartServer(String name) {
        System.setSecurityManager(new SecurityManager());
        try {
            // Server obj = new ServerImplementation();
            Server stub = (Server) UnicastRemoteObject.exportObject(this, 0);
            Registry reg = LocateRegistry.getRegistry();
            System.out.println("1");
            reg.bind(name, stub);
            System.out.println("2");

            InetAddress addr = InetAddress.getLocalHost();
            thisNode = new nodeAddress(addr.getHostAddress(), name);
            addNodeAddress(thisNode);
            this.start();
            System.out.println("Server has started on " + addr.getHostAddress() +  " " + name);
        } catch(Exception exception) {
            exception.printStackTrace();

        }
    }

    public void registerNeighbor(String host, String name) throws RemoteException {
        try{
            nodeAddress newNode = new nodeAddress(host, name);
            addNodeAddress(newNode);
            System.out.println("Neighbor node registered");
            Registry registry = LocateRegistry.getRegistry(newNode.getAddress());
            Server server = (Server) registry.lookup(newNode.getName());
            servers.add(server);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        // return "ADDED";
    }

    //this will be used to get all neighbours
    public List<nodeAddress> getAllNodesOnNetwork() throws RemoteException{
        return nodes;
    }

    public boolean addNodeAddress(nodeAddress node) {
        nodes.add(node);
        return true;
    }

    //this will be used to register with all neighborus nrigbours this is it, 
    public void registerWithAllNeighbors() throws Exception{
        System.out.println("Connecting to all nodes on network..");
        for(nodeAddress node : nodes) {
            if(node.getName().equals(thisNode.getName()))
                continue;
            Registry registry = LocateRegistry.getRegistry(node.getAddress());
            Server server = (Server) registry.lookup(node.getName());
            servers.add(server);
            server.registerNeighbor(thisNode.getAddress(), thisNode.getName());
        }
        System.out.println("Connections established");
    }

    public void receiveVote(String vote, String applicationUUID, String voteUUID) throws RemoteException {
        try{
            System.out.println("Vote received");
            voters.remove(voteUUID);
            blockchain.addNewTransaction(new Vote(vote, applicationUUID, voteUUID));
        }catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public void registerApplication(String uuid, String address, String name) throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(address);
            Client client = (Client) registry.lookup(name); 

            clients.put(uuid, client);
            System.out.println("Client " + uuid + " is connected");
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public void applicationResponder() {
        //have a blockingQueue in BlockChain classes that has a list of mined blocks. 
        //when blocks are done mining, send message to application level to let app know that youre done. First person to win gets to resend 

        while(true) {
            try{
                Vote completed = blockchain.completedMine();
                System.out.println("Vote id: " + completed.getVoteId() + " has been completed");

                Client client = clients.get(completed.getApplicationId());
                boolean winner = client.transactionCompleted(completed.getVoteId());

                if(winner) {
                    Block block = blockchain.getPendingBlock(completed.getVoteId());
                    String pow = blockchain.getPendingPOW(completed.getVoteId());
                    System.out.println("Sending winning block to neighbours");

                    for(Server server : servers) {
                        server.receiveBlockAndPOW(pow, block);
                    }
                    receiveBlockAndPOW(pow, block);
                }
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void receiveBlockAndPOW(String POW, Block block) throws RemoteException {
        //verify POW first.
        blockchain.pow.nounce -= 100;
        blockchain.createBlock(block);
    }

    public List<Block> getLatestBlockChain() throws RemoteException {
        return blockchain.blocks;
    }
    
    public String getLatestHash() throws RemoteException {
        return blockchain.latestHash;
    }

    public void setBlockChainAndHash(List<Block> blocks, String hash) {
        blockchain.blocks = blocks;
        blockchain.latestHash = hash;
    }

    public void addVoter(String uniqueVoterId) throws RemoteException {
        voters.add(uniqueVoterId);
    } 

    public void addCandidate(String candidate) throws RemoteException {
        candidates.add(candidate);
    }

    public HashSet<String> getVoters() throws RemoteException {
        return voters;
    }

    public List<String> getCandidates() throws RemoteException {
        return candidates;
    }

    public boolean verifyVoter(String voterId) throws RemoteException {
        return voters.contains(voterId);
    }

    public void run() {
        applicationResponder();
    }
}