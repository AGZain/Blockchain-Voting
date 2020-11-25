
public class Block {
    String timestamp;
    String id;
    String data;
    PoW pow;
    public Block(String timestamp, String id, String data, String prevHash) {
        this.timestamp = timestamp;
        this.id = id;
        this.data = data;
    }
}