//This class will contain the server classes that other servers will use to communicate with this server with. 

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.util.*;



public class ServerImplementation implements Server {
    
    //ListOfNeighoughrServers
    List<nodeAddress> nodes = new ArrayList<nodeAddress>();
    List<Server> servers = new ArrayList<Server>();
    nodeAddress thisNode;
    BlackChain blackchain;


    public ServerImplementation() throws RemoteException {
        super();
        BlockChain = new BlockChain();
    }

    public void StartServer(String name) {
        System.setSecurityManager(new SecurityManager());
        try {
            Server obj = new ServerImplementation();
            Server stub = (Server) UnicastRemoteObject.exportObject(obj, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.bind(name, stub);
            InetAddress addr = InetAddress.getLocalHost();
            thisNode = new nodeAddress(addr.getHostAddress(), name);
            addNodeAddress(thisNode);
            System.out.println("Server has started on " + addr.getHostAddress() + addr.getHostName());
        } catch(Exception exception) {
            exception.printStackTrace();

        }
    }

    public String registerNeighbor(nodeAddress newNode) throws RemoteException {
        addNodeAddress(newNode);
        System.out.println("conntecting to a node");
        Registry registry = LocateRegistry.getRegistry(newNode.getAddress());
        Server server = (Server) registry.lookup(newNode.getName());
        servers.add(server);
        System.out.println("Time to register the neighbour");
        return "ADDED";
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
            if(node.getHost().equals(thisNode.getHost()))
                continue;
            System.out.println("conntecting to a node");
            Registry registry = LocateRegistry.getRegistry(node.getAddress());
            Server server = (Server) registry.lookup(node.getName());
            servers.add(server);
            server.registerNeighbor(thisNode)
        }
    }

    public void applicationResponder() {
        //have a blockingQueue in BlockChain classes that has a list of mined blocks. 
        //when blocks are done mining, send message to application level to let app know that youre done. First person to win gets to resend 

    }
}