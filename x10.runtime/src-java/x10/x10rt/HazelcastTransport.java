package x10.x10rt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import x10.x10rt.SocketTransport.CALLBACKID;
import x10.x10rt.SocketTransport.RETURNCODE;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class HazelcastTransport {
	
	private static final String X10RT = "X10RT";
	private int myPlaceId;
	private HazelcastInstance hazelcast;
	private final Queue<X10RTMessage> incoming = new LinkedList<X10RTMessage>();
	private final String topicRegistration;
	private int initialNumPlaces = 0; // when the launcher is used, we expect some known number of places before running
	private boolean initializing = true;

	public HazelcastTransport() {
		Config config = new Config();
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
		hazelcast = Hazelcast.newHazelcastInstance(config);

		// Ask Hazelcast for a place ID
		IAtomicLong placeGen = hazelcast.getAtomicLong(X10RT);
		myPlaceId = (int) placeGen.getAndIncrement();
		//System.err.println("Hazelcast says that I am place "+myPlaceId);

		// If the X10 launcher started us, replace the hazelcast place ID with the launcher ID
		// this prevents confusing log messages
		String placeFlag = System.getenv(SocketTransport.X10_LAUNCHER_PLACE);
		if (placeFlag != null) {
			myPlaceId = Integer.parseInt(placeFlag);
			//System.err.println("changed place ID to "+myPlaceId);
			initialNumPlaces = Integer.parseInt(System.getenv(SocketTransport.X10_NPLACES));
		}
		
		// establish link to other places
		ITopic<X10RTMessage> link = hazelcast.getTopic(X10RT+myPlaceId);
		topicRegistration = link.addMessageListener(new TopicListener());
		System.err.println("registered on topic "+(X10RT+myPlaceId));
		
		// In this environment, it is difficult to know if a place is missing because
		// we're in the middle of startup, or if it has gone away.  At this point we know
		// that we're in startup mode. Hold up the main for place 0 until all expected 
		// initial places have joined
		if (myPlaceId == 0)
			x10rt_nplaces(); // wait here
	}
	
	public int x10rt_nplaces() {
		IAtomicLong placeGen = hazelcast.getAtomicLong(X10RT);
		long numJoined = placeGen.get();
		boolean waited = false;
		if (initializing && numJoined < initialNumPlaces) {
			System.err.println("Hazelcast says that there are "+placeGen.get()+" places");
			System.err.println("Waiting until Hazelcast says that there are "+initialNumPlaces+" places");
			waited = true;
		}
		while (initializing && numJoined < initialNumPlaces) {
			// wait at startup time for all expected places to join here
			try {Thread.sleep(10);} 
			catch (InterruptedException e){}
			numJoined = placeGen.get();
		}
		if (waited)
			System.err.println("Finished waiting for "+initialNumPlaces+" places to join");
		initializing = false;
		return (int) numJoined;
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

    public int sendMessage(int place, int msg_id, ByteBuffer[] bytes) {
    	System.err.println("sending message id "+msg_id+" to topic "+(X10RT+place));
    	X10RTMessage msg = new X10RTMessage(msg_id, bytes);
    	ITopic<X10RTMessage> link = hazelcast.getTopic(X10RT+place);
    	link.publish(msg);
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
	
	public int x10rt_probe() {
		X10RTMessage msg = null;
		synchronized (incoming) {
			msg = incoming.poll();
		}
		
		try {
			if (msg != null) {
				if (msg.callbackId == CALLBACKID.closureMessageID.ordinal())
					SocketTransport.runClosureAtReceive(ByteBuffer.wrap(msg.data));
				else if (msg.callbackId == CALLBACKID.simpleAsyncMessageID.ordinal())
					SocketTransport.runSimpleAsyncAtReceive(ByteBuffer.wrap(msg.data));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return RETURNCODE.X10RT_ERR_OTHER.ordinal();
		}
		
		return RETURNCODE.X10RT_ERR_OK.ordinal();
	}
	
	public int shutdown() {
		ITopic<X10RTMessage> link = hazelcast.getTopic(X10RT+myPlaceId);
		link.removeMessageListener(topicRegistration);
		return RETURNCODE.X10RT_ERR_OK.ordinal();
	}
	
	private class X10RTMessage {
		final int callbackId;
    	final byte[] data;

    	public X10RTMessage(int callbackId, ByteBuffer[] bbdata) {
			super();
			this.callbackId = callbackId;
			int datalen = 0;
			for (ByteBuffer bb : bbdata)
				datalen+=bb.remaining();
			this.data = new byte[datalen];
			ByteBuffer localdata = ByteBuffer.wrap(this.data);
			for (ByteBuffer bb : bbdata)
				localdata.put(bb);
		}
    }
	
	private class TopicListener implements MessageListener<X10RTMessage> {

		@Override
		public void onMessage(Message<X10RTMessage> msg) {
			System.err.println("incoming message in place "+myPlaceId);
			synchronized (incoming) {
				incoming.add(msg.getMessageObject());
			}
		}
	}

}
