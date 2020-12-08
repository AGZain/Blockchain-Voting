//used to hold the connection details of a node on the network.
import java.io.Serializable;

public class nodeAddress implements Serializable {
    String address;
    String name;
    //initialize with a address and name
    public nodeAddress(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }
}