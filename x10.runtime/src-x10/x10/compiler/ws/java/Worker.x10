package x10.compiler.ws.java;

import x10.util.Random;
import x10.lang.Lock;

public final class Worker {
    private static workers = Rail.make[Worker](Runtime.INIT_THREADS, (i:Int)=>new Worker(i));

    public static finished:BoxedBoolean = new BoxedBoolean();

    private val random:Random;
    public val deque = new Deque();
    public val fifo = new Deque();
    public val lock = new Lock();

    public def this(i:Int) {
        random = new Random(i + (i << 8) + (i << 16) + (i << 24));
    }

    public def migrate() {
        var k:RegularFrame;
        lock.lock();
        while (null != (k = Frame.cast[Object,RegularFrame](deque.steal()))) {
//            Runtime.println(k + " migrated by " + this);
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
                    k.resume(this);
                    unroll(k);
                } catch (Stolen) {}
            }
        } catch (t:Throwable) {
            Runtime.println("Uncaught exception in worker: " + t);
        }
    }

    public def find():RegularFrame {
        var k:Object = null;
//        if (deque.poll() != null) Runtime.println("ERROR");
        k = fifo.steal();
        while (null == k) {
            if (finished.value) return null; // TODO: termination condition
            k = workers(random.nextInt(Runtime.INIT_THREADS)).fifo.steal();
            if (null != k) break;
            val i = random.nextInt(Runtime.INIT_THREADS);
            if (workers(i).lock.tryLock()) {
                k = workers(i).deque.steal();
                if (null != k) {
                    atomic Frame.cast[Object,RegularFrame](k).ff.asyncs++;
                }
                workers(i).lock.unlock();
            }
        }
//        Runtime.println(k + " stolen");
        return Frame.cast[Object,RegularFrame](k);
    }

    public def unroll(var frame:Frame) {
        var up:Frame;
        while (true) {
            up = frame.up;
            if (null == up) return;
            if (frame instanceof FinishFrame) {
                var asyncs:Int;
                atomic asyncs = --Frame.cast[Frame,FinishFrame](frame).asyncs;
                if (0 != asyncs) return;
            }
            up.back(this, frame);
            up.resume(this);
            frame = up;
        }
    }

    public static def main(frame:MainFrame) {
        for (var i:Int = 1; i<Runtime.INIT_THREADS; i++) {
            val ii = i;
            async workers(ii).run();
        }
        try {
            frame.fast(workers(0));
            Worker.finished.value = true;
        } catch (Stolen) {
            workers(0).run();
        } catch (t:Throwable) {
            Runtime.println("Uncaught exception in main: " + t);
        }
    }
}
