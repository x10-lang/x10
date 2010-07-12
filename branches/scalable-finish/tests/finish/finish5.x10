package x10.lang;
import x10.array.*;
 // This program tests at/async in if-else
public class finish5 {

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
        finish{
                if(i>1){
                        async{}
                }
                else{
                        at(here){}
                }
                f1();
        }

        if(i<1){
                finish{
                        at(here){}
                        async{}
                }

                async{}
                at(here){}
        }
        
        finish{
                async{
                        var i:int =1;
                        if(i==0){
                                async{}
                        }
                        if(i==1){
                                at(here){}
                        }
                }
        }
        finish{
                at(here){
        
                        var i:int =1;
                        if(i==2){
                                at(here){
                        
                                       async{}
                                }
                                async{}
                        }
                        at(here){}

                }
        }
  }        
}


