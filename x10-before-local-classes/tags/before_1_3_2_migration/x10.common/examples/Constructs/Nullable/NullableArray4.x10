/** 
 * Tests: 
 *   - (mostly static typing behavior)
 *   - the inter-play of array, nullable, java types,
sub-typing;
 *   - nullable array reference; nullable array elements;
 *   - proper runtime checking for Java-style array
subtyping;
 *
 * @author Bin Xin
 * @date 2005/08
 */
public class NullableArray4 {
    class Sup {}
    class Sub extends Sup {}
    
    public boolean run() {
		 Sup[.] suparr;
		 (nullable Sup)[.] suparr2;
		 nullable (Sup[.]) suparr3;
		 nullable ((nullable Sup)[.]) suparr4;
		 		 
		 Sub[.] subarr = new Sub[[0:3]->here];
		 (nullable Sub)[.] subarr2 = new (nullable Sub)[[0:3]->here];
		 nullable (Sub[.]) subarr3 = null;
		 nullable ((nullable Sub)[.]) subarr4 = null;
		 
		 suparr = subarr;
		 
	         boolean gotException=false;
		 try {
		     //cast exception should be thrown here
		     suparr[0] = new Sup();		 		 
		 } catch(ClassCastException e) {
			gotException=true;
		 }
		 if (!gotException) return false;
		 
		 suparr2 = subarr;
		 suparr3 = subarr;
		 suparr4 = subarr;
		 
		 suparr2 = subarr2;
		 //suparr3 = subarr2; //illegal
		 suparr4 = subarr2;
		 
		 suparr3 = subarr3;
		 suparr4 = subarr3;
		 
		 suparr4 = subarr4;
		 

		 //how about java class
		 String[] sa = new String[3];
		 (nullable String)[] sa2 = new (nullable String)[3];
		 nullable (String[]) sa3 = null;
		 nullable ((nullable String)[]) sa4 = null;
		 
		 sa2 = sa;
		 sa3 = sa;
		 sa4 = sa;
		 
		 return true;
    }

    public static void main(String[] args) {
		 final boxedBoolean b=new boxedBoolean();
		 try {
		     finish async b.val=(new NullableArray4()).run();
		 } catch (ClassCastException e) {
		     e.printStackTrace();
		     b.val = true; //expected
		 } catch (Throwable e) {
		     e.printStackTrace();
		     b.val = false;
		 }
		 System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
		 x10.lang.Runtime.setExitCode(b.val?0:1);
    }

    static class boxedBoolean {
        boolean val=false;
    }

}
