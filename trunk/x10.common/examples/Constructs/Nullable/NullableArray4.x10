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
		 		 
		 Sub[.] subarr00 = new Sub[[0:3]];
		 (nullable Sub)[.] subarr01 = new (nullable Sub)[[0:3]];
		 nullable Sub[.] subarr10 = null;
		 nullable (nullable Sub)[.] subarr11 = null;
		 
		 Sup[.] suparr00 = subarr00;
		 
	         boolean gotException=false;
		 try {
		     //cast exception should be thrown here
		     suparr00[0] = new Sup();		 		 
		 } catch(ClassCastException e) {
			gotException=true;
		 }
		 if (!gotException) return false;
		 
		 (nullable Sup)[.] suparr01= subarr00;
		 suparr01 = subarr01;
		 suparr01= suparr00;

		 nullable Sup[.] suparr10=subarr00;
		 
		 //suparr10 = subarr01; //illegal
		 suparr10 = subarr10;
		 suparr10 = suparr00;
		 //suparr10 = suparr01; //illegal

		 nullable (nullable Sup)[.] suparr11=subarr00;
		 suparr11 = subarr01;
		 
		 suparr11 = subarr10;
		 
		 suparr11 = subarr11;
		 suparr11 = suparr00;
		 suparr11 = suparr01;
		 suparr11 = suparr10;
		 

		 //what about java classes?

		 String[] sa00 = new String[3];
		 (nullable String)[] sa01 = new (nullable String)[3];
		 nullable String[] sa10 = null;
		 nullable (((nullable String)[])) sa11 = null;
		 
		 sa01 = sa00;
		 sa10 = sa00;
		 // sa10 = sa01; //illegal
		 sa11 = sa00;
		 sa11 = sa10;
		 sa11 = sa01;
		 
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
