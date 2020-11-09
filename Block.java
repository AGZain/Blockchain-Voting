public class Block {
    Timestamp timestamp;
    String id;
    String data;

    public Block(Timestamp timestamp, String id, String data, String prevHash) {
        this.timestamp = timestamp;
        this.id = id;
        this.data = data;
    }
}