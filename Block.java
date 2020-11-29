
public class Block {
    String timestamp;
    String id;
    String data;
    PoW pow;//Need Hash value, 8 length string maybe
    public Block(String timestamp, String id, String data, String prevHash) {
        this.timestamp = timestamp;
        this.id = id;
        this.data = data;
    }
}