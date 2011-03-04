import clocked.*;
import x10.util.Random;


public class KMeansScalar {
    
    static val DIM=2,  K=4, POINTS=1048576, ITERATIONS=50, THREADS=64;
  
   static def  add (r1: Rail[int]!, r2: Rail[int]!): Rail[int] {
       var r3 : Rail[int]! = Rail.make[int](DIM);
       var i: Int;
        for (i = 0 ;  i <= DIM-1; i++)
                r3(i) =  r1(i) + r2(i);
        return r3;

  } 
 

    def dist(vec1: Double, vec2: Double ) {
         
                val tmp = vec1-vec2;
                val dist = tmp*tmp;
         
            	return dist;
   }
     


    def computeMeans() {
      finish {
      val c = Clock.make();
       val rnd = new Random(0);
       val iop = Int.+;
       val fop = Double.+;
	val slice = POINTS/THREADS;
       val redCluster =
          Rail.make[Double  @ Clocked[int] (c, fop, 0.0) ](K, (Int)=>rnd.nextDouble());
       val count =
            Rail.make[int @ Clocked[int](c, iop, 0) ] (K, (Int) => 0) ; 
          
       val points = Rail.make[Double](POINTS, (Int)=>rnd.nextDouble()); 
      
    for ((i) in 1..ITERATIONS) {
          
            
            for ((p) in 0..THREADS-1) { 
               async (this) clocked(c) { 
		  for ((l) in ((p*slice)..((p+1)*slice -1))) {
                  var closest:Int = -1;
                   var closestDist:Double = Double.MAX_VALUE;
                   val point = points(l);
                   for ((k) in 0..K-1) { // compute closest mean in cluster.
                       val distance =this.dist(point, redCluster(k));
                       if (distance < closestDist) {
                          closestDist = distance;
                          closest = k;
                       } // end if
                   } // end for
                        
                       redCluster(closest) = point;
                       count(closest) = 1;

		   } //end l
                 } // end async
               } // end for 
            next;  // Main activity waits for all the activities to finish
            
            for ((k) in 0..K-1) {
            	  redCluster(k) = redCluster(k)/count(k);
            			
            }
            next;
 
        
        } // end for
         
       for ((k) in 0..K-1) Console.OUT.println(redCluster(k));
      } 
    }
  
    public static def main (args : Rail[String]) {
     	val start_time = System.currentTimeMillis(); 
      	val k = new KMeansScalar();
        k.computeMeans();
    	val compute_time = (System.currentTimeMillis() - start_time);
    	Console.OUT.println( compute_time + " ");
       
       
    }
}

