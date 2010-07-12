package x10.lang;
import x10.compiler.*;
public class TrivialTest3 {
  
public def f1():void {
	
	var i:int = 1;
i++;
	//throw new IllegalOperationException();

}

public def foo():void throws IllegalOperationException {
	//@FinishAsync(1,1,true,"async")
	//@FinishAsync(1,1,true,"at")
	@FinishAsync(1,1,true,"async")
	  finish{
	  async{}
	  at(here){}
	  async{}
      }
     /* var f:boolean  = true;
      if(f){
    	  throw new IllegalOperationException();
    	  //f1();
    	  }
      var i:int = 1;
      i++;
      i = i + 3;*/
      
   }

  
  
}


