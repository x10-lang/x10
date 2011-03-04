import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;
import x10.io.Console;
import x10.io.File;
import x10.io.IOException;
import x10.io.Marshal;
import x10.io.*;
import x10.util.Pair;
import x10.util.Timer;
@NativeCPPInclude("WSMPLib.h")
@NativeCPPCompilationUnit("WSMPLib.cpp")
@NativeCPPCompilationUnit("PWSMPLib.cpp")
public class EQSolver {
//    public static extern def wgsmp(n:int,ra:unsafe Rail[int],ja: Rail[int],avals: Rail[int], b: Rail[double], ldb:int, nrhs:int, rmisc:double,iparm:Rail[int],dparm:Rail[double]);

	@Native("c++","eqsolve(#1,(#2)->raw(),(#3)->raw(),(#4)->raw(),(#5)->raw(),#6,#7)")
	static native def eqsolve(n:int,ra:Rail[int],ja:Rail[int],avals:Rail[double],b:Rail[double],ldb:int,format:int):int;
	
	@Native("c++","peqsolve(#1,(#2)->raw(),(#3)->raw(),(#4)->raw(),(#5)->raw(),#6)")
	static native def peqsolve(n:int,ra:ValRail[int],ja:ValRail[int],avals:ValRail[double],b:Rail[double],ldb:int):int;
	
	@Native("c++","peqsolve_csc(#1,(#2)->raw(),(#3)->raw(),(#4)->raw(),(#5)->raw(),#6)")
	static native def peqsolve_csc(n:int,ra:ValRail[int],ja:ValRail[int],avals:ValRail[double],b:Rail[double],ldb:int):int;
	
	@Native("c++","wsetmaxthrds(#1)")
	static native def wsetmaxthrds(n:int):Void;
	
	@Native("c++","mpiCTest(#1)")
	static native def mpiCTest(n:int):Void;
	
    @Native("c++","wgsmp(#1,(#2)->raw())")
	static native def wgsmp(n:int,ra: Rail[int]):Void;
    
    @Native("c++","parseRBFile(#1->c_str(),#2,(#3)->raw(),(#4)->raw(),(#5)->raw()")
    
    static native def parseRBFile(fn:String, n:Int, ia: ValRail[Int],ja:ValRail[Int],avals:Rail[double] ):Void;
 
    static def openRBFile(fn:String) throws IOException = new File(fn).lines();
    static def readLineFromFile(f:ReaderIterator[String]!)=f.next();
    static def readTitleFromFile(f:ReaderIterator[String]!) {
        val line = f.next();
        val title = line.substring(0, 72);
        val mxid = line.substring(72, 80);
        val line2 = f.next();
        val nums = line2.split(" ");
        var i:Int = 0;
        for (; i < nums.length && nums(i).length()==0; i++) { }
        val totcrd = Int.parse(nums(i++));
        for (; i < nums.length && nums(i).length()==0; i++) { }
        val ptrcrd = Int.parse(nums(i++));
        for (; i < nums.length && nums(i).length()==0; i++) { }
        val indcrd = Int.parse(nums(i++));
        for (; i < nums.length && nums(i).length()==0; i++) { }
        val valcrd = Int.parse(nums(i++));
        return title;
    }
    static def readNFromFile(f:ReaderIterator[String]!) {
        val line = f.next();
        val entries = line.split(" ");
        var i:Int = 0;
        for (; i < entries.length && entries(i).length()==0; i++) { }
        val mxtype = entries(i++);
        if (mxtype(2) != 'a') throw new RuntimeException();
        for (; i < entries.length && entries(i).length()==0; i++) { }
        val nrow = Int.parse(entries(i++));
        for (; i < entries.length && entries(i).length()==0; i++) { }
        val ncol = Int.parse(entries(i++));
        assert (nrow == ncol);
        for (; i < entries.length && entries(i).length()==0; i++) { }
        val nnzero = Int.parse(entries(i++));
        for (; i < entries.length && entries(i).length()==0; i++) { }
        val neltvl = Int.parse(entries(i++));
        return Pair[Int,Int](nrow, nnzero);
    }

