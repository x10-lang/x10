public class HelloWorld {
	public static void main(String[] args) {
		System.out.println("Hello World");
		for (int i = 0; i < args.length; i++)
			System.out.println("Arg "+i+": "+args[i]);
		String prefix = "3 = ";
		double three = 3;
		System.out.println(prefix+three);
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111644
//@@X101X@@TCASE@@HelloWorld
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HelloWorld.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Hello World
//@@X101X@@DATA@@3 = 3.0
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
