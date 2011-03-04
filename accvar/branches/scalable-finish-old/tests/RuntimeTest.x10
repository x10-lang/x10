package x10.lang;
import x10.array.*;
import x10.util.*;
public class RuntimeTest{
	  public def dummy_void():void{}
	   
	  public def foo():void{
          /*1 successful!
           * PolyScanner.x10:
           *		- loop(body: (Rail[int])=>void, p:Rail[int]!,q:Rail[int]!, r:int):	 
          
           val r1 = Region.makeEmpty(3);
           val ps1:PolyScanner! = new PolyScanner(r1);
           val v1:Rail[int]=[1,2,3];
           ps1.loop((vv:Rail[int])=>dummy_void(),v1,v1,0); */ 
          
           /*2
            * FastArray.x10: fail to compile!
            *           - this(dist: Dist{constant}, 
            		* init: (Point{self.rank==dist.rank})=>T){here == dist.onePlace}
   		: FastArray[T]{self.dist==dist}: 
           
           val R: Region = 1..100;
           val d:Dist{constant==true} = R -> here;
           f:(Point{self.rank==d.rank})=>int{here == d.onePlace} = (x:Point{self.rank==d.rank})=>3;
           val fa:FastArray[int]{sefl.dist==d} = new FastArray[int](d,f);
            */
           /*3 successful
            *Future.x10: 
        	- run(): 
        		
           dummy_D:()=>int = ()=>3;
           val f3 =new Future[int](dummy_D);
           f3.run(); */ 
           
           /*4
             PlaceLocalHandle.x10:
            	 - make[T](dist:Dist, init:()=>T!){T <: Object}:PlaceLocalHandle[T]:
          	
	    val R: Region = 1..100;
	    val d:Dist = R -> here;
	    val s:String! = new String();
	    f4:(()=>String!) = ()=> s;
            val ph = PlaceLocalHandle.make[String](d,f4);  */	
            
             
            /*5
             Runtime.x10:
            	  - start(init:()=>Void, body:()=>Void):Void:
            		
             Runtime.start(()=>dummy_void(),()=>dummy_void());  
            */
            /*6 successful
             System.x10:
            	  - makeRemoteRail[T](p:Place, length:Int, init: Rail[T]!): Rail[T]{self.length==length}:
          		 */ 
	     val r = [1,2,3] as Rail[int];
             val p = r.home;
             val rr = System.makeRemoteRail[int](p,3,r);    
            
             
             /*7 wala fails to build the callgraph
              DistributedRail.x10:
            	  - reduceLocal (op:(T,T)=>T):
                  - reduceGlobal (op:(T,T)=>T):
                  - bcastLocal (op:(T,T)=>T):
                  - collectiveReduce contains all the previous three methods
 
             val dr = new DistributedRail[int](3,[1,2,3]); 
             dr.collectiveReduce((x:int,y:int)=>x+y);
             */
                         
	 }
}
