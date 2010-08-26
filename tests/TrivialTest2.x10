package x10.lang;
public class TrivialTest2 {
  
 
  public def f1():int{
	  var i:int = 1;
  async{}
	  return i + 1;
  }
  
  
  public def bar():int{
	  return 2;
  }
  
  
  public def f5():int{
	  return 2;
  }
  
  
  public def f4():int{
	  return 2;
  }
  public def f6():void{
  }
  public def f3(x:Rail[int],y:Rail[int]):Rail[int]{
	  return x;
  }
  public def f7() {
	  return true;
  }
  public def foo():void {
	  var i:int = 1;
  // finish{
	  // f4();
	  // async{}
	  // at(here){async{}}
  // }
	 // f5();
   // f3([1,2,3],[1,2]);
   f7();
	  //finish{
		  // f6(); 
		  // at(here){f1();}
		 // }
	  // async{
		  // at(here){}
		  // async{
			  // bar();
			  // at(here){}
		  // }
	  // }
	  // finish{
		 // at(here){f4();}
		 // finish{
			 // at(here){}
		 // }
	  // }
	  // at(here){
		  // at(here){f6();}
	  // }
	
	
   }

  
  
}


