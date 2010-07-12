package x10.lang;
import x10.array.*;
public class loop4 {

  public def work():void{}
  public def foo():void {
        val R: Region = 1..100;
        val d:Dist = R -> here;
        finish ateach(p in d){
                finish async(p){
                        while(true){
                                async{}
                        }
                }
        }
  }        
}


