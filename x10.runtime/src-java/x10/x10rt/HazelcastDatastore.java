package x10.x10rt;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class HazelcastDatastore {
	private final HazelcastInstance hazelcast; // handle on hazelcast
	private final String publicAddress; // the ip:port that can be used to connect to the local hazelcast
	
	// if leader argument is null, then we are the leader
	HazelcastDatastore(String leader) {
		String launcherProvidedHostname = System.getenv("X10_LAUNCHER_HOST");

		Config config = new Config();
		config.setProperty("hazelcast.logging.type", "none"); // disables Hazelcast logging
		NetworkConfig netconfig = config.getNetworkConfig();
		if (launcherProvidedHostname != null) { // override the network interfaces used to match the hostfile/hostlist
			try {
				String IP = InetAddress.getByName(launcherProvidedHostname).getHostAddress();
				netconfig = netconfig.setInterfaces(new InterfacesConfig().addInterface(IP).setEnabled(true));
			} catch (UnknownHostException e) {
				// InetAddress.getByName() failed.  address not usable.  Let hazelcast pick one instead
			}
		}
		JoinConfig join = netconfig.setPortAutoIncrement(true).getJoin();
		join.getMulticastConfig().setEnabled(false);
		join.getTcpIpConfig().setEnabled(true).setRequiredMember(leader);
		config.addMapConfig(new MapConfig("FinishResilientHC").setInMemoryFormat(InMemoryFormat.OBJECT));
		hazelcast = Hazelcast.newHazelcastInstance(config);
		InetSocketAddress addr = (InetSocketAddress) hazelcast.getLocalEndpoint().getSocketAddress();
		publicAddress = addr.getAddress().getHostAddress()+':'+addr.getPort();
	}
	
	String getConnectionInfo() {
		return publicAddress;
	}
	
	@SuppressWarnings("rawtypes")
	IMap getResilientMap(String name) {
		return hazelcast.getMap(name);
	}
	
	int getContainerCount() {
		return hazelcast.getCluster().getMembers().size();
	}
	
	void shutdown() {
		try {
			hazelcast.shutdown();
		} catch (Exception e) {
			// be quiet.
		}
	}

	HazelcastInstance getHazelcastInstance() {
		return hazelcast;
	}
}
