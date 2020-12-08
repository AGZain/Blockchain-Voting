//This is the implementation of the actual blocks on the blockchain
import java.io.*; 
import java.util.*;
public class Block implements Serializable{
    String timestamp;
    int id;
    String data;
    String prevHash;
    int nounce;
    List<Integer> proof;

    //initializing a block, these are the required parameters
    public Block(String timestamp, int id, String data, String prevHash, int nounce, List<Integer> proof) {
        this.timestamp = timestamp;
        this.id = id;
        this.data = data;
        this.prevHash = prevHash;
        this.nounce = nounce;
        this.proof = proof;
    }

    //returns previous hash
    public String getPrevHash() {
        return prevHash;
    }

    //get the actual vote that was cast
    public String getData() {
        return data;
    }
}