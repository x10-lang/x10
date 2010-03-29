import clocked.*;
import x10.util.Random;


public class KMeansScalar {
    
    static val DIM=2,  K=4, POINTS=2000, ITERATIONS=50;
  
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
      val c = Clock.make();
       val rnd = new Random(0);
       val iop = Int.+;
       val fop = Double.+;
       val redCluster : Rail[Double @ Clocked[int] (c, fop)]!  = 
          Rail.make[Double](DIM, (Int)=>rnd.nextDouble());
       val count: Rail [Int @ Clocked [int] (c, iop)]! =
            Rail.make[int] (K, (Int) => 0) ; 
          
       val points =Rail.make[Double](DIM, (Int)=>rnd.nextDouble()); 
      
    for ((i) in 1..ITERATIONS) {
          
            
            for ((p) in 0..POINTS-1) { 
               async (this) clocked(c) { 
                  var closest:Int = -1;
               
                   var closestDist:Double = Double.MAX_VALUE;
                   val point = points(p);
                   for ((k) in 0..K-1) { // compute closest mean in cluster.
                       val distance =this.dist(point, redCluster(k));
                       if (distance < closestDist) {
                          closestDist = distance;
                          closest = k;
                       } // end if
                   } // end for
                        
                       redCluster(closest) = point;
                       count(closest) = 1;
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
  
    public static def main (args : Rail[String]) {
      
      	val k = new KMeansScalar();
        k.computeMeans();
       
       
    }
}

