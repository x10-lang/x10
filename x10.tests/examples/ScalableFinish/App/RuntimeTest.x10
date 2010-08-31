/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
import x10.array.*;
import x10.util.*;
/**
 * Description: 
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
public class RuntimeTest{

	public def dummy_void():void{}
        
        
        public def run() {
		
           
           /*3 successful
            *Future.x10: 
        	- run():  */ 
        		
           dummy_D:()=>int = ()=>3;
           val f3 =new Future[int](dummy_D);
           f3.run();
           
           /*4
             PlaceLocalHandle.x10:
            	 - make[T](dist:Dist, init:()=>T!){T <: Object}:PlaceLocalHandle[T]:
          	*/	
            		 val R: Region = 1..100;
            	 val d:Dist = R -> here;
            	 val s:String! = new String();
            	 f4:(()=>String!) = ()=> s;
            	 val ph = PlaceLocalHandle.make[String](d,f4);  
            
            
            
             
             /*7 wala fails to build the callgraph
              DistributedRail.x10:
            	  - reduceLocal (op:(T,T)=>T):
                  - reduceGlobal (op:(T,T)=>T):
                  - bcastLocal (op:(T,T)=>T):
                  - collectiveReduce contains all the previous three methods
             */
             val dr = new DistributedRail[int](3,[1,2,3]); 
             dr.collectiveReduce((x:int,y:int)=>x+y);
             
             /* DistArray */
             val r1:Region = 1..100;
             val d1:Dist = R -> here;
             val da = DistArray.make(d1);
             val op = (x:int,y:int)=>{3};
             da.reduce(op,1);
	}

	public static def main(args: Rail[String]) {
		new RuntimeTest().run();
	}
}


         
