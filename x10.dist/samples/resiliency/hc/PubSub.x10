import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.*;
import x10.util.concurrent.Latch;

public class PubSub {

    static def hcInstance() {
	val config = new Config();
	config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
	config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
	config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
	val instance = Hazelcast.newHazelcastInstance(config); 
	return instance;
    }
    public static def main (args : Rail[String]) {

	val hcInstance = hcInstance();
	val topic = hcInstance.getTopic("default");
        finish for (p in Place.places()) if (p != here) at (p) async {
	    val myInstance = hcInstance();
            myInstance.getTopic("default").addMessageListener(new MessageListener() {
                    public def onMessage(msg:Message):void {
			Console.OUT.println("Got msg: " + msg + " with contents " + msg.getMessageObject() + " at place " + here);
		    }
		});
	}
	for (i in 1..10) 
	topic.publish(here + ":(" + i + ") Hello world! ");
        new Latch().await();
    }

}

