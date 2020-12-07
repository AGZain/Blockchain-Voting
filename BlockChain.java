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
    PoW pow = new PoW();


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
        System.out.println("adding new transaction to queue");
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
                System.out.println("got a new block to mine!");
                Vote newTransaction = newTransactions.take();
                //do POW then create block.
                pow.buildProblems();
                List<Integer> proof = pow.get_res();
                int nounce = pow.nounce;
                Block block = new Block(new java.util.Date().toString(),           //create the block based on the new transaction...
                                        ++blocks.get(blocks.size()-1).id,
                                        newTransaction.getVote(),
                                        latestHash,
                                        nounce,                      //last two are temp. nounce and proof examples, will be replaced later
                                        proof);

                //generate hash.

                pendingBlocks.put(newTransaction.getVoteId(), block);
                pendingPOWs.put(newTransaction.getVoteId(), "POW");
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
        if(block.getPrevHash().equals(latestHash) && pow.checkAns(block.nounce, block.proof)) {
            try {
                System.out.println("Adding new block to blockchain");
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