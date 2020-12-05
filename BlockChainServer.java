import java.rmi.*;
import java.rmi.registry.*;
import java.net.*;
import java.util.*;

public class BlockChainServer {
    
    public static void main(String args[]) {
        
        BlockChainServer blockchainServer = new BlockChainServer();
        boolean newNetwork = (args.length == 1) ? true : false;

        try {
            ServerImplementation serverImplementation = new ServerImplementation(newNetwork);
            String name = args[0];
            serverImplementation.StartServer(name);
            //if the args for a remote hsot and name are passed, then connect to that remote host and get all of its neighbours
            if (args.length >= 3)
                blockchainServer.connectToNetwork("host", args[2], serverImplementation);    


        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public void connectToNetwork(String host, String name, ServerImplementation serverImplementation) throws Exception {
        List<nodeAddress> networkNodes = new ArrayList<nodeAddress>();
        Registry registry = LocateRegistry.getRegistry("127.0.0.1");
        Server server = (Server) registry.lookup(name);

        networkNodes = server.getAllNodesOnNetwork();
        
        for(nodeAddress node: networkNodes) {
            serverImplementation.addNodeAddress(node);
        }
        
        System.out.println("starz");
        serverImplementation.registerWithAllNeighbors();
    }

}