package x10.lang;
import x10.util.*;
public class TrivialTest2 {
  
 
  public def f2():int{
	  return 2;
  }
  
  public def f3(x:Rail[int],y:Rail[int]):Rail[int]{
	  return x;
  }
  public def foo():void {
	  
	  f3([1,2,3],[1,2]);
	  async{
		  at(here){}
	  }
	  finish{
		 at(here){}
	  }
	 
	
	
  }

  
  
}


