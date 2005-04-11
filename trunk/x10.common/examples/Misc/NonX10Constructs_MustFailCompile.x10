
/**
 * @author kemal 4/2005
 *
 * Non X10 java constructs should be flagged by compiler
 *
 */
public class NonX10Constructs_MustFailCompile {
   volatile boolean flag=false;
   int x=0;
   public boolean run() {
       boolean b=false;
       synchronized(this) { x++;}
       synchronized(this) { b=(x==1);}
       return b;
   }
	public static void main(String args[]) {
		boolean b= (new NonX10Constructs_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
