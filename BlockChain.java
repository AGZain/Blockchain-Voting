import java.lang.Math;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.security.MessageDigest;

public class BlockChain extends Thread {
    List<Block> blocks;
    BlockingQueue<Vote> newTransactions = new LinkedBlockingQueue<Vote>();   
    BlockingQueue<Vote> completedTransaction = new LinkedBlockingQueue<Vote>();   
    HashMap<String, Block> pendingBlocks = new HashMap<String, Block>();
    HashMap<String, String> pendingPOWs = new HashMap<String, String>(); 


    List<Block> minedBlocks;                    //might not be needed, maybe we can remove
    String latestHash;

    public BlockChain() {
        blocks = new ArrayList<Block>();
        minedBlocks = new ArrayList<Block>();
    }

    public BlockChain(List<Block> blocks, List<Block> minedBlocks) {
        this.blocks = blocks;
        this.minedBlocks = minedBlocks;
    }

    public void addNewTransaction(Vote vote) {
        //users can add anew transaction which can be added to a blocking queue.
        System.out.println("adding new transaction to blockchain");
        try{
            newTransactions.add(vote); 
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public void mine() {
        //should be a seperate thread that is just checking the blocking queue for new transcations
        //do the POW, then send the confirmation back to the Application level. 
        //if no approval by application level, then drop. 

        while(true) {
            try {
                Vote newTransaction = newTransactions.take();
                //do POW then create block.
                System.out.println("got a new block to mine!");
                Block block = new Block(new java.util.Date().toString(),           //create the block based on the new transaction...
                                        ++blocks.get(blocks.size()-1).id,
                                        newTransaction.getVote(),
                                        latestHash,
                                        1,                      //last two are temp. nounce and proof examples, will be replaced later
                                        "10");

                //generate hash.

                pendingBlocks.put(newTransaction.getVoteId(), block);
                pendingPOWs.put(newTransaction.getVoteId(), "TEST POW");
                completedTransaction.add(newTransaction); 
            } catch(Exception exception) {
                exception.printStackTrace();
            }

        }
    }

    public String generateHash(Block block) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);    
        oos.writeObject(block);
        oos.flush();

        byte[] hashBytes = digest.digest(bos.toByteArray());
        String hash  = new String(hashBytes);
        return hash;
    }

    public void createBlock(Block block) {
        //Do this part only if the block creation has been verified
        //oW pow = new PoW();    
        //block.pow = pow;    //Assigning pow to a block, change "block" to the most recent block made
        System.out.println("Adding new block to blockchain");
        if(block.getPrevHash().equals(latestHash)) {
            try {
                latestHash = generateHash(block);
                blocks.add(block);
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public Vote completedMine() {
        try{
            return completedTransaction.take();
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        return new Vote("ERROR", "ERROR", "ERROR");
    }

    public Block getPendingBlock(String voteUUID) {
        Block pendingBlock = pendingBlocks.get(voteUUID);
        pendingBlocks.remove(voteUUID);
        return pendingBlock;
    }

    public String getPendingPOW(String voteUUID) {
        String pendingPOW = pendingPOWs.get(voteUUID);
        pendingPOWs.remove(voteUUID);
        return pendingPOW;
    }

    public void run() {
        System.out.println("Starting mining");
        mine();
    }
}