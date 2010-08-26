package x10.lang;
import x10.array.*;

public class TrivialTest {
  
  public def f1(x:int):void{
	finish{
		async{
			
			var y:int = 0;
			y = x +1 ;
		}
	}
  }
  
  public def f2():int{
	  return 2;
  }
  
  public def f3():void{
	  f2();
	  f3();
	  f1(3);
  }
  
  public def f4():void{
	  var f:boolean = false;
          if(f){
        	  async{}
        	  return;
          }
          async{}
          async{f4();}
          async{};
  }
  public def f5():void{
	  async{f5();}
  }
  public def f6():void{
	  f5();
  }
  /**/public def f7(body:()=>void):void{
	  body();
  }
  
  /*public def fthr(x:int):boolean{
	  if(x>0){
		  return true;
	  }
	  else{
		  throw new ClockUseException();
		  
	  }
  }*/
  
  public def f8():void{
	  f9();
  }
  public def f9():void{
	  f10();
  }
  public def f10():void{
	  
  }
  public def foo():void {
	  
	  
	  
	  /*finish{
		  async{}
		  async{
			  f2();
		  }
	  }*/
	  //f8();
	  /* test last statement 
	  f2();
	  at(here){
		  finish{
                  at(here){
			  async{}
		  }}
		  async{}
                  at(here){
                          async{}
                }
	  }*/	  
          
          
          
          
          /*at(here){
		  async{}
	  }*/
	  
	  /*4 
	   * f6() is not marked as the last stmt in finish, because ir has a goto after f6()
	   finish{
		  f6();
	  }*/
	  /* 3 
	  finish{
		  
	  var i:int = 1;
		  async{}
		  i = 1;
		  async{}
		  i = i + 1;
		  async{}
		  
		  
	  } */
	  
	  /*2 
	  finish{
		  async{}
		  async{}
	  }*/
	  /* 1  
	  finish{
		  var i:int = 0;
	  
		  async{}
		  i = i + 1;
	  }*/
	  /* test closure
		
		body : ()=>void = ()=> f1(3);
		f7(body); */
		
	  /* test ateach	 
	  val R: Region = 1..100;
	  val d:Dist = R -> here;
	  ateach(p in d){
		  async(p){}
	  }*/ 

	  /* test foreach
	  val r = 0..10;
	  
	  foreach (p:Point(1)  in r){
		  var i:int = 0;
	  
		 i = i + 1; 
	  }*/
	  /* test throw 
	
		try {
	          
	        val f = fthr(-1);
	        return ;
	      } catch (e: Exception) {
	          return ;
	      }	  */
  
	 
	 
	 /* was failed : arity is not correct "asyncs" within a method call   
	  * now successful:
	  var i:int = 1;
  i = i + 1;
  finish{
  if(i>0){
 	 if(i<3){
 		 async{var k:int = 1;}
 		 i = 3;
 	 }
 	 else{
 		while(false){
 		async{var k:int = 1;}
 		}
 		 i = 8;
 	 }
 	async{var k:int = 1;}
  }
  else{
 	 var j:int = 0;
  async{var k:int = 1;}
          for(;j<10;j++){
        	  async{
        		  async{var k:int = 1;}
        	}
         	 j = j + 1;
          }
  }
  async{var k:int = 1;}
  }
  i = i + 1;
  async{var k:int = 1;}*/
	 /* single loop
	 var i:int = 1;
	 for(i=3;i<9;i++){
		 i = i +1;
		 f2();
		 i = i + 1;
	 }
	 */
	 /*var i:int = 1;
	 for(i=3;i<9;i++){
		 for(i=3;i<9;i++){
			 f2();
			 i = i +1;
			 if(i>5){
				 f2();
				break; 
			 }
		 }
		 i = i + 1;
		 if(i<3){
			 f2();
			 continue;
		 }
	 }*/
	 
	 
	 
	 }*/
	 
	 
	 
		 
		 
		 */	
	/*val f = true;
		 if(f){finish{
		 at(here){
		 
			 
				 async{}
			 }
		 }
		 }*/
		 
	/*
	finish{
			 at(here){
				 async{}
				 at(here){
					 async{}
				 }
				 async{}
				 at(here){}
			 }
		 }*/
		 /* 
		 finish{
			 at(here){
				 async{}
				 
				 finish{at(here){
					 async{}
				 }}
				 async{}
				 at(here){}
			 }
		 }*/
		 /* 
		 finish{
			 at(here){
				 async{}
				 
				 at(here){
					 finish{	 async{}
				 }}
				 async{}
				 at(here){}
			 }
		 }*/
		 /* 
		 finish{
			 val f1 = true;
			 if(f1){
			 at(here){
				 async{}
				 at(here){
					 async{}
				 }
				 async{}
				 at(here){}
			 }
			 }
			 
			 var i:int = 1;
			 for(i=0;i<1;i++){
			 at(here){
				 async{}
				 at(here){
					 async{}
				 }
				 async{}
				 at(here){}
			 }
			 }
			  
			 
		 }*/
		 
		 /*finish{
			 val f1 = true;
			 if(f1){
			 at(here){
				 async{}
				 at(here){
					 async{}
				 }
				 async{}
				 at(here){}
			 }
			 }
			 
			
			 at(here){
				 var i:int = 1;
			 for(i=0;i<1;i++){
				 
				 async{}
				 finish{at(here){
					 async{}
				 }
				 }
			 }
				 async{}
				 at(here){}
			 
			 }
			  
		
		 }*/
		 
	//cannot find
	//async(here){f1(f2());}
	
	//cannot find*/
	/*async(here){
		f1(2);
		f2();
	}
	
	//find methods
	//f1(2);
	//f2();
	
	//find methods
	//f1(f2());
	//f3();
	
	/* test recursion */
	/**/finish{
	f4();
	f5();
	}
	
	
	
	
  }

  
  
}


