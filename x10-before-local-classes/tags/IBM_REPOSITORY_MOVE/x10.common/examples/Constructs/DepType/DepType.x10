/** Tests many syntactic features of dep types.
 *@author vj, 5/17/2006
 *
 */

import harness.x10Test;

public class DepType(int i, int j) extends x10Test { 
    
     //  property declaration for an inner class.
    class Test(int k) extends DepType { 
        Test(int kk) {
            super(3,4);
            this.k=kk;
        }
    }
    class Test2 extends DepType {
        Test2() {
        super(3,5);
        }
    }
    
     //  thisClause on a class, and extension of a deptyped class
     class Test3  extends DepType(:i==j) { 
        final int k;
        Test3(:j==k)(int v) {
        super(v,v);
        k = v;
        }
    }
     
    // A construtor may specify constraints on properties that are true of the returned object.
    public DepType(:i==j )  (int ii, int jj   ) {
        this.i=ii;
        this.j=jj;
    }
    
    //  method specifies a thisClause.
   this(:i==3) DepType  make(int(:self==3) i ) { 
       return new DepType(i,i);
       }
   
    // a local variable with a dep clause.
    public  boolean run() {
	DepType(:i==3) d = (DepType(:i==3)) new DepType(3,6); 
	return true;
    }
	
    //  a method whose return type is a deptype
    this(:i==3) public boolean(:self==true)  run3() { 
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public boolean  run4( int j) {
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public static void main(String[] args) {
        new DepType(3,9).execute();
    }
   

		
}
