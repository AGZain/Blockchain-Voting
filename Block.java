import java.io.*; 
import java.util.*;
public class Block implements Serializable{
    String timestamp;
    int id;
    String data;
    String prevHash;
    int nounce;
    List<Integer> proof;

    public Block(String timestamp, int id, String data, String prevHash, int nounce, List<Integer> proof) {
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

    public String getData() {
        return data;
    }
}