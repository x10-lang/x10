package bug081117;

import x10.lang.*;

public class Hello
extends x10.
  lang.
  Object
{
    
    
//#line 3
public Hello() {
        
//#line 3
super();
    }
    
    
//#line 5
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
        
//#line 6
bug081117.
          supertest tt;
        
//#line 7
final bug081117.
          Test t =
          new bug081117.
          T(
          );
        
//#line 8
/* template:Async { */(x10.lang.Runtime.asPlace(/* template:here { */x10.lang.Runtime.here()/* } */)).runAsync
        	(new x10.runtime.Activity() {
        		public void runX10Task() {
        			{
            
//#line 9
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                       lang.
                                                                                                       System.
                                                                                                       out))/* } */.println("Entered new activity");
            
//#line 10
/* template:finish { */
            {
            	x10.lang.Runtime.getCurrentActivity().startFinish();
            	try {
            		{
                
//#line 10
/* template:Async { */(x10.lang.Runtime.asPlace((/* template:here { */x10.lang.Runtime.here()/* } */).
                                                                              next())).runAsync
                	(new x10.runtime.Activity() {
                		public void runX10Task() {
                			{
                    
//#line 11
x10.
                                  lang.
                                  Runtime.
                                  sleep(
                                  1000);
                }
                		}
                	});/* } */
            }
            	} catch (Throwable tmp4) {
            		x10.lang.Runtime.getCurrentActivity().pushException(tmp4);
            	} finally {
            		x10.lang.Runtime.getCurrentActivity().stopFinish();
            	}
            }
            /* } */
            
            
//#line 13
x10.
                          lang.
                          Runtime.
                          sleep(
                          1000);
            
//#line 14
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
