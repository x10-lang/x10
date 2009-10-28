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
		public static def format(var t: double): String = { return ""+t; }
	}

	public def run(): boolean = {
		var t: double = 25;
		var tmax: double = 200;
		x10.io.Console.OUT.println("	--> total mg-resid "+fmt.format(t)+
				" ("+fmt.format(t*100./tmax)+"%)");
		x10.io.Console.OUT.println("		Hello		world!		");
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new TabsInStrings().execute();
	}
}
