import java.util.ArrayList;  
import java.lang.Math;

//Equation:
//x^2 = a(mod n) || Math.pow(x, 2) = a % n

public class PoW{
    //State Variables
    private static ArrayList<Integer> res = new ArrayList<Integer>();
    private static ArrayList<Integer> non_res = new ArrayList<Integer>(); 

    /* THE CONSTRUCTOR */
    public PoW() {
        buildProblems();    //Calling function that builds the HashMap
    }

    /**
     * Function that takes a given n and calculates all the quantum residual and non-residual integers
     * These are then stored in the res and non_res lists respectively
     */
    void buildProblems() {
        int n = (int)(Math.random() * 550000) + 500000;     //n = 500000 took my laptop around 14 seconds to solve    
        ArrayList<Integer> x_list = new ArrayList<Integer>();   //list of all numbers co-prime of n

        /* Finding co primes for given n */
        for(int i = 1; i < n; i++) {
            if(rel_Prime(n, i) ) { 
                x_list.add(i);  //Adding the co-prime to the list of x variables
            }
        }
        //Calling function to find residues and nonresidues from x_list
        for(int i = 0; i < x_list.size(); i++) {
            find_res(n, x_list.get(i) );
        }
    }
    /**
     * Getter function that gets the list of all residual integers
     * @return the list of residual integers
     */
    public ArrayList<Integer> get_res() {
        return res;
    }
    /**
     * Getter function that gets the list of all non-residual integers
     * @return the list of non-residual integers
     */
    public ArrayList<Integer> get_non_res() {
        return non_res;
    }

    /**
     * Function to check if x and n are co-prime 
     * @param n The modulus integer
     * @param x Integer for perfect square
     * @return true if x and n are co-prime and false if otherwise
     */
    static boolean rel_Prime(int n, int x) {
        /* Algorithm to check if a and n have the same factors */
        //Get the factors of x
        for(int i = 2; i < x; i++) {
            switch(x % i) {
                case 0:
                //i is a factor of X 
                switch(n % i) {
                    //i is a factor of x and n, therefore x and n are not co-prime and cannot be used
                    case 0:
                        return false;
                }
            }
        }
        //If n and x do not share any factors then they are co prime and are applicable for the equation
        return true;
    }

    /**
     * Function that takes two co-prime integers and finds all quadratic residues and non-residues
     * @param n The modulus integer
     * @param x The integer for perfect square
     */
    static void find_res(int n, int x) {
        int a  = (int)Math.pow(x, 2)  % n;  //Potential quadratic residue

        //If the list is empty, add the first found 'a' as a residual
        switch(res.size() ) {
            case 0:
                res.add(a);
                System.out.println("Res -> " + a);
            break;
            //If the list is not empty, perform the loop
            default:
                //If this residue for x was already found, make x a non-residue
                if(res.contains(a) ) {
                    non_res.add(x);
                    System.out.println("Non Res -> " + x);
                }
                else {
                    //If this residue for x was not found, make var 'a' a residue
                    res.add(a);
                    System.out.println("Res -> " + a);
                }
        }   
    }
    
}