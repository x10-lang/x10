package x10.lang;
import x10.array.*;
public class async1 {

  public def work():void{}
  public def foo():void {
        finish async(here.next()){
                val R: Region = 1..100;
                val d:Dist = R -> here;
                ateach(p in d){
                }
        }
  }        
}


