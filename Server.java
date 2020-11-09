import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void registerNeighbor(String host) throws RemoteException;
}
