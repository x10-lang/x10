import harness.x10Test;
/**
 * Test that if a class implements an interface, and the interface specifies a property, 
 * then the class defines the property.
 * @author vj
 */
public class InheritedInvariantMustBeEntailed_MustFailCompile extends x10Test { 

    public static interface Test (l:int, m:int){l==m} {
      def put():int;
    }
    
    //  must fail here
    class Tester  (l:int, m:int) implements Test {
      public def this(arg:int):Tester { property(arg,arg); }
      public def put()=0;
	}
 
    public def run()=false;
    
    public static def main(Rail[String]) {
    	new InheritedInvariantMustBeEntailed_MustFailCompile().execute();
    }
}