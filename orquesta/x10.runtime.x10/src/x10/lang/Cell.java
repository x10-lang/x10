package x10.lang;

import x10.lang.annotations.*;
import x10.lang.*;

public class Cell
extends x10.
  lang.
  Object
{
    
//#line 7
public static interface T
                 extends x10.
                           lang.
                           Parameter
               {}
    
    
//#line 9
x10.
      lang.
      Object
      x;
    
    
//#line 10
x10.
                  lang.
                  Object
                  get(
                  ) {
        
//#line 10
return x;
    }
    
    
//#line 11
void
                  set(
                  x10.
                    lang.
                    Object x) {
        
//#line 11
this.
                      x =
          x;
    }
    
    
//#line 12
Cell(x10.
                       lang.
                       Object init) {
        
//#line 12
super();
        
//#line 13
x =
          init;
    }
    
    
//#line 15
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
      String[] t/* } */)  {
    	if (x10.lang.Runtime.runtime == null) {
    		java.lang.System.err.println("Please use the 'x10' script to invoke X10 programs, or see the generated");
    		java.lang.System.err.println("Java code for alternate invocation instructions.");
    		java.lang.System.exit(128);
    	}
    {
        
//#line 16
x10.
          lang.
          Cell x =
          new x10.
          lang.
          Cell(
          new x10.
            compilergenerated.
            BoxedInteger(
            0));
    }
    }
    
    // How to invoke?  Use the following general command:
    // java $(javaArgs) x10.lang.Runtime $(x10Args) ClassName $(x10AppArgs)
    /* } */
    
}
