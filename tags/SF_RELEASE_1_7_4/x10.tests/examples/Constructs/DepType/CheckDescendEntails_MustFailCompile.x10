/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that x=y implies x.f=y.f.
 *
 * @author vj
 */
public class CheckDescendEntails_MustFailCompile extends x10Test {
	
    class Test(a:Prop, b:Prop) {
        public def this(val a: Prop, val b: Prop): Test{self.a==a&&self.b==b} = { 
        	property(a,b);
        }
    }
    public def run(): boolean = {
	val p = new Prop(1,2);
		
	var t: Test{self.a == self.b} = new Test(p,p);
	var u: Test{self.a.i == self.b.j} = t; // this should fail type check.
	return true;
    }
    public static def main(var args: Rail[String]): void = {
	new CheckDescendEntails_MustFailCompile().execute();
    }
}
