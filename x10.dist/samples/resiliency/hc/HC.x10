import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.Map; 
import java.util.Queue;
import x10.util.concurrent.Latch;

public class HC {

    public static def main(args: Rail[String]) {
        Console.OUT.println("Starting X10-HC");
        for (p in Place.places()) async at(p) {
		Console.OUT.println("Starting X10-HC at place " + p);
		val config = new Config();
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
		val instance = Hazelcast.newHazelcastInstance(config); 
		val mapCustomers = instance.getMap("customers"); mapCustomers.put(1, "Joe");
		mapCustomers.put(2, "Ali");
		mapCustomers.put(3, "Avi");
		Console.OUT.println("Customer with key 1: "+ mapCustomers.get(1)); 
		Console.OUT.println("Map Size:" + mapCustomers.size());
		val queueCustomers = instance.getQueue("customers");
		queueCustomers.add("Tom");
		queueCustomers.add("Mary");
		queueCustomers.add("Jane");
		Console.OUT.println("First customer: " + queueCustomers.poll());
		Console.OUT.println("Second customer: "+ queueCustomers.peek());
		Console.OUT.println("Queue size: " + queueCustomers.size());
	    }
	val latch = new Latch();
        latch.await();
    } 
}
