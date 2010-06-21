package x10.lang;
import x10.array.*;

public class finish1 {
  
  public def foo():void {
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

  }        
}


