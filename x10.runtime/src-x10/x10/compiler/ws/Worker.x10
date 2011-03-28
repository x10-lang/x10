package x10.compiler.ws;

import x10.compiler.Abort;

import x10.lang.Lock;

import x10.util.Random;

public final class Worker {
    public val workers:Rail[Worker];
    private val random:Random;

    public val deque = new Deque();
    public var fifo:Deque = deque; // hack to avoid stealing from null fifo
    public val lock = new Lock();

    public def this(i:Int, workers:Rail[Worker]) {
        random = new Random(i + (i << 8) + (i << 16) + (i << 24));
        this.workers = workers;
    }

    public def migrate() {
        var k:RegularFrame;
        lock.lock();
        while (!Frame.isNULL(k = Frame.cast[Object,RegularFrame](deque.steal()))) {
            val r = k.remap();
            Runtime.atomicMonitor.lock(); r.ff.asyncs++; Runtime.atomicMonitor.unlock();
            fifo.push(r);
        }
        lock.unlock();
    }

    public def run() {
        try {
            while (true) {
                val k = find();
                if (Frame.isNULL(k)) return;
                try {
                    unroll(Frame.cast[Object,Frame](k));
                } catch (Abort) {}
            }
        } catch (t:Throwable) {
            Runtime.println("Uncaught exception at place " + here + " in WS worker: " + t);
            t.printStackTrace();
        }
    }

    public def find():Object {
        var k:Object;
        //1) cur thread fifo
        k = fifo.steal();
        while (Frame.isNULL(k)) {
            if (Runtime.wsEnded()) return Frame.NULL[Object]();
            //2) other thread fifo
            val rand = random.nextInt(Runtime.NTHREADS);
            k = workers(rand).fifo.steal();
            if (!Frame.isNULL(k)) break;
            //3) other thread deque
            if (workers(rand).lock.tryLock()) {
                k = workers(rand).deque.steal();
                if (!Frame.isNULL(k)) {
                    val r = Frame.cast[Object,RegularFrame](k).remap();
                    Runtime.atomicMonitor.lock(); r.ff.asyncs++; Runtime.atomicMonitor.unlock();
                    k = r;
                }
                workers(rand).lock.unlock();
            }
            if (!Frame.isNULL(k)) break;
            //4) remote activity
            Runtime.wsProcessEvents();
            k = fifo.steal();
        }
        return k;
    }

    public def unroll(var frame:Frame) {
        var up:Frame;
        while (true) {
            frame.wrapResume(this);
            up = frame.up;
            up.wrapBack(this, frame);
            Runtime.deallocObject(frame);
            frame = up;
        }
    }

    public def remoteAsync(place:Place, frame:RegularFrame){
        val id:Int = place.id;
        val body = ()=> @x10.compiler.RemoteInvocation {
            Runtime.wsFIFO().push(frame);
        };
        Runtime.wsRunAsync(id, body);
    }

    public def remoteAt(place:Place, frame:RegularFrame){
        val id:Int = place.id;
        val body = ()=> @x10.compiler.RemoteInvocation {
            Runtime.wsFIFO().push(frame);
        };
        Runtime.wsRunAsync(id, body);
        throw Abort.ABORT;
    }

    public static def allStop(worker:Worker){
        val body = ()=> @x10.compiler.RemoteInvocation { Runtime.wsEnd(); };
        for (var i:Int = 1; i<Place.MAX_PLACES; i++) {
            Runtime.wsRunCommand(i, body);
        }
        Runtime.dealloc(body);
        Runtime.wsEnd();
    }

    public static def initPerPlace() {
        Runtime.wsInit();
        val workers = Rail.make[Worker](Runtime.NTHREADS);
        for (var i:Int = 0; i<Runtime.NTHREADS; i++) {
            workers(i) = new Worker(i, workers);
        }
        workers(0).fifo = Runtime.wsFIFO();
        for(var i:Int = 1; i<Runtime.NTHREADS; i++) {
            val worker = workers(i);
            async {
                worker.fifo = Runtime.wsFIFO();
                worker.run();
            }
        }
        return workers(0);
    }

    public static def main(frame:MainFrame) {
        val worker00 = initPerPlace(); // init place 0 first
        for (var i:Int = 1; i<Place.MAX_PLACES; i++) { // init place >0
            val p = Place.place(i);
            async at(p) initPerPlace().run();
        }
        val ff = frame.ff;
        try {
            frame.fast(worker00); // run main activity
        } catch (t:Abort) {
            worker00.run(); // join the pool
        } catch (t:Throwable) {
            ff.caught(t); // main terminated abnormally
        } finally {
            allStop(worker00);
        }
        ff.check();
    }
}