     static def readRailFromFile[T](f:ReaderIterator[String]!, n:Int, parse:(String)=>T) {
//		val r = Rail.make[T](n);
//    	var j:Int = 0;
//    	for(;j!=r.length();j++)
//    	{
//    		val line = f.next();
//    		val entries = line.split(" ");
//    		var i:Int = 0;
//    		
//    		for (; i < entries.length ; i++) { 
//    			if(entries(i).length()==0) continue;
//    			r(j)=parse(entries(i));
//    		}
//    	}
//		return ValRail.make[T](n, r);
    	// Inverting the flow of control in the above code!
    	
    	 val myRail:Rail[T]= Rail.make[T](n, parse(""));
         for (var i:Int=0; i < n; ) {
    	    val splits = f.next().split(" ");
    	    for (var j:Int=0; j < splits.length; j++) {
    		   if (splits(j).length() != 0) {
    			 myRail(i++) = parse(splits(j));
    		   }
    	     }
         }
         for (var i:Int=0; i < (n < 5? n: 5); ++i)
        	 Console.OUT.println("a(" + i+ ")=" + myRail(i));
         return myRail as ValRail[T];
    }

    static def test():Void {
    val ia:Rail[Int] = [1,5,9,12,14,17,22,24,29,33];
    val ja:Rail[Int] = [1,3,7,8,2,3,5,9,1,3,7,4,8,5,6,9,2,4,6,7,8,4,7,1,3,5,7,8,2,3,8,9];
    val n :Int = 9;
    val avals = [14.0,      -5.0,                  -1.0,-6.0,
                              14.0,-1.0,      -3.0,                  -1.0,
                        -1.0,      16.0,                  -2.0,
                                          14.0,                  -3.0,
                                                14.0,-1.0,            -1.0,
                              -2.0,      -1.0,      16.0,-2.0,-4.0,
                                          -1.0,            16.0,
                        -3.0,      -4.0,      -3.0,      -4.0,71.0,
                              -1.0,-2.0,                        -4.0,16.0];

   val b: Rail[Double] = [2.0,9.0,13.0,11.0,12.0,7.0,15.0,57.0,9.0];

	val ldb:Int=9;
    var t1:Double=0;
    var t2:Double=0;

    eqsolve(n,ia,ja,avals,b,ldb,0);


   Console.OUT.println("Total time is "+t2+"  The answer is: ");
   for((i) in 0..b.length-1){
	   Console.OUT.println(" "+b(i));
	 }
   }
    
