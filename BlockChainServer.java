import java.rmi.*;
import java.rmi.registry.*;

public class BlockChainServer {
    public static void main(String args[]) {
        try {
            ServerImplementation serverImplementation = new ServerImplementation();
            serverImplementation.StartServer();
            System.out.println("DOING OTHER STUFF");
        } catch(Exception exception) {}


        // try {
        //     String name = "BlockChainServer";
        //     Registry registry = LocateRegistry.getRegistry(args[0]);
            
        // } catch(Exception exception) {
        //     exception.printStackTrace();
        // }
    }
}