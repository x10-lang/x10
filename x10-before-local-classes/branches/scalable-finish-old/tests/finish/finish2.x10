package x10.lang;
import x10.array.*;
 // This program tests finish statements which have multiple asyncs and
 // at, and their combinations. 
public class finish2 {

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

        // finish with multiple asyncs
        finish{
                async{}
                async{}
        }

        finish{
                async{}
                f1();
                async{}
        }

        // finish with multiple at
        finish{
                at(here){}
                at(here){}
        }

        finish{
                at(here){}
                f2();
                at(here){}
        }

        // finish with async and at
        finish{
                at(here){}
                i = i + 1;
                async{}
        }
        finish{
                at(here){}
                f1();
                i = i + 1;
        }
        finish{
                async{}
                f2();
        }

  }        
}


