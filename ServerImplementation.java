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
        if(genesisNeeded) {     //if a gensis block is needed, we can generate it
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

    //start the blockchain server on this node
    public void StartServer(String name) {
        System.setSecurityManager(new SecurityManager());
        try {
            Server stub = (Server) UnicastRemoteObject.exportObject(this, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.bind(name, stub);

            InetAddress addr = InetAddress.getLocalHost();
            thisNode = new nodeAddress(addr.getHostAddress(), name);
            addNodeAddress(thisNode);
            this.start();
            System.out.println("Server has started on " + addr.getHostAddress() +  " " + name);
        } catch(Exception exception) {
            exception.printStackTrace();

        }
    }


    //Register with a neighboring node
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
    }

    //this will be used to get all neighbours, a remote node will call this mothod.
    public List<nodeAddress> getAllNodesOnNetwork() throws RemoteException{
        return nodes;
    }

    //add a new node to this nodes list of this node.
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

    //this is used to receive a vote, the client application will send the votes using this method.
    //The client will pass the vote itself, the ID of the client, as well as the unique voter id
    public void receiveVote(String vote, String applicationUUID, String voteUUID) throws RemoteException {
        try{
            System.out.println("Vote received");
            voters.remove(voteUUID);
            blockchain.addNewTransaction(new Vote(vote, applicationUUID, voteUUID));
        }catch(Exception exception) {
            exception.printStackTrace();
        }
    }
    //used to register a new application (client)
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

    //this waits for a block to be mined, when it is mined, a request is sent to the application layer
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
    //receive a block
    public void receiveBlockAndPOW(String POW, Block block) throws RemoteException {
        blockchain.pow.nounce -= 100;
        blockchain.createBlock(block);
    }
    //return the current blockchain
    public List<Block> getLatestBlockChain() throws RemoteException {
        return blockchain.blocks;
    }
    //return the latest hash
    public String getLatestHash() throws RemoteException {
        return blockchain.latestHash;
    }
    //set the blockchain and hash
    public void setBlockChainAndHash(List<Block> blocks, String hash) {
        blockchain.blocks = blocks;
        blockchain.latestHash = hash;
    }
    //add a new unique voter
    public void addVoter(String uniqueVoterId) throws RemoteException {
        voters.add(uniqueVoterId);
    } 
    //add a candidate
    public void addCandidate(String candidate) throws RemoteException {
        candidates.add(candidate);
    }
    //get the current available voters that have not voted yet
    public HashSet<String> getVoters() throws RemoteException {
        return voters;
    }
    //get a lsit of candidates
    public List<String> getCandidates() throws RemoteException {
        return candidates;
    }
    //verify that the voter exists
    public boolean verifyVoter(String voterId) throws RemoteException {
        return voters.contains(voterId);
    }
    //start a thread that is constantly waiting to sent a respond to the application layer 
    public void run() {
        applicationResponder();
    }
}