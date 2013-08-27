/**
 * The canonical "Hello, World" demo class expressed in X10
 */


import x10.runtime.*;
import x10.util.*;
import x10.lang.*;
import x10.compiler.Transaction;
import x10.compiler.StackAllocate;

public class Hello {
	
	static val num_s:Int = 0;
	static val arr_s:Array[Long](1) = new Array[Long](5);
    /**
     * The main method for the Hello class
     */
    public static def main(Array[String]) {
        //finish for (p in Place.places()) {
        //    at (p) async Console.OUT.println("Hello World from place "+p.id);
        //}
        val a = new A();
        val Other  = here.next();
        
        //Runtime.initTMSystem();
        
        
        a.foo_1(3);
        a.num = a.num + 1;
        //a.arr(2) = a.num + 1;
        //Console.OUT.println(a.arr(2));
        
    	var num:Int = 0;
    	
    	a.arr_byte(0) = 0;
    	a.arr_short(0) = 0;
    	a.arr_int(0) = 0;
    	a.arr_long(0) = 0;
    	
    	/*at (Other) {
    		Runtime.initTMSystem();
    	}*/
    	finish for (var k:int=0; k < 1; k++)
    	{
    		async {
    			Runtime.initTMThread();
    			
    			for (var j:int = 0; j < 2; j++) {
	    			@Transaction
			    	atomic {
			        	// local variable
			        	num = num + 1;
			        	
			        	a.obj_any = new C[int,long](1, 2);
			        	a.obj_C = new C[int,long](1, 2);
			        	a.obj_D = new D[C[int,long]](a.obj_C); 
			        	a.obj_E = new E_Struct_Template[int, short](1,2);
			        	a.obj_F = new F_Struct(3,4);
			        	// 
			        	// field load and assignment - primitive types 
			        	a.num_byte = a.num_byte + 1;
			        	a.num_short = a.num_short + 1;
			        	a.num_int = a.num_int + 1;
			        	a.num_long = a.num_long + 1; 
			        	
			        	at (Other) {
			        		a.num_byte = a.num_byte + 1;
			        		a.num_short = a.num_short + 1;
			        		a.num_int = a.num_int + 1;
			        		a.num_long = a.num_long + 1;
			        	}
			        	
			        	/*at (Other) {
			        		a.num_byte = a.num_byte + 1;
			        		a.num_short = a.num_short + 1;
			        		a.num_int = a.num_int + 1;
			        		a.num_long = a.num_long + 1;
			        	}*/
			        	
			        	
			        	// field load and assignment - object types
			        	a.obj = new B();
			        	a.obj = a.obj;
			        	a.obj.boo_1(2);
			        	a.obj.boo_1(4);
			        	
			         	//array load and assignment - primitive types  
			         	
			         	
			        	a.arr_byte(0) = a.arr_byte(0) + 1;
			        	a.arr_short(0) = a.arr_short(0) + 1;
			        	a.arr_int(0) = a.arr_int(0) + 1;
			        	a.arr_long(0) = a.arr_long(0) + 1;
			
			        	a.arr_byte(0) = a.arr_byte(0) + 1;
			        	a.arr_short(0) = a.arr_short(0) + 1;
			        	a.arr_int(0) = a.arr_int(0) + 1;
			        	a.arr_long(0) = a.arr_long(0) + 1;
			        	
			        	// array load and assignment - object types  
			        	a.arr_obj(0) = new B();
			        	a.arr_obj(0).boo_1(6);
	        		}
    			}
    			
    			Runtime.finishTMThread();
    		}
        }
      
        Console.OUT.println("a.num_byte = " + a.num_byte);
        Console.OUT.println("a.num_short = " + a.num_short);
        Console.OUT.println("a.num_int = " + a.num_int);
        Console.OUT.println("a.num_long = " + a.num_long);
        Console.OUT.println("a.obj = " + a.obj);
      
        Console.OUT.println("a.arr_byte(0) = " + a.arr_byte(0));
        Console.OUT.println("a.arr_short(0) = " + a.arr_short(0));
        Console.OUT.println("a.arr_int(0) = " + a.arr_int(0));
        Console.OUT.println("a.arr_long(0) = " + a.arr_long(0));
       	Console.OUT.println("a.arr_obj(0) = " + a.arr_obj(0));

        atomic {
        	// local variable
        	num = num + 1;
        	
        	// field load and assignment - primitive types 
        	a.num_byte = a.num_byte + 1;
        	a.num_short = a.num_short + 1;
        	a.num_int = a.num_int + 1;
        	a.num_long = a.num_long + 1; 
        	
        	a.num_byte = a.num_byte + 1;
        	a.num_short = a.num_short + 1;
        	a.num_int = a.num_int + 1;
        	a.num_long = a.num_long + 1;
        	
        	// field load and assignment - object types
        	a.obj = new B();
        	a.obj = a.obj;
        	a.obj.boo_1(2);
        	a.obj.boo_1(4);
        	
        	// array load and assignment - primitive types  
        	a.arr_byte(0) = 0;
        	a.arr_short(0) = 0;
        	a.arr_int(0) = 0;
        	a.arr_long(0) = 0;
        	
        	a.arr_byte(0) = a.arr_byte(0) & 1;
        	a.arr_short(0) = a.arr_short(0) + 1;
        	a.arr_int(0) = a.arr_int(0) + 1;
        	a.arr_long(0) = a.arr_long(0) + 1;

        	a.arr_byte(0) = a.arr_byte(0) + 1;
        	a.arr_short(0) = a.arr_short(0) + 1;
        	a.arr_int(0) = a.arr_int(0) + 1;
        	a.arr_long(0) = a.arr_long(0) + 1;
        	
        	// array load and assignment - object types  
        	a.arr_obj(0) = new B();
        	a.arr_obj(0).boo_1(6);
        }
        
        Console.OUT.println(a.obj);
        Console.OUT.println(a.arr_obj(0));
        
        Runtime.finishTMSystem();
    }
}