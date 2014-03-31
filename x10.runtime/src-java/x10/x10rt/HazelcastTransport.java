package x10.x10rt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import x10.x10rt.SocketTransport.RETURNCODE;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class HazelcastTransport {
	
	private static final String X10RT = "X10RT";
	private static final String X10RTCLOSURE = "X10RTCLOSURE";
	private static final String X10RTASYNC = "X10RTASYNC";
	private int myPlaceId;
	private HazelcastInstance hazelcast;
	private Communicator closureCommunicator;
	private Communicator asyncCommunicator;
	private int initialNumPlaces = 0; // when the launcher is used, we expect some known number of places before running
	private static final boolean DEBUG = false;

	public HazelcastTransport() {
		Config config = new Config();
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
		hazelcast = Hazelcast.newHazelcastInstance(config);

		// Ask Hazelcast for a place ID
		IAtomicLong placeGen = hazelcast.getAtomicLong(X10RT);
		myPlaceId = (int) placeGen.getAndIncrement();
		if (DEBUG) System.err.println("Hazelcast says that I am place "+myPlaceId);

		// If the X10 launcher started us, replace the hazelcast place ID with the launcher ID
		// this prevents confusing log messages
		String placeFlag = System.getenv(SocketTransport.X10_LAUNCHER_PLACE);
		if (placeFlag != null) {
			myPlaceId = Integer.parseInt(placeFlag);
			if (DEBUG) System.err.println("changed place ID to "+myPlaceId);
			initialNumPlaces = Integer.parseInt(System.getenv(SocketTransport.X10_NPLACES));
		}
		
		// establish link to other places
		closureCommunicator = new Communicator(X10RTCLOSURE+'_'+Integer.toString(myPlaceId));
		asyncCommunicator = new Communicator(X10RTASYNC+'_'+Integer.toString(myPlaceId));
		
		// wait here until all expected places have joined
		while (placeGen.get() < initialNumPlaces) {
			// wait at startup time for all expected places to join here
			try {Thread.sleep(10);} 
			catch (InterruptedException e){}
		}
	}
	
	public int x10rt_nplaces() {
		IAtomicLong placeGen = hazelcast.getAtomicLong(X10RT);
		return (int) placeGen.get();
	}
	
	public int x10rt_here() {
		return myPlaceId;
	}
	
	public int numDead() {
    	return 0;
    }
    
    public boolean isPlaceDead(int place) {
    	return false;
    }

    public int sendAsync(int place, byte[] data) {
    	ITopic<byte[]> link = hazelcast.getTopic(X10RTASYNC+'_'+Integer.toString(place));
    	if (DEBUG) System.err.println("sending async to topic "+(X10RTASYNC+'_'+Integer.toString(place)));
    	link.publish(data);
    	
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int sendClosure(int place, byte[] data) {
    	ITopic<byte[]> link = hazelcast.getTopic(X10RTCLOSURE+'_'+Integer.toString(place));
    	if (DEBUG) System.err.println("sending async to topic "+(X10RTCLOSURE+'_'+Integer.toString(place)));
    	link.publish(data);
    	
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
	
	public int x10rt_probe() {
		byte[] msg = null;
		msg = asyncCommunicator.poll();
		if (msg != null) {
			try {
				SocketTransport.runSimpleAsyncAtReceive(ByteBuffer.wrap(msg));
			}
			catch (IOException e) {
				e.printStackTrace();
				return RETURNCODE.X10RT_ERR_OTHER.ordinal();
			}
		}
		
		msg = closureCommunicator.poll();
		if (msg != null) {
			try {
				SocketTransport.runClosureAtReceive(ByteBuffer.wrap(msg));
			}
			catch (IOException e) {
				e.printStackTrace();
				return RETURNCODE.X10RT_ERR_OTHER.ordinal();
			}
		}
		
		return RETURNCODE.X10RT_ERR_OK.ordinal();
	}
	
	public int shutdown() {
		closureCommunicator.shutdown();
		asyncCommunicator.shutdown();
		return RETURNCODE.X10RT_ERR_OK.ordinal();
	}
	
	class Communicator implements MessageListener<byte[]> {
		private final Queue<byte[]> queue;
		private final String topic;
		private final String registration;
		
		Communicator(String topic) {
			super();
			this.topic = topic;
			this.queue = new LinkedList<byte[]>();
			ITopic<byte[]> link = hazelcast.getTopic(this.topic);
			this.registration = link.addMessageListener(this);
		}
		
		void shutdown() {
			try {
				ITopic<String> link = hazelcast.getTopic(topic);
				link.removeMessageListener(registration);
			}
			catch (HazelcastInstanceNotActiveException e){} // nothing to shut down
		}
		
		byte[] poll() {
			synchronized (queue) {
				return queue.poll();
			}
		}

		@Override
		public void onMessage(Message<byte[]> msg) {
			if (DEBUG) System.err.println("incoming message in place "+myPlaceId);
			synchronized (queue) {
				queue.add(msg.getMessageObject());
			}
		}
	}
}
