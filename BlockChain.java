//this is the implementation of the blockchain itself.
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


    List<Block> minedBlocks; 
    String latestHash;

    //if new blockchain network is beign created
    public BlockChain() {
        blocks = new ArrayList<Block>();
        minedBlocks = new ArrayList<Block>();
    }

    //if a blockchain network exists, import the blocks/blockchain
    public BlockChain(List<Block> blocks, List<Block> minedBlocks) {
        this.blocks = blocks;
        this.minedBlocks = minedBlocks;
    }

    //add a new transaction so that it can be mined
    public void addNewTransaction(Vote vote) {
        //users can add anew transaction which can be added to a blocking queue.
        System.out.println("adding new transaction to queue");
        try{
            newTransactions.add(vote); 
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    //should be a seperate thread that is just checking the blocking queue for new transcations
    //do the POW, then send the confirmation back to the Application level. 
    //if no approval by application level, then drop. 
    public void mine() {
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
    //given a block, we generate the hash and return it
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

    //check hash and proof of work, then add to blockchain
    public void createBlock(Block block) {
        if(block.getPrevHash().equals(latestHash) || pow.checkAns(block.nounce, block.proof)) {
            try {
                System.out.println("Adding new block to blockchain");
                latestHash = generateHash(block);
                blocks.add(block);
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    //we use a blocking queue to return a node once it has been mined, so that it can be added to the blockchain
    public Vote completedMine() {
        try{
            return completedTransaction.take();
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        return new Vote("ERROR", "ERROR", "ERROR");
    }

    //return a pending block
    public Block getPendingBlock(String voteUUID) {
        Block pendingBlock = pendingBlocks.get(voteUUID);
        pendingBlocks.remove(voteUUID);
        return pendingBlock;
    }

    //return pening proof of works that need to be verified
    public String getPendingPOW(String voteUUID) {
        String pendingPOW = pendingPOWs.get(voteUUID);
        pendingPOWs.remove(voteUUID);
        return pendingPOW;
    }
    //mining will run on a seperate thread so that it is always runnings
    public void run() {
        System.out.println("Starting mining");
        mine();
    }
}