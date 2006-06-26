/**
 *@author vj, 5/17/2006
 *
 */
 
public class DepType {
    final int i;
    final int j;
    class Test extends DepType {
        int k;
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
    class Test3(:j==k) extends DepType(:i==j) {
        final int k;
        Test3(int v) {
        super(v,v);
        k = v;
        }
    }
    // A construtor may
    public DepType(:i==j )  (int ii, int jj   ) {
        this.i=ii;
        this.j=jj;
    }
   DepType(:i==3) make(int(3) i ) { 
       return new DepType(i,i);
       }
    public  boolean run() {
	DepType(:i==3) d = new DepType(3,6);
	return true;
    }
	
    public boolean this(3) run3() {
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public boolean  run4( int j) {
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public static void main(String[] args) {
        boolean b= false;
        try {
                finish b =(new DepType(3,8)).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b=false;
        }
        System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b?0:1);
    }
   

		
}
