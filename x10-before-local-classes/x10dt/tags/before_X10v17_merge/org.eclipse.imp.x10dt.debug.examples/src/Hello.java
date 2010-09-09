import x10.lang.*;

public class Hello
extends x10.
  lang.
  Object
{
    
    
//#line 2
public Hello() {
        
//#line 2
super();
    }
    
    
//#line 4
/* template:Main { */
    public static class Main extends x10.runtime.Activity {
    	private final String[] form;
    	public Main(String[] args) {
    		super("Main Activity");
    		this.form = args;
    	}
    	public void runX10Task() throws Throwable {
    		main(form);
    	}
    }
    
    // the original app-main method
    public static  void main(/* Join: { */java.
      lang.
      String[] args/* } */)  {
    	if (x10.lang.Runtime.runtime == null) {
    		java.lang.System.err.println("Please use the 'x10' script to invoke X10 programs, or see the generated");
    		java.lang.System.err.println("Java code for alternate invocation instructions.");
    		java.lang.System.exit(128);
    	}
    {
        
//#line 5
supertest tt;
        
//#line 6
final Test t =
          new T(
          );
        
//#line 7
/* template:Async { */(x10.lang.Runtime.asPlace(/* template:here { */x10.lang.Runtime.here()/* } */)).runAsync
        	(new x10.runtime.Activity() {
        		public void runX10Task() {
        			{
            
//#line 8
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                       lang.
                                                                                                       System.
                                                                                                       out))/* } */.println("Entered new activity");
            
//#line 9
/* template:finish { */
            {
            	x10.lang.Runtime.getCurrentActivity().startFinish();
            	try {
            		{
                
//#line 9
/* template:Async { */(x10.lang.Runtime.asPlace((/* template:here { */x10.lang.Runtime.here()/* } */).
                                                                             next())).runAsync
                	(new x10.runtime.Activity() {
                		public void runX10Task() {
                			{
                    
//#line 10
x10.
                                  lang.
                                  Runtime.
                                  sleep(
                                  1000);
                }
                		}
                	});/* } */
            }
            	} catch (Throwable tmp0) {
            		x10.lang.Runtime.getCurrentActivity().pushException(tmp0);
            	} finally {
            		x10.lang.Runtime.getCurrentActivity().stopFinish();
            	}
            }
            /* } */
            
            
//#line 12
x10.
                          lang.
                          Runtime.
                          sleep(
                          1000);
            
//#line 13
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                        lang.
                                                                                                        System.
                                                                                                        out))/* } */.println("Exited new activity");
        }
        		}
        	});/* } */
    }
    }
    
    // How to invoke?  Use the following general command:
    // java $(javaArgs) x10.lang.Runtime $(x10Args) ClassName $(x10AppArgs)
    /* } */
    
}
