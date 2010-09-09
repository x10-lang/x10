package x10.lang;
import x10.array.*;
 // This program tests simple finish: a finish only contains a single 
 // async or at or method call. 

public class finish1 {

  public def f1():void {
         // method contains async
         async{}
  }
  public def f2():void {
        // method contains at
        at(here){}
  } 
  public def foo():void {
        var i:int = 0;
        // finish without any statement
        finish{
                
        }
        // finish with single async
        finish{
                async{}
        }
        // finish with single at
        finish{
                at(here){}
        }

        // finish with other statements
        finish{
                i = i + 1;
        }
        
        // finish with method call
        finish{
                f1();
        }
        finish{
                f2();
        }


  }        
}


