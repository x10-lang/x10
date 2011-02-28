package x10.compiler.ws;

import x10.util.Random;
import x10.lang.Lock;
import x10.compiler.SuppressTransientError;
import x10.compiler.RemoteInvocation;

public final class Worker {
    public val workers:Rail[Worker];
    private val id:Int; //Could be removed finally ?
    private val random:Random;

    public val deque = new Deque();
    public val fifo = new Deque();
    public val lock = new Lock();
    
    public val finished:BoxedBoolean;

    public def this(i:Int, workers:Rail[Worker], finished:BoxedBoolean) {
        random = new Random(i + (i << 8) + (i << 16) + (i << 24));
        this.id = i;
        this.workers = workers;
        this.finished = finished;
    }

    public def notifyStop(){
        finished.value = true;
        //Runtime.println(here +": was notified to stop()");
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
        //And try process remote msg: add jobs into fifo
        Runtime.wsProcessEvents();
        if(fifo.size() > 0){
            return; //at least there are some jobs in fifo, which the worker could get from find();
            //but other worker may still steal them.
        }
        
        //Try a mini steal from other threads
        k = Frame.cast[Object,RegularFrame](workers(random.nextInt(Runtime.INIT_THREADS)).fifo.steal());
        if (null != k){
            fifo.push(Frame.upcast[RegularFrame,Object](k));
            return;
        }
        
        if(fifo.size() > 0){
            return; //at least there are some jobs in fifo, which the worker could get from find();
            //but other worker may still steal them.
        }
        
        val i = random.nextInt(Runtime.INIT_THREADS);
        if (workers(i).lock.tryLock()) {
            k = Frame.cast[Object,RegularFrame](workers(i).deque.steal());
            if (null!= k) {
                val r = k.remap();
                atomic r.ff.asyncs++;
                fifo.push(Frame.upcast[RegularFrame,Object](r));
            }
            workers(i).lock.unlock();
        }
    }                            

