/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.compiler.ws;

import x10.xrx.*;
import x10.compiler.Abort;
import x10.compiler.Ifdef;
import x10.compiler.NoReturn;

import x10.util.Random;

import x10.util.concurrent.Lock;

public final class Worker {
    public val workers:Rail[Worker];
    private val random:Random;

    public val id:Int;
    public val deque = new Deque();
    public var fifo:Deque = deque; // hack to avoid stealing from null fifo
    public val lock = new Lock();

    public var throwable:Exception = null;
    
    public def this(i:Int, workers:Rail[Worker]) {
        random = new Random(i + (i << 8n) + (i << 16n) + (i << 24n));
        this.id = i;
        this.workers = workers;
    }

    public def migrate() {
        var k:RegularFrame;
        lock.lock();
        while (null != (k = Frame.cast[Any,RegularFrame](deque.steal()))) {
            @Ifdef("__CPP__") {
                k = k.remap();
            }
            Runtime.atomicMonitor.lock(); k.ff.asyncs++; Runtime.atomicMonitor.unlock();
            fifo.push(k);
        }
        lock.unlock();
    }

    public def run() {
        try {
            while (true) {
                val k = find();
                if (null == k) return;
                try {
                    unroll(Frame.cast[Any,Frame](k));
                } catch (Abort) {}
            }
        } catch (t:Exception) {
            Runtime.println("Uncaught exception at place " + here + " in WS worker: " + t);
            t.printStackTrace();
        }
    }

    public def find():Any {
        var k:Any;
        //1) cur thread fifo
        k = fifo.steal();
        while (null == k) {
            if (Runtime.wsEnded()) return null;
            //2) other thread fifo
            val rand = random.nextInt(Runtime.NTHREADS);
            val victim = workers(rand);
            k = victim.fifo.steal();
            if (null != k) break;
            //3) other thread deque
            if (victim.lock.tryLock()) {
                k = victim.deque.steal();
                if (null != k) {
                    var r:RegularFrame = Frame.cast[Any,RegularFrame](k);
                    @Ifdef("__CPP__") {
                        r = r.remap();
                        k = r;
                    }
                    Runtime.atomicMonitor.lock(); r.ff.asyncs++; Runtime.atomicMonitor.unlock();
                }
                victim.lock.unlock();
            }
            if (null != k) break;
            //4) remote activity
            Runtime.x10rtProbe();
            k = fifo.steal();
        }
        return k;
    }

    @NoReturn public def unroll(var frame:Frame) {
        var up:Frame;
        while (true) {
            frame.wrapResume(this);
            up = frame.up;
            up.wrapBack(this, frame);
            Unsafe.dealloc(frame);
            frame = up;
        }
    }

    public static def wsRunAsync(id:Long, body:()=>void):void {
        if (id == Runtime.hereLong()) {
            val copy = Runtime.deepCopy(body);
            copy();
            Unsafe.dealloc(copy);
        } else {
            Runtime.x10rtSendMessage(id, body, null);
        }
        Unsafe.dealloc(body);
    }

    public static def runAsyncAt(place:Place, frame:RegularFrame){
        val body = ()=> @x10.compiler.RemoteInvocation("runAsyncAt") { Runtime.wsFIFO().push(frame); };
        wsRunAsync(place.id, body);
    }

    @NoReturn static public def runAt(place:Place, frame:RegularFrame){
        val body = ()=> @x10.compiler.RemoteInvocation("runAt") { Runtime.wsFIFO().push(frame); };
        wsRunAsync(place.id, body);
        throw Abort.ABORT;
    }

    public static def stop(){
        val body = ()=> @x10.compiler.RemoteInvocation("stop") { Runtime.wsEnd(); };
        for (p in Place.places()) {
            if (p != here) { 
                Runtime.x10rtSendMessage(p.id, body, null);
            }
        }
        Unsafe.dealloc(body);
        Runtime.wsEnd();
    }

    public static def startHere() {
        Runtime.wsInit();
        val workers = new Rail[Worker](Runtime.NTHREADS);
        for (var i:Int = 0n; i<Runtime.NTHREADS; i++) {
            workers(i) = new Worker(i, workers);
        }
        workers(0).fifo = Runtime.wsFIFO();
        for(var i:Int = 1n; i<Runtime.NTHREADS; i++) {
            val worker = workers(i);
            async {
                worker.fifo = Runtime.wsFIFO();
                worker.run();
            }
        }
        return workers(0);
    }

    public static def start() {
        val worker = startHere(); // init place 0 first
        for (p in Place.places()) { // init place >0
            if (p != here) { 
                at(p) async startHere().run();
            }
        }
        return worker;
    }

    public static def main(frame:MainFrame) {
        val worker = start();
        val ff = frame.ff;
        var finalize:Boolean = true; // FinallyEliminator
        try {
            frame.fast(worker); // run main activity
        } catch (t:Abort) {
            finalize = false;
            worker.run(); // join the pool
        } catch (t:Exception) {
            ff.caught(t); // main terminated abnormally
        } finally {
            if (finalize) stop();
        }
        ff.check();
    }

    public def rethrow() {
        if (null != throwable) {
            val t = throwable;
            throwable = null;
            throw t;
        }
    }
}
