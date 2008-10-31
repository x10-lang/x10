/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensure that tabs in strings do not cause compilation errors.
 * @author vj
 * @author kemal 11/2005
 */
public class TabsInStrings extends x10Test {

	static class fmt {
		public static String format(double t) { return ""+t; }
	}

	public boolean run() {
		double t = 25;
		double tmax = 200;
		System.out.println("	--> total mg-resid "+fmt.format(t)+
				" ("+fmt.format(t*100./tmax)+"%)");
		System.out.println("		Hello		world!		");
		return true;
	}

	public static void main(String[] args) {
		new TabsInStrings().execute();
	}
}

