package x10.compiler.ws;

import x10.util.Random;
import x10.lang.Lock;
import x10.compiler.SuppressTransientError;

public final class Worker {
    // FIXME: This doesn't work at all with multi-place programs in X10 2.1!
    //        You can't serialize a Worker across places and expect anything sensible to happen!
    private static workers = Rail.make[Worker](Runtime.INIT_THREADS, (i:Int)=>new Worker(i));

    public static finished:BoxedBoolean = new BoxedBoolean();

    private val random:Random;
    @SuppressTransientError
    public transient val deque = new Deque(); // FIXME: Shouldn't be transient, because this class should never be serialized
    @SuppressTransientError
    public transient val fifo = new Deque();  // FIXME: Shouldn't be transient, because this class should never be serialized
    @SuppressTransientError
    public transient val lock = new Lock();   // FIXME: Shouldn't be transient, because this class should never be serialized

    public def this(i:Int) {
        random = new Random(i + (i << 8) + (i << 16) + (i << 24));
    }

    public def migrate() {
        var k:RegularFrame;
        lock.lock();
        while (null != (k = Frame.cast[Object,RegularFrame](deque.steal()))) {
//            Runtime.println(k + " migrated by " + this);
            fifo.push(Frame.upcast[RegularFrame,Object](k.remap()));
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
        }
    }

    public def find():RegularFrame {
        var k:Object = Frame.NULL[Object]();
//        if (deque.poll() != null) Runtime.println("ERROR");
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