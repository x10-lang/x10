import harness.x10Test;
/**
 * Test that if a class implements an interface, and the interface specifies a property, 
 * then the class defines the property.
 * @author vj
 */
public class InheritedProperty_MustFailCompile extends x10Test { 

    public static interface Test (l:int) {
      def put():int;
    }
    
    // fail here
    class Tester  /*(l:int)*/ implements Test {
      public def this(arg:int):Tester {  }
      public def put()=0;
	}
 
    public def run()=false;
    
    public static def main(Rail[String]) {
    	new InheritedProperty_MustFailCompile().execute();
    }
}