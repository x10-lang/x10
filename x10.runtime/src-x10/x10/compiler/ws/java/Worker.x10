package x10.compiler.ws.java;

import x10.util.Random;
import x10.lang.Lock;

public final class Worker {
    private val workers:Rail[Worker];
    private val random:Random;

    public val finished:BoxedBoolean;
    public val deque = new Deque();
    public val fifo = new Deque();
    public val lock = new Lock();

    public def this(i:Int, workers:Rail[Worker], finished:BoxedBoolean) {
        random = new Random(i + (i << 8) + (i << 16) + (i << 24));
        this.workers = workers;
        this.finished = finished;
    }

    public def migrate() {
        var k:RegularFrame;
        lock.lock();
        while (null != (k = Frame.cast[Object,RegularFrame](deque.steal()))) {
//            Runtime.println(k + " migrated by " + this);
            atomic k.ff.asyncs++;
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
            t.printStackTrace();
        }
    }

    public def find():RegularFrame {
        var k:Object = null;
//        if (deque.poll() != null) Runtime.println("deque.poll() != null");
        k = fifo.steal();
        while (null == k) {
            if (finished.value) return null; // TODO: termination condition
            k = workers(random.nextInt(Runtime.NTHREADS)).fifo.steal();
            if (null != k) break;
            val i = random.nextInt(Runtime.NTHREADS);
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
        val workers = Rail.make[Worker](Runtime.NTHREADS);
        val finished = new BoxedBoolean();
        for (var i:Int = 0; i<Runtime.NTHREADS; i++) {
            workers(i) = new Worker(i, workers, finished);
        }
        for (var i:Int = 1; i<Runtime.NTHREADS; i++) {
            val ii = i;
            async workers(ii).run();
        }
        try {
            frame.fast(workers(0));
            workers(0).finished.value = true;
        } catch (Stolen) {
            workers(0).run();
        } catch (t:Throwable) {
            Runtime.println("Uncaught exception in main: " + t);
            t.printStackTrace();
        }
    }
}