   static def ptest():Void{	  
   val ia1 = [1,5,9,12];
   val ia2 = [1,3,6,11];
   val ia3 = [1,3,8,12];	
   
   val ja1 = [1,3,7,8,2,3,5,9,1,3,7];
   val ja2 = [4,8,5,6,9,2,4,6,7,8];
   val ja3 = [4,7,1,3,5,7,8,2,3,8,9];

   val np:Int = 3;
   val avals1 = [14.0,      -5.0,                  -1.0,-6.0,
                            14.0,-1.0,      -3.0,                  -1.0,
                      -1.0,      16.0,                  -2.0];
   val avals2 = [        14.0,                  -3.0,
                                       14.0,-1.0,            -1.0,
                           -2.0,      -1.0,      16.0,-2.0,-4.0];
   val avals3 = [                      -1.0,            16.0,
                      -3.0,      -4.0,      -3.0,      -4.0,71.0,
                             -1.0,-2.0,                        -4.0,16.0];
   var b:Rail[Double]=[2.0,9.0,13.0,11.0,12.0,7.0,15.0,57.0,9.0];
   val ldbp:Int=3;
   var t1:Double=0;
   var t2:Double=0;

   finish{
       ateach (p in Dist.makeUnique()){
               Console.OUT.println("here is "+here.id);
               if(here.id==0){
                       var b1:Rail[Double]=[2.0,9.0,13.0];
                       @Native("c++", "printf(\"%d %p\\n\", np, (ia1)->raw());") { }
                       peqsolve(np,ia1,ja1,avals1,b1,ldbp);
                        for((i) in 0..b1.length-1){
                                       Console.OUT.println(" "+b1(i));
                }
               }
               else if (here.id==1){
                        var b2:Rail[Double]=[11.0,12.0,7.0];
                       peqsolve(np,ia2,ja2,avals2,b2,ldbp);
                       for((i) in 0..b2.length-1){
                               Console.OUT.println(" "+b2(i));
                       }
               }else if (here.id==2){
                       var b3:Rail[Double]=[15.0,57.0,9.0];
                       peqsolve(np,ia3,ja3,avals3,b3,ldbp);
                       for((i) in 0..b3.length-1){
                               Console.OUT.println(" "+b3(i));
                       }
               }
       }
 }

   }
   static def mpitest():Void{	  
   
   finish{
       ateach (p in Dist.makeUnique()){
               Console.OUT.println("here is "+here.id);
               if(here.id==0){
                       var b1:Rail[Double]=[14.0,14.0,16.0];
                       mpiCTest(here.id);
                        for((i) in 0..b1.length-1){
                                       Console.OUT.println(" "+b1(i));
                }
               }
               else if (here.id==1){
                        var b2:Rail[Double]=[14.0,14.0,16.0];
               mpiCTest(here.id);
                       for((i) in 0..b2.length-1){
                               Console.OUT.println(" "+b2(i));
                       }
               }else if (here.id==2){
                       var b3:Rail[Double]=[15.0,57.0,9.0];
               mpiCTest(here.id);
                       for((i) in 0..b3.length-1){
                               Console.OUT.println(" "+b3(i));
                       }
               }
       }
 }
}
   public static def largeTest(fn:String):Void{
	   val timer = new Timer();
	try{
		val t_start = timer.milliTime();
	    val inA = openRBFile(fn);
	    readTitleFromFile(inA);
	    val counter = readNFromFile(inA);
	    val n=counter.first;
	    val nnz=counter.second; 
	    Console.OUT.println("n is "+n+"nnz is "+nnz);
	   
	    readLineFromFile(inA);
	    val t_ia= timer.milliTime();
	    val ia:ValRail[Int] = readRailFromFile[Int](inA, n+1, Int.parse.(String));
	    val t_ja= timer.milliTime();
	    val ja:ValRail[Int]= readRailFromFile[Int](inA, nnz, Int.parse.(String));
	    val t_avals= timer.milliTime();
	    val avals:ValRail[Double] = readRailFromFile[Double](inA, nnz, Double.parse.(String));
	    val t_readend= timer.milliTime();
	    val tt1=t_ja-t_ia;
	    val tt2 = t_avals-t_ja;
	    val tt3 = t_readend-t_avals;
	    Console.OUT.println("ia input takes " +tt1+ "ja input takes "+tt2+"avals input takes "+tt3); 
	    val b: Rail[Double] = Rail.make[Double](n,0);
	    // create b so that the solution is 1 for check.

	    for((i) in 0..nnz-1){
	    	b(ja(i)-1)+=avals(i);
		    }
	    val valB: ValRail[Double] = b ;
		var t1:Double=0;
		var t2:Double=0;
		val np = Place.MAX_PLACES;
		if( 0 == np){
			Console.OUT.println("Error, Number of place is : "+np);
			return;
		}
		val  rnpp :Int = n/np; //rowNumberPerPlace
        
	
		finish{
	    	ateach (p in Dist.makeUnique()){
	            
	            wsetmaxthrds(1);
		        val startR:Int = here.id*rnpp+1; //start from 1;
		        var endR:Int = here.id*rnpp+rnpp;
		         if(here.id==Place.MAX_PLACES-1&&n%np!=0){
		        //For last block, pay attention to uneven row number
		            endR = n;
	        	}
		        val localN = endR-startR+1;
		        val localK = ia(endR)-ia(startR-1);
		        val prevK = ia(startR-1)-1;
		        Console.OUT.println("localN at place "+ here.id + " is "+localN);
		        Console.OUT.println("localK at place "+ here.id + " is "+localK);
		        Console.OUT.println("prevK at place "+ here.id + " is "+prevK);
		        var t_copy:Long = -timer.milliTime();
		        val localIA = ValRail.make[Int](localN+1,(i:int)=>(ia(i+here.id*rnpp)-prevK));
		        val localJA = ValRail.make[Int](localK,(i:int)=>ja(prevK+i));
		        val localAVALS= ValRail.make[Double](localK,(i:int)=>avals(prevK+i));
		        val localB = Rail.make[Double](localN,(i:int)=>valB(i+here.id*rnpp));
		        t_copy += timer.milliTime();
	        
		       //   for((i) in 0..localIA.length-1){
		       //       Console.OUT.print(" "+localIA(i));
		       //   }
		     	 // Console.OUT.println();
		       //   for((i) in 0..localJA.length-1){
		       //       Console.OUT.print(" "+localJA(i));
		       //   }
		       //   Console.OUT.println();
		       //   for((i) in 0..localAVALS.length-1){
		       //       Console.OUT.print(" "+localAVALS(i));
		       //   }
		       //   Console.OUT.println();
		       //   for((i) in 0..localB.length-1){
		       //       Console.OUT.print(" "+localB(i));
		       //   }   
		        var t_compute:Long = -timer.milliTime();
		        peqsolve_csc(localN,localIA,localJA,localAVALS,localB,localN);
		        t_compute += timer.milliTime();
		        
		        val transB:ValRail[Double] = localB;
		        Console.OUT.println("Place "+here.id+": copy time is "+t_copy+"  Computation time is: "+t_compute);
		        at(Place.FIRST_PLACE) {
		     	   for ((i) in 0 .. localN-1)b(i+startR-1)=transB(i);
		    	}
			}
		}
	
	
		//Console.OUT.println("Total time is "+t2+"  The answer is: ");
		for((i) in 0..(n < 10? n-1: 10)){
			Console.OUT.println(" "+b(i));
		}
        var ecount:Int = 0;
        for((i) in 0..b.length()-1){
            if(b(i)-1.0>0.05||b(i)-1.0<-0.05){
            	ecount++;
            }
        }
        Console.ERR.println(" Result Error number: "+ecount);
        val t_total=timer.milliTime()-t_start;
        Console.OUT.println("Total time is "+t_total);
	}catch(e:IOException){
		Console.OUT.println(e);
	}
   
}
	public def copytest(){
	   // input data
   val ia = [1,5,9,12,14,17,22,24,29,33];
   val ja = [1,3,7,8, 2,3,5,9, 1,3,7, 4,8, 5,6,9, 2,4,6,7,8, 4,7, 1,3,5,7,8, 2,3,8,9];
   val n :Int = 9;
   val avals = [14.0,      -5.0,                  -1.0,-6.0,
                             14.0,-1.0,      -3.0,                  -1.0,
                       -1.0,      16.0,                  -2.0,
                                         14.0,                  -3.0,
                                               14.0,-1.0,            -1.0,
                             -2.0,      -1.0,      16.0,-2.0,-4.0,
                                         -1.0,            16.0,
                       -3.0,      -4.0,      -3.0,      -4.0,71.0,
                             -1.0,-2.0,                        -4.0,16.0];

  val b: Rail[Double] = [2.0,9.0,13.0,11.0,12.0,7.0,15.0,57.0,9.0];
   val valB: ValRail[Double] = b ;

   val ldb:Int=9;
   var t1:Double=0;
   var t2:Double=0;
   val np = Place.MAX_PLACES;
   if( 0 == np){
       Console.OUT.println("Error, Number of place is : "+np);
       return;
   }
   val  rnpp :Int = n/np; //rowNumberPerPlace
   
   finish{
       ateach (p in Dist.makeUnique()){
           val startR:Int = here.id*rnpp+1; //start from 1;
           var endR:Int = here.id*rnpp+rnpp;
            if(here.id==Place.MAX_PLACES-1&&n%np!=0){
           //For last block, pay attention to uneven row number
               endR = n;
           }
           val localN = endR-startR+1;
           val localK = ia(endR)-ia(startR-1);
           val prevK = ia(startR-1)-1;
           Console.OUT.println("localN at place "+ here.id + " is "+localN);
           Console.OUT.println("localK at place "+ here.id + " is "+localK);
           Console.OUT.println("prevK at place "+ here.id + " is "+prevK);
           val localIA = ValRail.make[Int](localN+1,(i:int)=>(ia(i+here.id*rnpp)-prevK));
           val localJA = ValRail.make[Int](localK,(i:int)=>ja(prevK+i));
           val localAVALS= ValRail.make[Double](localK,(i:int)=>avals(prevK+i));
           val localB = Rail.make[Double](localN,(i:int)=>valB(i+here.id*rnpp));
           
           
           for((i) in 0..localIA.length-1){
               Console.OUT.print(" "+localIA(i));
           }
        Console.OUT.println();
           for((i) in 0..localJA.length-1){
               Console.OUT.print(" "+localJA(i));
           }
           Console.OUT.println();
           for((i) in 0..localAVALS.length-1){
               Console.OUT.print(" "+localAVALS(i));
           }
           Console.OUT.println();
           for((i) in 0..localB.length-1){
               Console.OUT.print(" "+localB(i));
           }   
           
           peqsolve(localN,localIA,localJA,localAVALS,localB,localN);
           
           val transB:ValRail[Double] = localB;
           at(Place.FIRST_PLACE) {
        	   for ((i) in 0 .. localN-1)b(i+startR-1)=transB(i);
       }
   }
   }


  //Console.OUT.println("Total time is "+t2+"  The answer is: ");
  for((i) in 0..b.length-1){
	   Console.OUT.println(" "+b(i));
	 }
   }
   public def copytest2(n:Int){
	   // input data

	  // val n :Int = 1000;
	   val ia = ValRail.make[Int](n+1, (i:Int)=>i+1);
	   val ja =  ValRail.make[Int](n,  (i:Int)=>i+1);
	   val avals =  ValRail.make[Double](n,(i:Int)=>(i+1)*1.0);
	   val b: Rail[Double] = Rail.make[Double](n,(i:Int)=>1.0*(i+1));
   val valB: ValRail[Double] = b ;

   val ldb:Int=n;
   var t1:Double=0;
   var t2:Double=0;
   val np = Place.MAX_PLACES;
   if( 0 == np){
       Console.OUT.println("Error, Number of place is : "+np);
       return;
   }
   val  rnpp :Int = n/np; //rowNumberPerPlace
   
   finish{
       ateach (p in Dist.makeUnique()){
           val startR:Int = here.id*rnpp+1; //start from 1;
           var endR:Int = here.id*rnpp+rnpp;
            if(here.id==Place.MAX_PLACES-1&&n%np!=0){
           //For last block, pay attention to uneven row number
               endR = n;
           }
           val localN = endR-startR+1;
           val localK = ia(endR)-ia(startR-1);
           val prevK = ia(startR-1)-1;
           Console.OUT.println("localN at place "+ here.id + " is "+localN);
           Console.OUT.println("localK at place "+ here.id + " is "+localK);
           Console.OUT.println("prevK at place "+ here.id + " is "+prevK);
           val localIA = ValRail.make[Int](localN+1,(i:int)=>(ia(i+here.id*rnpp)-prevK));
           val localJA = ValRail.make[Int](localK,(i:int)=>ja(prevK+i));
           val localAVALS= ValRail.make[Double](localK,(i:int)=>avals(prevK+i));
           val localB = Rail.make[Double](localN,(i:int)=>valB(i+here.id*rnpp));
           
           
 /*          for((i) in 0..localIA.length-1){
               Console.OUT.print(" "+localIA(i));
           }
        Console.OUT.println();
           for((i) in 0..localJA.length-1){
               Console.OUT.print(" "+localJA(i));
           }
           Console.OUT.println();
           for((i) in 0..localAVALS.length-1){
               Console.OUT.print(" "+localAVALS(i));
           }
           Console.OUT.println();
           for((i) in 0..localB.length-1){
               Console.OUT.print(" "+localB(i));
           }   
 */          
           peqsolve(localN,localIA,localJA,localAVALS,localB,localN);
           
           val transB:ValRail[Double] = localB;
           at(Place.FIRST_PLACE) {
        	   for ((i) in 0 .. localN-1)b(i+startR-1)=transB(i);
       }
   }
   }


  //Console.OUT.println("Total time is "+t2+"  The answer is: ");
  for((i) in 0..b.length-1){
	 //  Console.OUT.print(" "+b(i));   
	if (b(i)-1.0>0.05||b(i)-1.0<-0.05){Console.OUT.println("Result Error!"); return;}
	 
	 }
   }
	public static def main(args:Rail[String]!) {
        	 if (args.length > 0) {
         	  Console.OUT.println("The first arg is: "+args(0));
         	}
         Console.OUT.println("Hello X10 world");
         val h = new EQSolver();  // final variable
         //   h.test();
         //h.largeTest();
         //h.ptest();
         //h.mpitest();
        //h.copytest2(Int.parse(args(0)));
         h.largeTest(args(0));

}
}
