package x10.compiler.ws;

import x10.util.Random;
import x10.util.Stack;
import x10.lang.Lock;
import x10.compiler.Abort;

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
            //Runtime.println(k + " migrated by " + this);
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
                if (Frame.isNULL(k)) {
                    //Runtime.println(here + " :Worker(" + id + ") terminated");
                    return;
                }
                try {
                    unroll(Frame.cast[Object,Frame](k)); // top frames are meant to be on the stack
                } catch (Abort) {}
            }
        } catch (t:Throwable) {
            Runtime.println(here + "Uncaught exception in worker: " + t);
            t.printStackTrace();
        }
    }

    /*
     * The find could return
     * - RegularFrame: continue execution
     * - RemoteMainFrame: start a new remote task
     * - FinishFrame: from remote join
     */
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
            if (!(frame instanceof MainFrame) && !(frame instanceof RootFinish)) {
                Runtime.deallocObject(frame);
            }
            frame = up;
        }
    }

    static def derefFF(ref:GlobalRef[FinishFrame]) = (ref as GlobalRef[FinishFrame]{home==here})() as FinishFrame;
    static def deref(ref:GlobalRef[Frame]) = (ref as GlobalRef[Frame]{home==here})() as Frame;

    //the frame should be in heap, and could be copied deeply
    public def remoteAsync(place:Place, frame:RegularFrame){
        val id:Int = place.id;
        val body = ()=> @x10.compiler.RemoteInvocation {
            Runtime.wsFIFO().push(frame);
        };
        //Runtime.println(here + " :Run Remote job at place:" + id);
        Runtime.wsRunAsync(id, body);
        Runtime.dealloc(body);
        //need clean the heap allocated frame, too.
        Runtime.deallocObject(frame.up);
        Runtime.deallocObject(frame);
    }

    //the frame should be in heap, and could be copied deeply
    public def remoteAt(place:Place, frame:RegularFrame){
        val id:Int = place.id;
        val body = ()=> @x10.compiler.RemoteInvocation {
            Runtime.wsFIFO().push(frame);
        };
        //Runtime.println(here + " :Run Remote job at place:" + id);
        Runtime.wsRunAsync(id, body);
        Runtime.dealloc(body);
        //need clean the heap allocated frame, too.
        Runtime.deallocObject(frame.up.up);
        Runtime.deallocObject(frame.up);
        Runtime.deallocObject(frame);
        throw Abort.ABORT;
    }

    public def remoteFinishJoin(ffRef:GlobalRef[FinishFrame], stack:Stack[Throwable]) {
        val id:Int = ffRef.home.id;
        val body:()=>void = ()=> @x10.compiler.RemoteInvocation {
            val ff = derefFF(ffRef);
            if (!Frame.isNULL(stack)) {
                Runtime.atomicMonitor.lock();
                if (Frame.isNULL(ff.stack)) ff.stack = new Stack[Throwable]();
                while (!stack.isEmpty()) ff.stack.push(stack.pop());
                Runtime.atomicMonitor.unlock();
            }
            Runtime.wsFIFO().push(ff);
            //Runtime.println(here + " :FF join frame pushed");
        };
        //Runtime.println(here + " :Run Finish Join back to place:" + id);
        Runtime.wsRunCommand(id, body);
        Runtime.dealloc(body);
        throw Abort.ABORT;
    }

    /*
     * Notify the remote at's finish flag:boxedBoolean
     * Set it as true. Just execute it
     * No need atomic, so no need push the boxedBoolean to que.
     */
    public static def remoteAtNotify(ref:GlobalRef[Frame], t:Throwable) {
        val id:Int = ref.home.id;
        //need push the frame back to its inque
        //locate the remote worker
        val body:()=>void = ()=> @x10.compiler.RemoteInvocation {
            val frame = deref(ref);
            frame.throwable = t;
            Runtime.wsFIFO().push(frame);
            //Runtime.println(here + " :At Notify executed");
        };
        //Runtime.println(here + " :Run At Notify back to place:" + id);
        Runtime.wsRunCommand(id, body);
        Runtime.dealloc(body);
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
        try {
            frame.fast(worker00); // run main activity
        } catch (t:Abort) {
            worker00.run(); // join the pool
        } catch (t:Throwable) {
            frame.ff.caught(t); // main terminated abnormally
        } finally {
            allStop(worker00);
        }
        frame.ff.rethrowAll();
    }
}
