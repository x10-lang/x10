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
	  
	  
	  
	  finish{
		  async{}
		  async{
			  f2();
		  }
	  }
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
	  /* test ateach	  val R: Region = 1..100;
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
  
	 /*var i:int = 1;
         //i = bar(((x:int) => x +1),i);
         finish{
        	 var j:int = 1;
                 j = j + 1;
                 finish{}
                 
         }*/
         
         /*
          *PolyScanner.x10:
        		- loop(body: (Rail[int])=>void, p:Rail[int]!,q:Rail[int]!, r:int):	 
          
         val r = Region.makeEmpty(3);
         val ps:PolyScanner! = new PolyScanner(r);
         val v:Rail[int]=[1,2,3];
         ps.loop((v:Rail[int])=>f2(),v,v,0);*/
         
         /**
          * test InvertedGraph and Dominator in wala 
          *

	         
	 */
	 
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
	 /* consecutive loop
	  var i:int = 1;
	 for(i=3;i<9;i++){
		 i = i +1;
		 f2();
		 i = i + 1;
	 }
	 for(i=3;i<9;i++){
		 i = i +1;
		 f2();
		 i = i + 1;
	 }
	 * 
	 */
	 
	 /* nested loop 
	  var i:int = 1;
	 for(i=3;i<9;i++){
		 i = i +1;
		 f2();
		 
		 for(i=3;i<9;i++){
			 i = i +1;
			 f2();
			 i = i + 1;
		 }
		 i = i + 1;
	 }
	 * 
	 */
	 /*
	  var i:int = 1;
	 for(i=3;i<9;i++){
		 for(i=3;i<9;i++){
			 i = i +1;
			 f2();
			 i = i + 1;
		 }
	 }
	 */
	 
	 /*var i:int = 1;
	 for(i=3;i<9;i++){
		 for(i=3;i<9;i++){
			 i = i +1;
			 if(i>5){
				break; 
			 }
		 }
		 i = i + 1;
		 if(i<3){
			 continue;
		 }
	 }*/
	 
	 /* arity=1: pass!
	 finish{
		 async{ var i:int = 1;}
	 }
	 */
	 
	 /* arity=1: pass!
	 var i:int = 1;
	 for(i=1;i<10;i++){
		 finish{
			 async{var j:int = 1;}
		 }
	 }*/
	 /* arity=unbounded: pass!
	 var i:int = 1;
	 var j:int = 1;
	 for(i=1;i<10;i++){
		 finish{
			 for(j=1;j<10;j++){
				 async{var k:int =1;}
				 j++;
			 }
		 }
	 }*/
	 /* arity=unbounded: pass!
	 var j:int = 1;
	 finish{
		 for(j=1;j<10;j++){
			 async{var k:int =1;}
		 }
		
	 }*/
	 /* arity=ZeroOrOne: pass!
	 var f:boolean=true;
	 finish{
		 if(f){
			 async{var k:int =1;}
		 }
		 async{}
	 }*/
	 /*
	  at(here){
		 val i = 1;
	 }*/
	 
	 /*
	 at(here){

	   val f = true;
	     if(f){
		 async{}
	     }
	 }*/
	 
	 
	 /*
	  * should this at care about its finish? - No
	 at(here){
		 finish{
			 async{}
		 }
	 }
	  */
	 /*
	  at(here){
			 finish{
				 
			 val f = true;
			if(f){	 async{}}
			 async{}
			 }
			 async{}
		 }*/
	 
	/*	 
	  at(here){ val f = true;
		if(f){	 
			 finish{
				 
			async{}}
			 }
		 }*/
		 
	/*
		 finish{
			 at(here){
				 async{}
			 }
		 }
	 */
	 /*finish{
		 
	 val f = true;
	 if(f){
		 at(here){
			 async{}
		 }
	 }
	 }*/
	 /*finish{
		 at(here){
		 
			 
				 async{}
			 }
		 }*/
		 
		 
	/*finish{
			 finish{
				 async{}
				 at(here){
					 at(here){
						 async{}
					 }
				 }
			 }
			 async{}
		 }*/
		 /* 
		 finish{
			 at(here){
			 finish{
				 async{}
				 
					 at(here){
						 async{}
					 }
				 
			 }
			 
		 }
			 async{}
		 }*/	
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
	
	//cannot find
	/*async(here){
		f1(2);
		f2();
	}*/
	
	//find methods
	//f1(2);
	//f2();
	
	//find methods
	//f1(f2());
	//f3();
	
	/* test recursion */
	/*finish{
	f4();
	f5();
	}*/
	
	
	
	
  }

  
  
}


