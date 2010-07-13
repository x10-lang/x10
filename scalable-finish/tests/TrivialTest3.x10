package x10.lang;
import x10.compiler.*;
public class TrivialTest3 {
  
public def f1():void {
	
	var i:int = 1;
i++;
	//throw new IllegalOperationException();

}

public def foo():void throws IllegalOperationException {

      var f:boolean  = true;
      if(f){
    	  f1();
    	  throw new IllegalOperationException();
    	  //f1();
    	  }
      var i:int = 1;
      i++;
      i = i + 3;
      
   }

  
  
}


