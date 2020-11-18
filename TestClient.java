
import java.rmi.*;
import java.rmi.registry.*;

public class TestClient {

    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.1.1");
            Server server = (Server) registry.lookup("TestServer");
            System.out.println("testinggggggg");
            System.out.println(server.registerNeighbor("EST"));
            
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }
}