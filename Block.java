public class Block {
    Timestamp timestamp;
    String id;
    String data;
    
    public Block(Timestamp timestamp, String id, String data) {
        this.timestamp = timestamp;
        this.id = id;
        this.data = data;
    }
}