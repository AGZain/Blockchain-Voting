import java.util.ArrayList;

public class Consensus {
    
    public Consensus() {

    }

    /* --------------------------------------------------------------------------------------
     *                              Code for Consensus
     *  -------------------------------------------------------------------------------------
     * Also lemme know when you start working on the implementation so I can help you with the
     * difficulty part */

    /**
     * Check list if as are all valid by getting x from x = Math.sqrt(a % 10)
     * And then check if x is co prime with n
     * @param n given mod value
     * @param a_list list of found residues
     * @return Boolean if the answer is correct or incorrect
     */ 
    static Boolean checkAns(int n, ArrayList<Integer>a_list) {
        for(int i = 0; i < a_list.size(); i++) {
            double gcd = findGCD(a_list.get(i), n);
            if(gcd != 1){ 
                System.out.println(a_list.get(i) + ", " + n + ", " + gcd);
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
