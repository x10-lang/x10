
/**
 * Value class test:
 *
 * assigning to a value class field must be 
 * flagged as an error by the compiler
 *
 * @author Mandana Vaziri, kemal 4/2005
 *
 */

value Test1 {
	int f;
	
	public Test1(int f){
		this.f = f;
	}
	
	public void modifier(){
//===> compiler error should occur here
		f++;
	}	
}

public class ValueClass5_MustFailCompile  {


        public boolean run() {
		
		Test1 x= new Test1(1);
		x.modifier();
		return x.f==2;

        }

	public static void main(String args[]) {
		boolean b= (new ValueClass5_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
