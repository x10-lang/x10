package x10.lang;
import x10.array.*;
 // This program tests nested finish 
public class finish3 {

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
        // nested finish without statements
        finish{
                finish{
                }
        }

        // finish1 should only have async2 
        finish{// finish1
                
                finish{
                        //async1
                        async{}               
                }
                //async2
                async{}
        }

        finish{
                async{
                        finish{
                                async{}
                        }
                        f1();
                }
        }


        finish{                
                finish{
                        at(here){}               
                }
                at(here){}
        }

        finish{
                at(here){
                        finish{
                                at(here){}
                        }
                        f2();
                }
        }
finish{
			 at(here){
			 finish{
				 async{}
				 
					 at(here){
						 async{}
					 }
				 
			 }
			 
		 }
			 async{}
		 }
  }        
}


