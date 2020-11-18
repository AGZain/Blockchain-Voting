//This class will contain the server classes that other servers will use to communicate with this server with. 

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.util.*;



public class ServerImplementation implements Server {
    
    //ListOfNeighoughrServers
    List<nodeAddress> nodes = new ArrayList<nodeAddress>();

    public ServerImplementation() throws RemoteException {
        super();
    }

    public void StartServer() {
        System.setSecurityManager(new SecurityManager());
        try {
            String name = "TestServer";
            Server obj = new ServerImplementation();
            Server stub = (Server) UnicastRemoteObject.exportObject(obj, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.bind(name, stub);
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("Server has started on " + addr.getHostAddress() + addr.getHostName());
        } catch(Exception exception) {
            exception.printStackTrace();

        }
    }

    public String registerNeighbor(String host) throws RemoteException {
        System.out.println("Time to register the neighbour");
        return "TEST RETURN";
    }

    public List<nodeAddress> getAllNodesOnNetwork() {
        return nodes;
    }

    public boolean addNode(nodeAddress node) {
        nodes.add(node);
        return true;
    }




}