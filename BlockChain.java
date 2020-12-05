import java.lang.Math;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.security.MessageDigest;

public class BlockChain extends Thread {
    List<Block> blocks;
    BlockingQueue<String> newTransactions = new LinkedBlockingDeque<String>();   
    List<Block> minedBlocks;
    String latestHash;

    public BlockChain() {
        blocks = new ArrayList<Block>();
        minedBlocks = new ArrayList<Block>();
    }

    public BlockChain(List<Block> blocks, List<Block> minedBlocks) {
        this.blocks = blocks;
        this.minedBlocks = minedBlocks;
    }

    public void addNewTransaction(String transaction) {
        //users can add anew transaction which can be added to a blocking queue.
        try{
            newTransactions.add(transaction); 
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public void mine() {
        //should be a seperate thread that is just checking the blocking queue for new transcations
        //do the POW, then send the confirmation back to the Application level. 
        //if no approval by application level, then drop. 

        while(true) {
            String newTransaction = newTransactions.take();
            //do POW then create block.
            Block block = new Block(new java.util.Date().toString(),           //create the block based on the new transaction...
                                    ++blocks.get(blocks.size()-1).id,
                                    newTransaction,
                                    latestHash,
                                    1,                      //last two are temp. nounce and proof examples, will be replaced later
                                    "10");

            //generate hash. 

        }
    }

    public String generateHash(Block block) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);    
        oos.writeObject(block);
        oos.flush();

        byte[] hashBytes = digest.digest(bos.toByteArray());
        String hash  = new String(hashBytes);
        return hash;
    }

    public void createBlock() {
        //Do this part only if the block creation has been verified
        //oW pow = new PoW();    
        //block.pow = pow;    //Assigning pow to a block, change "block" to the most recent block made
        
    }

    public void run() {
        mine();
    }
}