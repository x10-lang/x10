package x10.lang;
import x10.array.*;
public class loop5 {

  public def work():void{}
  public def foo():void {
        finish{
                var i:int = 0;
                for(i=0;i<10;i++){
                        async{}
                }
        }
  }        
}


