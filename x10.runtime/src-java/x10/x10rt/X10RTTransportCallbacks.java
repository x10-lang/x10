/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */
package x10.x10rt;

import x10.core.fun.VoidFun_0_1;
import x10.lang.Place;
import x10.network.NetworkTransportCallbacks;

/**
 * Implementation of NetworkTransportCallbacks for the X10RT transports. 
 */
final class X10RTTransportCallbacks implements NetworkTransportCallbacks {

    static VoidFun_0_1<Place> placeAddedHandler = null;
    static VoidFun_0_1<Place> placeRemovedHandler = null;
    private compressionCodec networkCompressor = ("snappy".equalsIgnoreCase(System.getProperty("X10RT_COMPRESSION", "none")))?compressionCodec.SNAPPY:compressionCodec.NONE;

    public void initDataStore(String connectTo) {
    	X10RT.initDataStore(connectTo);
    }
    
    public long getEpoch() {
    	return x10.xrx.Runtime.epoch$O();
    }
    
    public void setEpoch(long val) {
    	X10RT.initialEpoch = val;
    }
    
    public void runPlaceAddedHandler(int placeId) {
    	VoidFun_0_1<Place> handler = placeAddedHandler;
    	if (handler == null) return;
        
    	PlaceChangeWrapper pcw = new PlaceChangeWrapper(handler, placeId);
    	x10.xrx.Runtime.submitUncounted(pcw);
    }
    
    public void runPlaceRemovedHandler(int placeId) {
    	VoidFun_0_1<Place> handler = placeRemovedHandler;
    	if (handler == null) return;
    	
    	PlaceChangeWrapper pcw = new PlaceChangeWrapper(handler, placeId);
    	x10.xrx.Runtime.submitUncounted(pcw);
    }
    
    static final class PlaceChangeWrapper extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.serialization.X10JavaSerializable {
    	private VoidFun_0_1<Place> handler;
    	private long place;
    	
        public PlaceChangeWrapper(VoidFun_0_1<Place> handler, long place) {
        	this.handler = handler;
        	this.place = place;
        }

        @SuppressWarnings("unchecked")
        public static final x10.rtt.RuntimeType<PlaceChangeWrapper> $RTT = 
            x10.rtt.StaticVoidFunType.<PlaceChangeWrapper> make(PlaceChangeWrapper.class, new x10.rtt.Type[] { x10.core.fun.VoidFun_0_0.$RTT });
        
        public x10.rtt.RuntimeType<?> $getRTT() { return $RTT; }
        
        public x10.rtt.Type<?> $getParam(int i) { return null; }
        
        private Object writeReplace() throws java.io.ObjectStreamException {
            return new x10.serialization.SerializationProxy(this);
        }
        
        public static x10.serialization.X10JavaSerializable $_deserialize_body(x10.x10rt.X10RTTransportCallbacks.PlaceChangeWrapper $_obj, x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        	$_obj.handler = $deserializer.readObject();
            $_obj.place = $deserializer.readLong();
            return $_obj;
        }
        
        public static x10.serialization.X10JavaSerializable $_deserializer(x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        	x10.x10rt.X10RTTransportCallbacks.PlaceChangeWrapper $_obj = new x10.x10rt.X10RTTransportCallbacks.PlaceChangeWrapper((java.lang.System[]) null);
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }
        
        public void $_serialize(x10.serialization.X10JavaSerializer $serializer) throws java.io.IOException {
        	$serializer.write(this.handler);
        	$serializer.write(this.place);
        }
        
        // constructor just for allocation
        public PlaceChangeWrapper(final java.lang.System[] $dummy) {}
        
		@Override
		public void $apply() {
			Place p = new Place(this.place);
			this.handler.$apply(p, p.$getRTT());
		}
    }

	@Override
	public compressionCodec useCompressionCodec() {
		return networkCompressor;
	}
}
