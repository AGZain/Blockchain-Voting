import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    String registerNeighbor(String host) throws RemoteException;
}
