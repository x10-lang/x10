package x10.x10rt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import x10.x10rt.SocketTransport.CALLBACKID;
import x10.x10rt.SocketTransport.RETURNCODE;

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
		
	public HazelcastTransport() {
		hazelcast = Hazelcast.newHazelcastInstance();

		// Ask Hazelcast for a place ID
		IAtomicLong placeGen = hazelcast.getAtomicLong(X10RT);			
		myPlaceId = (int) placeGen.getAndIncrement();

		// If the X10 launcher started us, replace the hazelcast place ID with the launcher ID
		// this prevents confusing log messages
		String placeFlag = System.getenv(SocketTransport.X10_LAUNCHER_PLACE);
		if (placeFlag != null) 
			myPlaceId = Integer.parseInt(placeFlag);
		
		// establish link to other places
		ITopic<X10RTMessage> link = hazelcast.getTopic(X10RT+myPlaceId);
		topicRegistration = link.addMessageListener(new TopicListener());
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

    public int sendMessage(int place, int msg_id, ByteBuffer[] bytes) {
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
			synchronized (incoming) {
				incoming.add(msg.getMessageObject());
			}
		}
	}

}
