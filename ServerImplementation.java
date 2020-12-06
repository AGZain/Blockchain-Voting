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
                                    "0");
            blockchain.blocks.add(genesis);    
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
            System.out.println("Server has started on " + addr.getHostAddress() + addr.getHostName());
        } catch(Exception exception) {
            exception.printStackTrace();

        }
    }

    public void registerNeighbor(String host, String name) throws RemoteException {
        try{
            nodeAddress newNode = new nodeAddress(host, name);
            addNodeAddress(newNode);
            System.out.println("conntecting to a node");
            Registry registry = LocateRegistry.getRegistry(newNode.getAddress());
            Server server = (Server) registry.lookup(newNode.getName());
            servers.add(server);
            System.out.println("Time to register the neighbour");
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
        for(nodeAddress node : nodes) {
            if(node.getName().equals(thisNode.getName()))
                continue;
            System.out.println("conntecting to a node");
            Registry registry = LocateRegistry.getRegistry(node.getAddress());
            Server server = (Server) registry.lookup(node.getName());
            servers.add(server);
            server.registerNeighbor(thisNode.getAddress(), thisNode.getName());
        }
    }

    public void receiveVote(String vote, String applicationUUID, String voteUUID) throws RemoteException {
        try{
            System.out.println("Vote recived");
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
                }
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }


    public void run() {
        applicationResponder();
    }
}