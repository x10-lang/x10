public class HWInlineFunc {
        public static void foo(int i) {

                System.out.println(i);
                bar();
                System.out.println(i+1);

        }
        public static void bar(){
                
                System.out.println (0);
        }
	public static void main(String[] args) {
		System.out.println("Hello World");
                final int a = 0 ;
                foo(2);

	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110919
//@@X101X@@TCASE@@HWInlineFunc
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWInlineFunc.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Hello World
//@@X101X@@DATA@@2
//@@X101X@@DATA@@0
//@@X101X@@DATA@@3
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