    public def run() {
        //Runtime.println(here + ": Worker(" + id + ") started..." );
        Runtime.wsBindWorker(this, id);
        try {
            while (true) {
                val k = find();
                if (null == k) {
                    //Runtime.println(here + " :Worker(" + id + ") terminated");
                    return;
                }
                if(k instanceof RegularFrame){
                    //could be a regular frame of local, or remote one. Just call resume
                    val r:RegularFrame = Frame.cast[Object,RegularFrame](k);
                    try {
                        r.resume(this);
                        unstack(Frame.upcast[RegularFrame,Frame](r)); // top frames are meant to be on the stack
                    } catch (Stolen) {}
                    purge(Frame.upcast[RegularFrame,Frame](r), r.ff); // needed because we did not stack allocate those frames
                }
                else if(k instanceof FinishFrame){
                    //finish frame, need run the finish frame's unroll
                    val p:FinishFrame = Frame.cast[Object, FinishFrame](k);
                    try{
                        //Runtime.println(here+" :Execute remote finish join");
                        unroll(p);
                    } catch (Stolen){}
                }
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
        var k:Object = Frame.NULL[Object]();
//        if (deque.poll() != null) Runtime.println("deque.poll() != null");
    
        //The following sequence decides which type of task has the highest priority
        //right now: 1) cur thread fifo; 2) other thread fifo; 3) other thread deque; 4) remote job 
        //1) cur thread fifo
        k = fifo.steal();
        while (null == k) {
            if (finished.value) return Frame.NULL[Object](); // TODO: termination condition
            //2) other thread fifo
            k = workers(random.nextInt(Runtime.INIT_THREADS)).fifo.steal();
            if (null != k) break;
            //3) other thread deque
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
            if (null != k) break;
            //4) remote again
            Runtime.wsProcessEvents();    
            k = fifo.steal();
        }
//        Runtime.println(k + " stolen");
        return k;
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

    static def deref[T](root:GlobalRef[Worker]) = (root as GlobalRef[Worker]{home==here})() as T;
    static def derefFrame[T](ffRef:GlobalRef[FinishFrame]) = (ffRef as GlobalRef[FinishFrame]{home==here})() as T;
    static def derefBB[T](root:GlobalRef[BoxedBoolean]) = (root as GlobalRef[BoxedBoolean]{home==here})() as T;
    
    //the frame should be in heap, and could be copied deeply
    public def remoteRunFrame(place:Place, frame:RegularFrame, ff:FinishFrame){
        atomic ff.asyncs++; //need add the frame's structure
        val id:Int = place.id;
        val body:()=>void = ()=> {
            //Worker.pushRemoteFrame(frame); //place local handle version
            val worker = Runtime.wsWorker() as Worker;
            if(worker == null){
                throw new RuntimeException(here + "[WSRT_ERR]The current X10 thread has no bound WS Worker");
            }
            worker.fifo.push(Frame.upcast[RegularFrame, Object](frame));
        };
        //Runtime.println(here + " :Run Remote job at place:" + id);
        Runtime.wsRunAsync(id, body);
        Runtime.dealloc(body);
        //need clean the heap allocated frame, too.
        //The RemoteMainFrame, the RemoteRootFinish & the RemoteRootFrame
        Runtime.deallocObject(Frame.upcast[Frame,Object](frame.up.up));
        Runtime.deallocObject(Frame.upcast[Frame,Object](frame.up));
        Runtime.deallocObject(Frame.upcast[Frame,Object](frame));
    }

    public def remoteFinishJoin(ffRef:GlobalRef[FinishFrame]) {
        val id:Int = ffRef.home.id;
        val body:()=>void = ()=>{       
            val worker = Runtime.wsWorker() as Worker;
            if(worker == null){
                throw new RuntimeException(here + "[WSRT_ERR]The current X10 thread has no bound WS Worker");
            }
            worker.fifo.push(Frame.upcast[FinishFrame, Object](derefFrame[FinishFrame](ffRef)));
            //Runtime.println(here + " :FF join frame pushed");
        };
        //Runtime.println(here + " :Run Finish Join back to place:" + id);
        Runtime.wsRunCommand(id, body);
        Runtime.dealloc(body);
    }

    /*
     * Notify the remote at's finish flag:boxedBoolean
     * Set it as true. Just execute it
     * No need atomic, so no need push the boxedBoolean to que.
     */
    public static def remoteAtNotify(bbRef:GlobalRef[BoxedBoolean]) {
        val id:Int = bbRef.home.id;
        //need push the frame back to its inque
        //locate the remote worker
        val body:()=>void = ()=>{            
            derefBB[BoxedBoolean](bbRef).value = true;
            //Runtime.println(here + " :At Notify executed");
        };
        //Runtime.println(here + " :Run At Notify back to place:" + id);
        Runtime.wsRunCommand(id, body);
        Runtime.dealloc(body);
    }
    
    public static def allStop(worker:Worker){
        for(var id:Int = 0; id < Place.MAX_PLACES; id++ ) {
            val idd:Int = id;
            val body:()=>void = ()=>{
                val worker = Runtime.wsWorker() as Worker;
                if(worker == null){
                    throw new RuntimeException(here + "[WSRT_ERR]The current X10 thread has no bound WS Worker");
                }
                worker.notifyStop();
            };
            Runtime.wsRunCommand(idd, body);
            Runtime.dealloc(body);
        }
    }

    public static def main(frame:MainFrame) {
        //First iteration, create all workers, and get the globalRef array
        for (p in Place.places()) {
            if(p == here){
                continue; //in later loop
            }
            async at(p) {
                val workers = Rail.make[Worker](Runtime.INIT_THREADS);
                val finished = new BoxedBoolean();
                for (var i:Int = 0; i<Runtime.INIT_THREADS; i++) {
                    workers(i) = new Worker(i, workers, finished);
                }
                for( var i:Int = 0; i<Runtime.INIT_THREADS; i++) {
                    val ii = i;
                    async workers(ii).run();
                }
            }
        }

        //2nd iteration, current place
        val workers = Rail.make[Worker](Runtime.INIT_THREADS);
        val finished = new BoxedBoolean();
        for (var i:Int = 0; i<Runtime.INIT_THREADS; i++) {
            workers(i) = new Worker(i, workers, finished);
        }
        for( var i:Int = 1; i<Runtime.INIT_THREADS; i++) {
            val ii = i;
            async workers(ii).run();
        }

        //start first worker at place0
        val worker00 = workers(0);
        try {
            Runtime.wsBindWorker(worker00, 0);
            frame.fast(worker00);
            //If the app goes here, it means it always in fast path. 
            //We need process the ff.asyncs to make sure it can quit correctly
            var asyncs:Int;
            atomic asyncs = --frame.ff.asyncs;
            if(asyncs > 0) {
                //Runtime.println(here + " :Worker(0) will start after main's fast..." );
                worker00.run();
            }
            else {
                //global stop
                //Runtime.println(here+":Fire all stop msg from fast" );
                Worker.allStop(worker00);
                //Runtime.println(here + ":Worker(0) terminated in fast");    
            }
        } catch (Stolen) {
            //Runtime.println(here + " :Worker(0) will start after main's fast's stolen..." );
            worker00.run();
        } catch (t:Throwable) {
            Runtime.println(here +"Uncaught exception in main: " + t);
            t.printStackTrace();
        }
    }
}
