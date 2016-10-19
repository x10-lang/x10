package x10.x10rt;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
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
	
	static int numHazelcastInstances() {
	    int numInstances = X10RT.numPlaces();
	    String envInstances = System.getenv("X10_NUM_HAZELCAST_INSTANCES");
	    if (envInstances != null) {
	        try {
	            int envNum = Integer.parseInt(envInstances);
	            if (envNum > 0 && envNum < numInstances) {
	                numInstances = envNum;
	            }
	        } catch (NumberFormatException e) {
	            System.err.println("Malformed value '"+envInstances+"' for X10_NUM_HAZELCAST_INSTANCES");
	        }
	    }
	    return numInstances;
	}

	// if leader argument is null, then we are the leader
	HazelcastDatastore(String leader) {
		String launcherProvidedHostname = System.getenv("X10_LAUNCHER_HOST");
		boolean clientOnly = X10RT.hereId() >= numHazelcastInstances();

        if (clientOnly) {
            ClientConfig config = new ClientConfig();
            if (leader != null) {
                List<String> leaders = new ArrayList<String>(1);
                leaders.add(leader);
                ClientNetworkConfig netconfig = config.getNetworkConfig();
                netconfig.setAddresses(leaders);
            }
            config.setProperty("hazelcast.socket.connect.timeout.seconds", "1");
            config.setProperty("hazelcast.connection.monitor.max.faults", "0");
            config.setProperty("hazelcast.operation.backup.timeout.millis", "100");
            config.setProperty("hazelcast.logging.type", "none"); // disables Hazelcast logging (but not enough until Hazelcast 3.7)
            System.setProperty("hazelcast.logging.type", "none"); // workaround Hazelcast limitation that may be fixed in 3.7
            hazelcast = HazelcastClient.newHazelcastClient(config);
        } else {
            Config config = new Config();
            config.setProperty("hazelcast.socket.connect.timeout.seconds", "1");
            config.setProperty("hazelcast.connection.monitor.max.faults", "0");
            config.setProperty("hazelcast.operation.backup.timeout.millis", "100");
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
        }

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
