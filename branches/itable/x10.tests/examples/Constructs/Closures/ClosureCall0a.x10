// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: closure type params

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *
 * @author nystrom 8/2008
 * Fails Sun Mar 22 19:54:46 2009 with
C:\eclipse332\ws-mar09\x10.tests\examples\Constructs\Closures\ClosureCall0a.x10:22:
    Method apply[X0](Z1) in <anonymous subtype of (T) => T{self==x}> cannot be
    called with arguments (x10.lang.String{self=="hi", s=="hi"}); Could not
    infer type for type parameter X0.
C:\eclipse332\ws-mar09\x10.tests\examples\Constructs\Closures\ClosureCall0a.x10:22:
    Method apply[X0](Z1) in <anonymous subtype of (T) => T{self==x}> cannot be
    called with arguments (x10.lang.String{self=="hi", s=="hi"}); Could not
    infer type for type parameter X0.
2 errors.
 */

public class ClosureCall0a extends x10Test {

    public def run(): boolean = {
	val s= "hi";
        val a = ([T](x:T)=>x)(s);
        return a.equals(s);
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall0a().execute();
    }
}
