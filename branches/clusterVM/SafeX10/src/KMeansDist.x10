import x10.io.Console;
import x10.util.Random;
import x10.io.Console;
import x10.util.Random;

public class KMeansDist {

    const DIM=2, CLUSTERS=4, POINTS=2000, ITERATIONS=50;
    const EPS=0.01F;
    
    static type ValVec(k:Int) = ValRail[Float](k);
    static type ValVec = ValVec(DIM);
    
    static type Vec(k:Int) = Vector{self.k==k};
    static type Vec = Vec(DIM);
    static class Vector(k:Int) implements (Int(k))=>Float {
    	static type MyVec(k:Int) = Rail[Float](k);
    	def this(k:Int, init:(Int)=>Float): Vec(k) {
    	   property(k);
    	   vec = Rail.makeVar[Float](this.k, init);
    	   count = 0;
    	}
    	public def apply(i:Int(k)) = vec(i);
    	def makeZero() {
            for ((i) in 0..k-1) 
          	vec(i) =0.0F;	
            count=0;
    	}
    	def addIn(a:ValVec(k)) {
            for ((i) in 0..k-1) 
    		vec(i) += a(i);
            count++;
    	}
    	def div(f:Int) {
            for ((i) in 0..k-1)
    		vec(i) /= f;
    	}
    	def dist(a:ValVec(k)):Float {
    	    var dist:Float=0.0F;
    	    for ((i) in 0..k-1) {
    		val tmp = vec(i)-a(i);
    		dist += tmp*tmp;
    	    }
            return dist;	
    	}
    	def dist(a:Vec(k)):Float {
    		 var dist:Float=0.0F;
	    for ((i) in 0..k-1) {
		val tmp = vec(i)-a(i);
		dist += tmp*tmp;
	    }
        return dist;	
    	}
    	def print() {
    		Console.OUT.println();
    		for ((i) in 0..k-1) {
    			Console.OUT.print((i>0? " " : "") + vec(i));
    		}
    	}
    	def  normalize() { div(count);};
    	var vec: MyVec(k);
    	var count:Int;
    }
  
    public static def main (args : Rail[String]) {
        val rnd = new Random(0);
        val P = Place.MAX_PLACES;
        val points = Array.makeVal[ValRail[ValVec]](Dist.makeUnique(), 
        		Rail.makeVal[ValVec](POINTS/P, 
                		(Int)=>Rail.makeVal[Float](DIM, (Int)=>rnd.nextFloat())));
        		
        // annoying bug... specifying static type KMeans = Rail[Vector(DIM)](CLUSTERS), and 
        // declaring clusters:KMeans does not work :-(.
        		
        var redCluster : Rail[Vec(DIM)](CLUSTERS) = 
        	Rail.makeVar[Vec(DIM)](CLUSTERS, (i:Int)=> new Vector(DIM, (j:Int)=>points(i)(j)));
        var blackCluster : Rail[Vec(DIM)](CLUSTERS) = 
        	Rail.makeVar[Vec(DIM)](CLUSTERS, (i:Int)=> new Vector(DIM, (j:Int)=>0.0F));


        for (i in 1..ITERATIONS) {
        	val tmp = redCluster;
        	redCluster = blackCluster;
        	blackCluster=tmp;
        	for ((p) in 0..POINTS-1) { 
        		var closest:Int = -1;
        		var closestDist:Float = Float.MAX_VALUE;
        	        val point = points(p);
        		for ((k) in 0..CLUSTERS-1) { // compute closest mean in cluster.
        			val dist = blackCluster(k).dist(point);
        			if (dist < closestDist) {
        				closestDist = dist;
        				closest = k;
        			}
        		}
        		redCluster(closest).addIn(point);
        	}
        	
        	for ((k) in 0..CLUSTERS-1) 
        		redCluster(k).normalize(); 
        	
        	var b:Boolean = true;
        	 for ((k) in 0..CLUSTERS-1) {
        		 if (redCluster(k).dist(blackCluster(k)) > EPS) {
        			 b=false;
        			 break;
        		 }
        	 }
        	if (b) {
        		Console.OUT.println("Breaking at iteration " + i);
        		break;
        	}
        		
        	for ((k) in 0..CLUSTERS-1) 
        		blackCluster(k).makeZero(); 	
        }

        for ((k) in 0..CLUSTERS-1) redCluster(k).print();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

// vim: shiftwidth=4:tabstop=4:expandtab
