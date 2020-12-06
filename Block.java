import java.io.*; 

public class Block implements Serializable{
    String timestamp;
    int id;
    String data;
    String prevHash;
    PoW pow;//Need Hash value, 8 length string maybe
    int nounce;
    String proof;

    public Block(String timestamp, int id, String data, String prevHash, int nounce, String proof) {
        this.timestamp = timestamp;
        this.id = id;
        this.data = data;
        this.prevHash = prevHash;
        this.nounce = nounce;
        this.proof = proof;
    }

    public String getPrevHash() {
        return prevHash;
    }
}