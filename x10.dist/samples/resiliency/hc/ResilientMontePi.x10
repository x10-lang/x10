import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.Map; 
import java.util.Queue;
import x10.io.Console;
import x10.util.*;
import x10.compiler.*;

public class ResilientMontePi {

    static def hcInstance() {
	val config = new Config();
	config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
	config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
	config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
	val instance = Hazelcast.newHazelcastInstance(config); 
	return instance;
    }
    static val ITERS = 1000000000l / (Place.MAX_PLACES-1);

    public static def main (args : Rail[String]) {

        val victim = args.size > 0 ? Long.parse(args(0)) : -1;

        val result = GlobalRef(new Cell(Pair[Long,Long](0l, 0l))); // (points_in_circle, points_tested)
	val hcInstance = hcInstance();
	val resultsMap = hcInstance.getMap("result");
        finish for (p in Place.places()) if (p != here) async {
            try {
                at (p) {
		    val myInstance = hcInstance();
		    val myResultsMap = myInstance.getMap("result");
                    val rand = new Random(System.nanoTime());
                    var total : Long = 0l;
                    for (iter in 1..ITERS) {
                        val x = rand.nextDouble();
                        val y = rand.nextDouble();
                        if (x*x + y*y <= 1.0) total++;
			if (iter % 1000 == 0) {
                            val id = here.id;
			    myResultsMap.put(id, total + " " + iter);
                            if (here.id == victim && iter > ITERS/2) {
                                Console.OUT.println("BOOM: at "+here+" on iteration "+iter);
                                System.killHere();
                            }
			}
                    }
                    val total_ = total;
                    Console.OUT.println("Work done at: "+here);
                    at (result) atomic {
                        result()(Pair(result()().first+total_, result()().second+ITERS));
                    }
                }
            } catch (e:DeadPlaceException) {
                Console.OUT.println("Got DeadPlaceException from "+e.place);
		val v = resultsMap.get(e.place.id) as String;
                Console.OUT.println("Retrieved from "+e.place + " "  + result);
                val split = v.indexOf(' ');
                val t = Long.parseLong(v.substring(0N,split));
                val r = Long.parseLong(v.substring(split+1n));
		atomic {
		    result()(Pair(t as Long,r as Long));
		}
            }
        }

        val pi = (4.0 * result()().first) / result()().second;
        Console.OUT.println("pi = "+pi+"   calculated with "+result()().second+" samples.");

    }

}

// vim: shiftwidth=4:tabstop=4:expandtab

