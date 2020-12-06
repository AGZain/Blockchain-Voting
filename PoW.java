import java.util.ArrayList;  
import java.lang.Math;

//Equation:
//x^2 = a(mod nounce) || Math.pow(x, 2) = a % nounce

public class PoW{
    //State Variables
    private static ArrayList<Integer> res = new ArrayList<Integer>();
    static int nounce = 300000;    //nounce = 300000 took my laptop around 40 seconds to solve, you can tweak these values 
    /* THE CONSTRUCTOR */
    public PoW() {
    }

    /**
     * Function that takes a given nounce and calculates all the quantum residual and non-residual integers
     * These are then stored in the res and non_res lists respectively
     */
    void buildProblems() {
        ArrayList<Integer> x_list = new ArrayList<Integer>();   //list of all numbers co-prime of nounce
        /* Finding co primes for given nounce */
        for(int i = 1; i < nounce; i++) {
            if(rel_Prime(nounce, i) ) { 
                x_list.add(i);  //Adding the co-prime to the list of x variables
            }
        }
        /*Calling function to find residues and nonresidues from x_list*/
        for(int i = 0; i < x_list.size(); i++) {
            find_res(nounce, x_list.get(i) );
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
     * Function to check if x and nounce are co-prime 
     * @param nounce The modulus integer
     * @param x Integer for perfect square
     * @return true if x and nounce are co-prime and false if otherwise
     */
    static boolean rel_Prime(int nounce, int x) {
        /* Algorithm to check if a and nounce have the same factors */
        //Get the factors of x
        for(int i = 2; i < x; i++) {
            switch(x % i) {
                case 0:
                //i is a factor of X 
                switch(nounce % i) {
                    //i is a factor of x and nounce, therefore x and nounce are not co-prime and cannot be used
                    case 0:
                        return false;
                }
            }
        }
        //If nounce and x do not share any factors then they are co prime and are applicable for the equation
        return true;
    }

    /**
     * Function that takes two co-prime integers and finds all quadratic residues and non-residues
     * @param nounce The modulus integer
     * @param x The integer for perfect square
     */
    static void find_res(int nounce, int x) {
        int a  = (int)Math.pow(x, 2)  % nounce;  //Potential quadratic residue

        //If the list is empty, add the first found 'a' as a residual
        switch(res.size() ) {
            case 0:
                res.add(a);
                System.out.println("Res -> " + a);
            break;
            //If the list is not empty, perform the loop
            default:
            //If this residue for x was already found, make x a non-residue
            if(!res.contains(a) ) {
                //If this residue for x was not found, make var 'a' a residue
                res.add(a);
                System.out.println("Res -> " + a);
            }            
        }   
    }

    /**
     * Check list if as are all valid by getting x from x = Math.sqrt(a % 10)
     * And then check if x is co prime with nounce
     * @param nounce given mod value
     * @param a_list list of found residues
     * @return Boolean if the answer is correct or incorrect
     */ 
    static Boolean checkAns(int nounce, ArrayList<Integer>a_list) {
        for(int i = 0; i < a_list.size(); i++) {
            double gcd = findGCD(a_list.get(i), nounce);
            if(gcd != 1){ 
                return false;
            }
        }
        return true;
    }
    private static int findGCD(int num1, int num2) {
        if(num2 == 0) {
            return num1;
        }
        return findGCD(num2, num1 % num2);
    }
}