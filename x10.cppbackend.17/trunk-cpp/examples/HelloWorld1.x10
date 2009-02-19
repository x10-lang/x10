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

