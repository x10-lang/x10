package x10.lang;
import x10.array.*;
 // This program tests at/async in if-else
public class finish6 {

  public def f1():void {
         // method contains async
         async{}
  }
  public def f2():void {
        // method contains at
        at(here){}
  } 
  public def foo():void {
        var i:int = 1;
        for(;;){
                finish{
                        async{}
                }
                finish{
                        at(here){}
                }
                async{}
                at(here){}
        }
        for(;;){
                for(;;){
                        finish{
                                async{}
                        }
                        finish{
                                at(here){}
                        }
                        async{}
                        at(here){}
                }
                f2();
        }
        for(;;){
        	for(;;){
        		async{}
        		break;
        	}
        	f1();
        }
        for(;;){
        	for(;;){
        		async{}
                        continue;	
        	}
        	f1();
        }
        for(;;){
		 finish{
			 for(;;){
				 async{}
			 }
		 }
        }
        finish{
		 for(;;){
			 async{}
		 }
		
	 }
  }        
}


