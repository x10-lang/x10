/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * (here).id compiled properly but here.id did not.
 * (as of 5/28/2005)
 *
 * @author kemal, 5/2005
 */
public class HereParentheses extends x10Test {

	public def run(): boolean = {
		x10.io.Console.OUT.println("(here).id="+(here).id+" (here).next()="+(here).next()
				   + " (here).prev()="+(here).prev());
		x10.io.Console.OUT.println("here.id="+here.id+ " here.next()=" 
				   + here.next()+" here.prev()"+here.prev());
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new HereParentheses().execute();
	}
}
