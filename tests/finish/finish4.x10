package x10.lang;
import x10.array.*;
 // This program tests nested at and async 
public class finish4 {

  public def f1():void {
         // method contains async
         async{}
  }
  public def f2():void {
        // method contains at
        at(here){}
  } 
  public def foo():void {

        finish{
                at(here){
                        async{}
                }
                async{
                        at(here){}
                }
        }
        async{
                finish{
                        at(here){}
                }
                at(here){
                        finish{}
                        async{}
                }
        }
        at(here){
                finish{
                        async{}
                }
                async{
                        finish{
                                async{}
                        }
                }
        }

        at(here){
                async{}
                at(here){
                        async{}
                }
                f1();
        }
        async{
                async{
                        f2();
                }
        }
  }        
}


