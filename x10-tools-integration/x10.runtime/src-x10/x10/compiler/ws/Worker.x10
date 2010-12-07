package x10.compiler.ws;

import x10.util.Random;
import x10.lang.Lock;
import x10.compiler.SuppressTransientError;

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
            val r = k.remap();
            atomic r.ff.asyncs++;
            fifo.push(Frame.upcast[RegularFrame,Object](r));
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
                    unstack(Frame.upcast[RegularFrame,Frame](k)); // top frames are meant to be on the stack
                } catch (Stolen) {}
                purge(Frame.upcast[RegularFrame,Frame](k), k.ff); // needed because we did not stack allocate those frames
            }
        } catch (t:Throwable) {
            Runtime.println("Uncaught exception in worker: " + t);
            t.printStackTrace();
        }
    }

    public def find():RegularFrame {
        var k:Object = Frame.NULL[Object]();
//        if (deque.poll() != null) Runtime.println("deque.poll() != null");
        k = fifo.steal();
        while (null == k) {
            if (finished.value) return Frame.NULL[RegularFrame](); // TODO: termination condition
            k = workers(random.nextInt(Runtime.INIT_THREADS)).fifo.steal();
            if (null != k) break;
            val i = random.nextInt(Runtime.INIT_THREADS);
            if (workers(i).lock.tryLock()) {
                k = workers(i).deque.steal();
                if (null!= k) {
                    val p = Frame.cast[Object,RegularFrame](k);
                    val r = p.remap();
                    // frames from k upto k.ff excluded should be stack-allocated but cannot because of @StackAllocate limitations
                    k = Frame.upcast[RegularFrame,Object](r);
                    atomic r.ff.asyncs++;
                }
                workers(i).lock.unlock();
            }
        }
//        Runtime.println(k + " stolen");
        return Frame.cast[Object,RegularFrame](k);
    }

    public static def purge(var frame:Frame, ff:FinishFrame) {
        while (!Frame.eq(frame, ff)) {
            val up = frame.up;
            if (frame instanceof MainFrame || frame instanceof RootFinish) return;
            Runtime.deallocObject(Frame.upcast[Frame,Object](frame));
            frame = up;
        }
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
            if (!(frame instanceof MainFrame) && !(frame instanceof RootFinish)) {
                Runtime.deallocObject(Frame.upcast[Frame,Object](frame));
            }
            try {
                up.resume(this);
            } catch (Stolen) {
                if (up instanceof RegularFrame) {
                    purge(up, Frame.cast[Frame,RegularFrame](up).ff);
                }
                throw Stolen.STOLEN;
            }
            frame = up;
        }
    }

    public def unstack(var frame:Frame) {
        var up:Frame;
        while (true) {
            up = frame.up;
            if (null == up) return;
            if (frame instanceof FinishFrame) {
                // moving to heap-allocated frames
                unroll(frame);
                return;
            }
            up.back(this, frame);
            up.resume(this);
            frame = up;
        }
    }

    public static def main(frame:MainFrame) {
        val workers = Rail.make[Worker](Runtime.INIT_THREADS);
        val finished = new BoxedBoolean();
        for (var i:Int = 0; i<Runtime.INIT_THREADS; i++) {
            workers(i) = new Worker(i, workers, finished);
        }
        for (var i:Int = 1; i<Runtime.INIT_THREADS; i++) {
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