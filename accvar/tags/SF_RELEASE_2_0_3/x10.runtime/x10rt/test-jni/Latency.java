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

import x10.x10rt.ActiveMessage;
import x10.x10rt.MessageRegistry;
import x10.x10rt.Place;
import x10.x10rt.X10RT;
import x10.x10rt.UnknownMessageException;

public class Latency {

    static int MAXBUFSIZE    = 1<<20; 
    static int _g_flag       = 0;
    static int _g_flagcntr   = 0;

    public static void main(String[] args) throws UnknownMessageException {

        int [] sbuf = new int[MAXBUFSIZE];

        int rank = X10RT.here().getId();
        Place otherPlace = X10RT.getPlace(rank^1);

        if(2 != X10RT.numPlaces()) {
            System.err.println("This test requires exactly 2 processes");
            System.exit(1);
        }

        X10RT.barrier();

        ActiveMessage ping = MessageRegistry.register(Latency.class, 
                "MessageHandler", new int[0].getClass(), Integer.TYPE);

        if(0 == rank) {
            System.out.println(" Bidirectional simultaneous AMsend test");
            System.out.println("========================================");
            System.out.println(" buflen(B)  latency(us)  bandwidth(MB/s)\n");
        }

        for (int i=0;i<MAXBUFSIZE; i++) { sbuf[i] = (rank+i) & 0xFF; }

        X10RT.barrier();

        for(int buflen=1; buflen <= MAXBUFSIZE; buflen <<= 1) {
            X10RT.barrier();
            ++ _g_flagcntr;
            ping.send(otherPlace, 0, buflen-1, sbuf);
            while(_g_flag < _g_flagcntr) X10RT.probe();
            int niters = (buflen > 10000) ? 10 : 100;
            X10RT.barrier();
            long t0 = System.nanoTime();
            for(int i=0; i < niters; ++i) {
                ++ _g_flagcntr;
                ping.send(otherPlace, 0, buflen-1, sbuf);
                while (_g_flag < _g_flagcntr) X10RT.probe();
            }
            long t1 = System.nanoTime();
            if (rank == 0) {
                int bytes = buflen * 4;
                System.out.format("%7d", bytes);
                System.out.format("%12.3f", (t1-t0)/(double)niters/1.0e3);
                System.out.format("%12.3f", bytes * niters / ((t1-t0)/1.0e3));
                System.out.println("");
            }
            X10RT.barrier();
        }

        X10RT.barrier();
    }

    public static void MessageHandler(int[] data, int messageSize) 
        throws UnknownMessageException, IllegalArgumentException {
        _g_flag ++;
    }
}
