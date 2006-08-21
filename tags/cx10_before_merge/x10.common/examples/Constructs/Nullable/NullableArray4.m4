//Generated automatically by 
//m4 NullableArra4.m4 > NullableArray4.x10
//Do not edit
define(`exception',
		 `
	         $1 = $2 ; 
	         {boolean gotException=false;
		 try {
		     $1[0] = $3;
		 } catch(ClassCastException e) {
		     gotException=true;
		 }
		 if (ifelse($4,`yes',`!',`')gotException) throw new Error();}')
/** 
 * Tests: 
 *   - the inter-play of array, nullable, java types,
sub-typing;
 *   - nullable array reference; nullable array elements;
 *   - proper runtime checking for Java-style array
subtyping;
 *
 * @author Bin Xin
 * @author kemal
 * @date 2005/08
 */
public class NullableArray4 {
    class Sup {}
    class Sub extends Sup {}
    
    public boolean run() {
		 		 
		 Sub[.] subarr00 = new Sub[[0:3]];
		 (nullable Sub)[.] subarr01 = new (nullable Sub)[[0:3]];
		 nullable Sub[.] subarr10 = null;
		 subarr10 = subarr00;
		 nullable (nullable Sub)[.] subarr11 = null;
		 subarr11=subarr01;
		 
		 Sup[.] suparr00 = subarr00;
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
		 
		 exception(suparr00,subarr00,new Sup(),yes);
		 exception(suparr01,subarr00,new Sup(),yes);
		 exception(suparr01,subarr00,null,yes);
		 exception(suparr01,suparr00,null,yes);
		 exception(suparr10,subarr00,new Sup(),yes);
		 exception(suparr10,subarr10,new Sup(),yes);
		 exception(suparr11,subarr00,new Sup(),yes);
		 exception(suparr11,subarr00,null,yes);
		 exception(suparr11,subarr01,new Sup(),yes);
		 exception(suparr11,subarr01,null,no);
		 exception(suparr11,subarr10,new Sup(),yes);
		 exception(suparr11,subarr10,null,yes);
		 exception(suparr11,subarr11,new Sup(),yes);
		 exception(suparr11,subarr11,null,no);
		 exception(suparr11,suparr00,null,yes);
		 exception(suparr11,suparr01,null,no);
		 exception(suparr11,suparr10,null,yes);

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
