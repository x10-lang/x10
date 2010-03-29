import clocked.*;
import x10.util.Random;


public class KMeans {
    
    static val DIM=2,  K=4, POINTS=2000, ITERATIONS=50;
  
   static def  add (r1: Rail[int]!, r2: Rail[int]!): Rail[int] {
       var r3 : Rail[int]! = Rail.make[int](DIM);
       var i: Int;
        for (i = 0 ;  i <= DIM-1; i++)
                r3(i) =  r1(i) + r2(i);
        return r3;

  } 
 

    def dist(vec1: Rail[Double]!, vec2: Rail[Double]! ) {
            var dist:Double=0.0F;
            var i: Int;
            for (i = 0; i <= DIM-1; i++) {
                val tmp = vec1(i)-vec2(i);
                dist += tmp*tmp;
            }
            return dist;
   }
     
    def print(vec: Rail[Double]!) {
          var i: Int;
            for (i = 0; i <= DIM-1; i++) {
                Console.OUT.print((i>0? " " : "") + vec(i));
   			}
   }

    def computeMeans() {
      val c = Clock.make();
       val rnd = new Random(0);
       val iop = Int.+;
       val fop = Double.+;
       val redCluster : Rail[Rail[Double @ Clocked[Int] (c, fop)]]! =
            Rail.make[Rail[Double]](K);
       
       for ((i) in 0..K-1)
       		redCluster(i) = Rail.make[Double](DIM, (Int)=>rnd.nextDouble());
       val count : Rail [int@Clocked[Int] (c, iop)]! =
            Rail.make[int] (K, (Int) => 0) ; 
          
       val points = Rail.make[Rail[Double]](POINTS, 
                       (Int)=>Rail.make[Double](DIM, (Int)=>rnd.nextDouble())); 
      
    for ((i) in 1..ITERATIONS) {
          
            
            for ((p) in 0..POINTS-1) { 
               async (this) clocked(c) { 
                  var closest:Int = -1;
               
                   var closestDist:Double = Double.MAX_VALUE;
                   val point = points(p);
                   for ((k) in 0..K-1) { // compute closest mean in cluster.
                       val distance = 0; // this.dist(point, redCluster(k));
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
            		
                     // redCluster(k) = Rail.make[Double](DIM, (i:int) => (redClusterK(i)/count(k)));
                     var j: Int;
            			for (j = 0; j <= DIM-1; j++) {
            			   val redClusterK = redCluster(k) as Rail[Double]!;
            			   redClusterK(i) = redClusterK(j)/count(k);
            			}
                      }
           

            next;
 
        
        } // end for
         
       //for ((k) in 0..K-1) this.print(redCluster(k));
       
    }
  
    public static def main (args : Rail[String]) {
      
      	val k = new KMeans();
        k.computeMeans();
       
       
    }
}

