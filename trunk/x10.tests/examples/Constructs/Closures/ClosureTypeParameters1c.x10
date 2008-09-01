// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Closure expressions have zero or more type parameters,
 * 
 * Methods, constructors, closures, and type definitions may have type
 * parameters, which are instantiated with concrete types at invocation
 * (§4.2).
 * 
 * A method, constructor, or closure may have type parameters whose scope
 * is the signature and body of the declaring method, constructor or
 * closure.
 * 
 *
 * @author bdlucas 8/2008
 */

public class ClosureTypeParameters1c extends ClosureTest {

    class V           {static val name = "V";};
    class W extends V {static val name = "W";}
    class X extends V {static val name = "X";};
    class Y extends X {static val name = "Y";};
    class Z extends X {static val name = "Z";};

    public def run(): boolean = {
        
        val f = [T](x:T,y:T) => x.toString() + y.toString();
        check("f[String](\"1\",\"1\")", f[String]("1","1"), "11");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureTypeParameters1c().execute();
    }
}
