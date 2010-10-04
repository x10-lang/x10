
class Foo{
	public static def foo4(){}
	public def foo3(){}
}
public class TrivialTest{


  
  public static def main(args: Rail[String]) {
        //t1();
  	//t2();
  	//t3();
  	//t4();
  	//t5();
  	//t6();
  	//t7();
  	//t8();
  	//t9();
  	//t10();
  	//t11();
  	//t12();
  	//t13();
  	//t14();
  	//t15();
        //t16();
  	//t17();
  	//t18();
  	//t19();
  	//t20();
  	//t21();
  	//t22();
    	//t23();
    	//t24();
        t25();
  }
  
  public static def t1(){
  	finish{
  		async{}
  		async{
  			f2();
  		}
  	}
  	f8();
  }
  public static def t2(){
  	/* test last statement */ 
  	f2();
  	finish{
  		finish{
        		at(here){
  				async{}
  			}
        	}
  		async{}
        	at(here){
        		async{}
        	}
  	}	
  }
  
  public static def t3(){
  	at(here){
  		async{}
  	}
  }
  
  public static def t4(){
  	/* f6 is not marked as the last stmt */
  	finish{
  		f6();
  	}
  }
  
  public static def t5(){
  	finish{
  		var i:int = 1;
  		async{}
  		i = 1;
  		async{}
  		i = i + 1;
  		async{}
  	}
  }
  
  public static def t6(){
  	finish{
  		async{}
  		async{}
  	}
  }
  
  public static def t7(){
  	finish{
  		var i:int = 0;
  		async{}
  		i = i + 1;
  	}
  }
  
  public static def t8(){
  	/* test closure */
  	val f = new Foo();
  	val body:()=>Void = ()=>{
  		// non-static member function
  		new TrivialTest().foo();
  		// static member function
  		async(here.next()){f2();}
  		// non-static non-member function
  		f.foo3();
  		//static non-member function
  		Foo.foo4();
  	};
  	finish{
  		async(here){
  			body();
  		}
  	}
  }
  
  public static def t9(){
  	/* test ateach */
  	val R: Region = 1..100;
  	val d:Dist = R -> here;
  	finish ateach(p in d){
  		async(p){}
  	}
  }
  
  public static def t10(){
  	/* test foreach */
  	val r = 0..10;
  	finish foreach (p:Point(1)  in r){
  		var i:int = 0;
  		i = i + 1; 
  	}
  }
  
  public static def t11(){
  	/* test throw exceptions */
  	try { 
  		val f = f11(-1);
  		return ;
	} catch (e: Exception) {
    		return ;
	}
  }
  
  public static def t12(){
  	/* test loops */
  	var i:int = 1;
  	i = i + 1;
  	finish{
  		if(i>0){
  			if(i<3){
  				async(here.next()){var k:int = 1;}
  				i = 3;
  			}else{
  				while(i>3){
  					async(here.next()){var k:int = 1;}
  				}
  				i = 8;
  			}
  			async(here.next()){var k:int = 1;}
  		}else{
  			var j:int = 0;
  			async(here.next()){var k:int = 1;}
          		for(;j<10;j++){
        	  		async{
        		  		async(here.next()){var k:int = 1;}
        			}
         	 		j = j + 1;
          		}
  		}
  		async{var k:int = 1;}
  	}
  	i = i + 1;
  	async(here.next()){var k:int = 1;}
  }
  
  public static def t13(){
  	/* test single loop */
  	var i:int = 1;
  	for(i=3;i<9;i++){
  		i = i +1;
  		f2();
  		i = i + 1;
  	}
  }
  
  public static def t14(){
  	/* test loop with break and continue */
  	var i:int = 1;
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
  	}
  }
  
  public static def t15(){
  	/* arity of at and async should be one */
  	val f = true;
  	if(f){
  		finish{
  			at(here){
  				async{}
  			}
  		}
  	}
  }
  
  public static def t16(){
  	finish{
  		at(here){
  			async{}
  			at(here){
  				async{}
  			}
            		at(here){}
  			async{}
  			
  		}
  	}
  }
  
  public static def t17(){
  	finish{
  		at(here){
  			async{}
  			at(here){
  				finish{	 
  					async{}
  				}
  			}
  			async{}
  			at(here){}
  		}
  	}
  }
  
  public static def t18(){
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
  	}
  }
  
  public static def t19(){
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
  		at(here){
  			var i:int = 1;
  			for(i=0;i<1;i++){
  				async{}
  				finish{
  					at(here){
  						async{}
  					}
  				}
  			}
  			async{}
  			at(here){}
  		}
  	}
  }
  
  public static def t20(){
  	/* test method call within an async */
  	async(here){
  		f1(f2());
  	}
  }
  public static def t21(){
  	val f = new Foo();
  	async{
  	    // non-static member function
  	    new TrivialTest().foo();
  	    // static member function
  	    f2();
  	    //non-static non-member function
  	    f.foo3();
  	    //static non-member function
  	    Foo.foo4();
  	}
  }
  
  public static def t22(){
  	/* test recursion */
  	finish{
  		f4();
  		f5();
  	}
  }
  public static def t23(){
  	f12();
  }
  public static def t24(){
	  finish{
		  val p = here;
		  async(p.next()){
			  async(p){}
		  }
	  }
  }
  public static def t25(){
  	val p = here;
        val p2 = here;
        val p1 = p;
        async(p){
             async(p1){}
        }
  }
  /* non-static method */
  public def foo(){
  
  }
  public static def f1(x:int):void{
	finish{
		async{
			
			var y:int = 0;
			y = x +1 ;
		}
	}
  }
  
  public static def f2():int{
	  return 2;
  }
  
  public static def f3():void{
	  f2();
	  f3();
	  f1(3);
  }
  
  public static def f4():void{
	  var f:boolean = false;
          if(f){
        	  async{}
        	  return;
          }
          async{}
          async{f4();}
          async{};
  }
  
  
  public static def f5():void{
	  async(here){f5();}
  }
  public static def f6():void{
	  f5();
  }
  public static def f7(body:()=>void):void{
	  body();
  }
  
  
  
  public static def f8():void{
	  f9();
  }
  public static def f9():void{
	  f10();
  }
  public static def f10():void{
	  
  }

  public static def f11(var x:int){
  	if(x>0){
		return true;
	}
	else{
		throw new ClockUseException();	  
	}
  }
  public static def f12(){
  	finish{
        	async(here.next()){
                	f13();
        	}
  	}
  }
  public static def f13(){
     	async(here.next()){
     		f12();
       	}
  }
  
}


         
