/*
 * (C) Copyright IBM Corporation 2006
 *
 *  HelloWorld1 program for X10.
 */
public class HelloWorld1 extends x10Test {

	public boolean run() {
		async (this) { System.out.println ("Hello X10 world!"); }
		return false;
	}

	public static void main(String[] args) {
		new HelloWorld1().run();
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111709
//@@X101X@@TCASE@@HelloWorld1
//@@X101X@@VCODE@@FAIL_COMPILE
//@@X101X@@TOUT@@0 60
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
