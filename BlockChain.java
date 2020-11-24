import java.lang.Math;

public class BlockChain {
    private Block[] chainOfBlocks;

    //TODO: Generate PoW questions and Assign them to blocks
    //Use a random number to assign a random difficulty value from 1 - 5 to each block
    //After a block is assigned the value, assign the next block the next priority until
    //5 is reached. Then use the random number generator to assign a random difficulty again
    int difficulty = 0;


    public void createBlock() {


        //Do this part only if the block creation has been verified
        //Get random number and based on that, create a PoW object and assign it to the Block
        if(difficulty == 5) {
            difficulty = 0;    
        }
        if(difficulty == 0) { 
            difficulty = (int)(Math.random() * 5) + 1;
        }
        else{
            difficulty += 1;
        }
        //Create PoW and assign it the index
        PoW proof = new PoW(difficulty);
        //TODO: Release PoW

    }
}