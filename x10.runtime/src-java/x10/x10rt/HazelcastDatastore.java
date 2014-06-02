package x10.x10rt;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/*
 * Much to do here.  Currently, this only works if all places are on the same machine, 
 * and only if there is a single X10 computation running.
 * 
 * TODO: control how hazelcast finds other hazelcast instances.  This will enable 
 * running across multiple machines, and multiple independent X10 programs running
 * at the same time on the same machines without interfering with each other.
 */
public class HazelcastDatastore {
	private final HazelcastInstance hazelcast;
	
	// if leader argument is null, then we are the leader
	HazelcastDatastore(String leader) {
		// TODO: adjust config based on leader.  For now, localhost only
		Config config = new Config();
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
		hazelcast = Hazelcast.newHazelcastInstance(config);
	}
	
	String getConnectionInfo() {
		return hazelcast.getConfig().getNetworkConfig().getPublicAddress();
	}
	
	@SuppressWarnings("rawtypes")
	IMap getResilientMap(String name) {
		return hazelcast.getMap(name);
	}
}
