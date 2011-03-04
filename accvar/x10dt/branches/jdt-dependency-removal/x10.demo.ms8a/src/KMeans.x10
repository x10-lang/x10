import x10.io.Console;
import x10.io.File;
import x10.io.Marshal;
import x10.io.IOException;
import x10.util.Random;
import x10.array.FastArray;

public class KMeans {
    public static const DEFAULT_NUM_DIMS = 3;
    public static const DEFAULT_NUM_CLUSTERS = 8;
    public static const DEFAULT_NUM_POINTS = 100;
    public static const DEFAULT_NUM_ITERATIONS = 50;

    public static val CONVERGENCE_THRESHOLD = 0.0001;

    public var NUM_DIMS_: Int;
    public var NUM_CLUSTERS_: Int;
    public var NUM_POINTS_: Int;
    public var NUM_ITERATIONS_: Int;

    public static def main (args : Rail[String]) {
        val k: KMeans = new KMeans();
        k.getArgs(args);
        k.dumpArgs();
        k.run();
    }

    private def run() {
        val NUM_DIMS = NUM_DIMS_;
        val NUM_CLUSTERS = NUM_CLUSTERS_;
        val NUM_POINTS = NUM_POINTS_;
        val NUM_ITERATIONS = NUM_ITERATIONS_;

        val dimRegion = 0..NUM_DIMS-1;
        val ptRegion = 0..NUM_POINTS-1;
        val clustRegion = 0..NUM_CLUSTERS-1;
        val ptDimRegion = (ptRegion * dimRegion) as Region(2);
        val clustDimRegion = (clustRegion * dimRegion) as Region(2);

        val points: Array[Float]{rank==2} = getPoints(ptDimRegion);
        val clusters = Array.make[Float](clustDimRegion, (p:Point) => points(p as Point(2)));
        val classification = Rail.makeVar[Int](NUM_POINTS, (Int) => 0);

        // used to detect convergence
        val new_clusters = Array.make[Float](clustDimRegion, (p:Point) => clusters(p as Point(2)));
        val cluster_counts = Rail.makeVar[Int](NUM_CLUSTERS, (Int) => 0);

        for ((iter):Point in 0..NUM_ITERATIONS-1) {
            classify(points, clusters, classification);
            reaverage(points, new_clusters, classification, cluster_counts);

            if (converged(new_clusters, clusters)) {
                break;
            }
        }
        printPoints(clusters, "clusters");
    }

    static type PointArray = Array[Float]{rank==2};

    private def classify(points: PointArray, clusters: PointArray, classification: Rail[Int]) {
        val ptDimRegion = points.region;
        val clustDimRegion = clusters.region;
        val ptRegion = ptDimRegion.projection(0) as Region(1){self.rect};
        val dimRegion = ptDimRegion.projection(1) as Region(1){self.rect};
        val clustRegion = clustDimRegion.projection(0) as Region(1){self.rect};

        for ((p) in ptRegion) {
            var closest:Int = -1;
            var closest_dist:Float = Float.MAX_VALUE;

            for ((k) in clustRegion) {
                var dist : Float = 0;
// <<< @unroll(4)
                for ((d):Point in dimRegion) {
                    val tmp = points(p,d) - clusters(k,d);

                    dist += tmp * tmp;
                }
// >>>
                // record closest cluster seen so far
                if (dist < closest_dist) {
                    closest_dist = dist;
                    closest = k;
                }
            }
            classification(p) = closest;
        }
    }

    private def reaverage(points: PointArray, new_clusters: PointArray, classification: Rail[Int], cluster_counts: Rail[Int]) {
        val ptDimRegion = points.region;
        val ptRegion = ptDimRegion.projection(0) as Region(1);
        val dimRegion = ptDimRegion.projection(1) as Region(1);

        finish {
            for ((p):Point in ptRegion) {
                val closest = classification(p);
                for ((d):Point in dimRegion) {
                    new_clusters(closest,d) += points(p,d);
                }
                atomic { cluster_counts(closest)++; }
            }
        }
    }

    private def converged(newClusters: PointArray, clusters: PointArray): Boolean = {
        val clustDimRegion = clusters.region;
        val clustRegion = clustDimRegion.projection(0) as Region(1);
        val dimRegion = clustDimRegion.projection(1) as Region(1);

        for ((c):Point in clustRegion) {
            for((d):Point in dimRegion) {
                if (Math.abs(newClusters(c, d) - clusters(c, d)) > CONVERGENCE_THRESHOLD) {
                    return false;
                }
            }
        }
        return true;
    }

    private def getArgs(args : Rail[String]) {
        NUM_DIMS_ = DEFAULT_NUM_DIMS;
        NUM_CLUSTERS_ = DEFAULT_NUM_CLUSTERS;
        NUM_POINTS_ = DEFAULT_NUM_POINTS;
        NUM_ITERATIONS_ = DEFAULT_NUM_ITERATIONS;

        switch (args.length) {
            case 4: NUM_ITERATIONS_ = Int.parseInt(args(3));
            case 3: NUM_DIMS_       = Int.parseInt(args(2));
            case 2: NUM_CLUSTERS_   = Int.parseInt(args(1));
            case 1: NUM_POINTS_     = Int.parseInt(args(0));
        }
    }

    private def dumpArgs() {
        Console.OUT.println("# points:   " + NUM_POINTS_);
        Console.OUT.println("# dims:     " + NUM_DIMS_);
        Console.OUT.println("# clusters: " + NUM_CLUSTERS_);
        Console.OUT.println("# iters:    " + NUM_ITERATIONS_);
    }

    private def printPoints(points: Array[Float]{rank==2}, label: String) {
        val r0 = points.region.projection(0) as Region(1);
        val rDim = points.region.projection(1) as Region(1);
        for((p) in r0) {
            Console.OUT.print(label + "(" + p + ") = [ ");
            for ((d) in rDim) {
                if (d > 0) {
                    Console.OUT.print(", ");
                }
                Console.OUT.printf("%.4f", points(p, d));
            }
            Console.OUT.println(" ]");
        }
    }

    private def getPoints(r: Region) : Array[Float](r) = {
        val ran: Random = new Random();
        val init_points = (Point) => {
            return ran.nextFloat();
        };
        val points = Array.make[Float](r, init_points);
        return points;
    }
}
