import java.io.Serializable;

public class nodeAddress implements Serializable {
    String address;
    String name;

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