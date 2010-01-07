/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test assigments and coercions.
 *
 * @author nystrom 9/2008
 */
public class Assign2 extends x10Test {
	public def run(): boolean = {
                val a = Rail.make[String](5, (int)=>"hi");
                var i:Settable[int,String] = a;
                i.set("bye", 1);

                return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Assign2().execute();
	}
}

