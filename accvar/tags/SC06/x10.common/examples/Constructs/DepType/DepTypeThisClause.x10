 /** Checks that an arg i from the param list of one method does not stray into 
 * body of another.
 *@author vj, 5/17/2006
 *
 */

import harness.x10Test;

public class DepTypeThisClause(int i, int j) extends x10Test { 
    public DepTypeThisClause(int ii, int jj) { this.i=ii; this.j=jj;}
    
    //  i is a param for this method and also a property
   this(:i==3) DepTypeThisClause  make(int(:self==3) i ) { 
       return new DepTypeThisClause(i,i);
       }
    //  a method whose return type is a deptype
    this(:i==3) public boolean(:self==true)  run() { 
        System.out.println("i (=3?) = " + i); //property ref.
        return true;
    }
    
    public static void main(String[] args) {
        new DepTypeThisClause(3,9).execute();
    }
   
    }