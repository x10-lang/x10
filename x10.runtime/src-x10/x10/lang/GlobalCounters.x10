/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

import x10.compiler.Native;

/**
 * @author tardieu
 */
public final class GlobalCounters {

    static PRINT_STATS = false;

    @Native("c++","x10aux::asyncs_sent")
    static def getAsyncsSent():Long = 0L;

    @Native("c++","x10aux::asyncs_sent = #v")
    static def setAsyncsSent(v:Long):void { }

    @Native("c++","x10aux::asyncs_received")
    static def getAsyncsReceived():Long = 0L;

    @Native("c++","x10aux::asyncs_received = #v")
    static def setAsyncsReceived(v:Long):void { }

    @Native("c++","x10aux::serialized_bytes")
    static def getSerializedBytes():Long = 0L;

    @Native("c++","x10aux::serialized_bytes = #v")
    static def setSerializedBytes(v:Long):void { }

    @Native("c++","x10aux::deserialized_bytes")
    static def getDeserializedBytes():Long = 0L;

    @Native("c++","x10aux::deserialized_bytes = #v")
    static def setDeserializedBytes(v:Long):void { }

    public static def serializedSize[T](v:T) {
        var r:Long;
        @Native("java", "r = x10.runtime.impl.java.Runtime.serialize(v).length;")
        @Native("c++", "x10aux::serialization_buffer buf; buf.write(v); r = buf.length();")
        { r = -1L; }
        return r;
    }

    public static struct X10RTMessageStats {

        public def this () {
            this.bytesSent = 0;
            this.messagesSent = 0;
            this.bytesReceived = 0;
            this.messagesReceived = 0;
        }

        public def this (bytesSent:Long, messagesSent:Long, bytesReceived:Long, messagesReceived:Long) {
            this.bytesSent = bytesSent;
            this.messagesSent = messagesSent;
            this.bytesReceived = bytesReceived;
            this.messagesReceived = messagesReceived;
        }

        public bytesSent:Long;
        public messagesSent:Long;
        public bytesReceived:Long;
        public messagesReceived:Long;

        public operator + this = this;
        public operator - this = X10RTMessageStats(-bytesSent, -messagesSent, -bytesReceived, -messagesReceived);
        public operator this + (that:X10RTMessageStats) = X10RTMessageStats(bytesSent+that.bytesSent,
                                                                            messagesSent+that.messagesSent,
                                                                            bytesReceived+that.bytesReceived,
                                                                            messagesReceived+that.messagesReceived);
        public operator this - (that:X10RTMessageStats) = this + (-that);

        public def toString () = "[out:"+bytesSent+"/"+messagesSent+" in:"+bytesReceived+"/"+messagesReceived+"]";
    }

    public static struct X10RTStats {

        public def this () {
            this.msg = X10RTMessageStats();
            this.put = X10RTMessageStats();
            this.putCopiedBytesSent = 0;
            this.putCopiedBytesReceived = 0;
            this.get = X10RTMessageStats();
            this.getCopiedBytesSent = 0;
            this.getCopiedBytesReceived = 0;
        }

        public def this (msg:X10RTMessageStats,
                         put:X10RTMessageStats, putCopiedBytesSent:Long, putCopiedBytesReceived:Long,
                         get:X10RTMessageStats, getCopiedBytesSent:Long, getCopiedBytesReceived:Long) {
            this.msg = msg;
            this.put = put;
            this.putCopiedBytesSent = putCopiedBytesSent;
            this.putCopiedBytesReceived = putCopiedBytesReceived;;
            this.get = get;
            this.getCopiedBytesSent = getCopiedBytesSent;
            this.getCopiedBytesReceived = getCopiedBytesReceived;
        }

        public msg:X10RTMessageStats;

        public put:X10RTMessageStats;
        public putCopiedBytesSent:Long;
        public putCopiedBytesReceived:Long;

        public get:X10RTMessageStats;
        public getCopiedBytesSent:Long;
        public getCopiedBytesReceived:Long;

        public operator + this = this;
        public operator - this = X10RTStats(-msg,
                                            -put,-putCopiedBytesSent,-putCopiedBytesReceived,
                                            -get,-getCopiedBytesSent,-getCopiedBytesReceived);
        public operator this + (that:X10RTStats) = X10RTStats(msg+that.msg,
                                                              put+that.put,
                                                              putCopiedBytesSent+that.putCopiedBytesSent,
                                                              putCopiedBytesReceived+that.putCopiedBytesReceived,
                                                              get+that.get,
                                                              getCopiedBytesSent+that.getCopiedBytesSent,
                                                              getCopiedBytesReceived+that.getCopiedBytesReceived);
        public operator this - (that:X10RTStats) = this + (-that);

        public def toString () = 
            "msg:"+msg+
            " put:"+put+
            " putCopiedBytesSent:"+putCopiedBytesSent+
            " putCopiedBytesReceived:"+putCopiedBytesReceived+
            " get:"+get+
            " getCopiedBytesSent:"+getCopiedBytesSent+
            " getCopiedBytesReceived:"+getCopiedBytesReceived;
    }

    /** Fetch the current state of the X10RT-level counters, including Array.asyncCopy (i.e. get/put) information. */
    public static def getX10RTStats () {
        @Native("c++", "return x10aux::get_X10RTStats<x10::lang::GlobalCounters__X10RTStats,x10::lang::GlobalCounters__X10RTMessageStats>();")
        {
            return X10RTStats();
        }
    }

    /** Fetch the current state of the X10RT-level counters, excluding anything related to Array.asyncCopy */
    public static def getX10RTMessageStats () = getX10RTStats().msg;

    public static def printStats() {
        if (PRINT_STATS) {
            Runtime.println("ASYNC SENT AT PLACE " + here.id +" = " + GlobalCounters.getAsyncsSent());
            Runtime.println("ASYNC RECV AT PLACE " + here.id +" = " + GlobalCounters.getAsyncsReceived());
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
