import harness.x10Test;
/**
 * Test that if a class implements an interface, and the interface specifies a property, 
 * then the class defines the property, and not just another nullary method with the same name 
 * that is public and safe.
 * @author vj
 */
public class InheritedProperty_MustFailCompile3 extends x10Test { 

    public static interface Test (l:int) {
      def put():int;
    }
    
    // fail here
    class Tester  /*(l:int)*/ implements Test {
      public def this(arg:int):Tester {  }
      public safe /*property*/ def l():int = 0;
      public def put()=0;
	}
 
    public def run()=false;
    
    public static def main(Rail[String]) {
    	new InheritedProperty_MustFailCompile3().execute();
    }
}