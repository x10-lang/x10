public class HWInlinableAsync {
	//static final int i = 10;

	public static void foo(/*final*/ int k) {
		final int j = 5;
		System.out.println(" foo " + k);
//		/*async (here)*/ { System.out.println(" foo " + k); }
	}
    

	public static void main(String [] args) {
		final int j = 100;
		final dist /*(:rank==1)*/ ALLPLACES= /*(dist(:rank==1))*/ dist.factory.unique(); 
		finish /* async */{
		    clock clk=clock.factory.clock();
		    ateach (point [PID]: ALLPLACES) clocked(clk){
				if (true) { 
							final int beta = 3;
							foo (5);
							async (here) clocked(clk) {
			    				int alpha = 5;
			    				//throw new x10.lang.Exception();
								alpha = j + j + alpha + beta;}
				}
			}
		}
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110859
//@@X101X@@TCASE@@HWInlinableAsync
//@@X101X@@VCODE@@FAIL_BUILD
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWInlinableAsync.cc
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
