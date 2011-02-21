package x10.compiler.ws;

import x10.util.Random;
import x10.lang.Lock;
import x10.compiler.SuppressTransientError;
import x10.compiler.RemoteInvocation;
import x10.compiler.InlineOnly;
public final class Worker {
    public val workers:Rail[Worker];
    private val id:Int;
    private val random:Random;

    public val deque = new Deque();
    public val fifo = new Deque();
    public val lock = new Lock();
    
    //static place local handle
    //or these are shared part among all workers in one place
    public val finished:BoxedBoolean;
    public val intaskque:Deque; //store remoteMainFrame, BoxedBoolean, and finishframe
                                //will be merged into fifo soon
    //and the primary worker for other places
    public var pWorkerRefs:Rail[GlobalRef[Worker]];

    public def this(i:Int, workers:Rail[Worker], finished:BoxedBoolean, intaskque:Deque) {
        random = new Random(i + (i << 8) + (i << 16) + (i << 24));
        this.id = i;
        this.workers = workers;
        this.finished = finished;
        this.intaskque = intaskque;
    }

    public def pushRemoteFrame(frame:RemoteMainFrame){
        intaskque.push(Frame.upcast[RemoteMainFrame, Object](frame));
    }

    public def pushRemoteFF(ff:FinishFrame){
        intaskque.push(Frame.upcast[FinishFrame, Object](ff));
    }

    public def notifyStop(){
        finished.value = true;
        //Runtime.println(here +" was notified to stop()");
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
                if (null == k) {
                    //Runtime.println(here + ":Worker(" + id + ") terminated");
                    return;
                }
                try {
                    k.resume(this);
                    unstack(Frame.upcast[RegularFrame,Frame](k)); // top frames are meant to be on the stack
                } catch (Stolen) {}
                purge(Frame.upcast[RegularFrame,Frame](k), k.ff); // needed because we did not stack allocate those frames
            }
        } catch (t:Throwable) {
            Runtime.println(here + "Uncaught exception in worker: " + t);
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
            if( null != k) break;
            //now process native event in case nothing to do
            Runtime.wsProcessEvents();
            //now regular frame or finish frame may in inque
            var remoteK:Object = intaskque.steal();
            if(null != remoteK){
                if(remoteK instanceof RemoteMainFrame){
                    //regular frame, should run as normal, not process stolen
                    val p = Frame.cast[Object, RemoteMainFrame](remoteK);
                    try{
                        //Runtime.println(here+":Got one remote task");
                        p.fast(this);
                        //if the app goes here, it means the app is always in fast.
                        //need check whether the current ff.asyncs is only 1
                        var asyncs:Int;
                        atomic asyncs = --p.ff.asyncs;
                        if(asyncs == 0){
                            val root:RemoteRootFrame = Frame.cast[Frame, RemoteRootFrame](Frame.cast[Frame, RemoteRootFinish](p.up).up);
                            val ffRef = root.ffRef; //a global ref
                            remoteFinishJoin(ffRef); //invoke the remote one   
                        }
                        //else still run the app
                    }catch (Stolen) {
                        //not sure whether need handle
                    }
                }
                else if(remoteK instanceof FinishFrame){
                    //finish frame, need run the finish frame's unroll
                    val p = Frame.cast[Object, FinishFrame](remoteK);
                    try{
                        //Runtime.println(here+":Got remote finish join");
                        unroll(p);
                    } catch (Stolen){
                        //not sure what to do
                    }
                }
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

    static def deref[T](root:GlobalRef[Worker]) = (root as GlobalRef[Worker]{home==here})() as T;
    static def derefFrame[T](ffRef:GlobalRef[FinishFrame]) = (ffRef as GlobalRef[FinishFrame]{home==here})() as T;
    static def derefBB[T](root:GlobalRef[BoxedBoolean]) = (root as GlobalRef[BoxedBoolean]{home==here})() as T;
    
    //the frame should be in heap, and could be copied deeply
    public def remoteRunFrame(place:Place, frame:RemoteMainFrame, ff:FinishFrame){
        atomic ff.asyncs++; //need add the frame's structure
        val id:Int = place.id;
        //need get the right worker
        val remoteWorkerRef = pWorkerRefs(id);
        //right now, the frame's up should be RemoteRootFinish, and upper RemoteRootFrame.
        //And the RemoteRootFrame contains the global ref to ff
        val body:()=>void = ()=> {
            //Worker.pushRemoteFrame(frame); //place local handle version
            val obj:Object = Frame.upcast[RemoteMainFrame, Object](frame);
            deref[Worker](remoteWorkerRef).pushRemoteFrame(frame);
        };
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
        //need push the frame back to its inque
        //locate the remote worker
        val remoteWorkerRef = pWorkerRefs(id);
        val body:()=>void = ()=>{            
            deref[Worker](remoteWorkerRef).pushRemoteFF(derefFrame[FinishFrame](ffRef));
        };
        //Runtime.println(here + ":Run Finish Join back to place:" + id);
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
        };
        //Runtime.println(here + ":Run At Notify back to place:" + id);
        Runtime.wsRunCommand(id, body);
        Runtime.dealloc(body);
    }
    
    public static def allStop(worker:Worker){
        for( var id:Int = 0; id < Place.MAX_PLACES; id++ ) {
            val remoteWorkerRef = worker.pWorkerRefs(id);
            val idd:Int = id;
            val body:()=>void = ()=>{
                deref[Worker](remoteWorkerRef).notifyStop();
                //Worker.notifyStop(); //place local handle version
            };
            Runtime.wsRunCommand(idd, body);
            Runtime.dealloc(body);
        }
    }

    //Primary workers
    //public static val pWorkers:Rail[GlobalRef[Worker]] = Rail.make[GlobalRef[Worker]](Place.MAX_PLACES);
    //public static val pWorkers = PlaceLocalHandle.make[Rail[GlobalRef[Worker]]](Dist.makeUnique(), () => Rail.make[GlobalRef[Worker]](Place.MAX_PLACES));

    public static def main(frame:MainFrame) {
        //create all workers in all places
        val worker0s = Rail.make[GlobalRef[Worker]](Place.MAX_PLACES);
        val worker0sRef = GlobalRef[Rail[GlobalRef[Worker]]](worker0s);

        //First iteration, create all workers, and get the globalRef array
        finish for (p in Place.places()) {
            async at(p) {
                val workers = Rail.make[Worker](Runtime.INIT_THREADS);
                val finished = new BoxedBoolean();
                val intaskque = new Deque();
                for (var i:Int = 0; i<Runtime.INIT_THREADS; i++) {
                    workers(i) = new Worker(i, workers, finished, intaskque);
                }
                //Need store the first worker back
                val worker0Ref = GlobalRef[Worker](workers(0));
                val placeId = here.id;
                at(worker0sRef) {
                    //store the global ref to place 0;
                    worker0sRef()(placeId) = worker0Ref;
                }
            }
        }
        //Second iteration, assign(copy) the globalRef array back to each place
        finish for (var id:Int = 0; id < Place.MAX_PLACES; id++) {
            val idd:Int = id;
            val worker0Ref = worker0s(idd);
            async at(worker0Ref) {
                //store the worker0s to local
                val workers = worker0Ref().workers;
                for(var i:Int = 0; i < Runtime.INIT_THREADS; i++){
                    val ii:Int = i;
                    workers(ii).pWorkerRefs = worker0s;
                }
            }
        }
        //now the worker0s should contains all refs
        //3rd iteration, aysnc start all workers
        for (var id:Int = 0; id < Place.MAX_PLACES; id++) {
            val idd:Int = id;
            val worker0Ref = worker0s(idd);
            async at(worker0Ref) {
                val workers = worker0Ref().workers;
                for (var i:Int = 0; i<Runtime.INIT_THREADS; i++) {
                    val ii = i;
                    if(ii == 0 && here.id == 0){
                        continue; //not start the 1st worker;
                    }
                    //Runtime.println(here + ":Worker(" + i + ") started..." );
                    async workers(ii).run();
                }
            }
        }
        //start first worker at place0
        val worker00 = deref[Worker](worker0s(0));
        try {
            frame.fast(worker00);
            //If the app goes here, it means it always in fast path. 
            //We need process the ff.asyncs to make sure it can quit correctly
            var asyncs:Int;
            atomic asyncs = --frame.ff.asyncs;
            if(asyncs > 0) {
                //Runtime.println(here + ":Worker(0) started after main's fast..." );
                worker00.run();
            }
            else {
                //global stop
                //Runtime.println(here+":Fire all stop msg from fast" );
                Worker.allStop(worker00);
                //Runtime.println(here + ":Worker(0) terminated in fast");    
            }
        } catch (Stolen) {
            //Runtime.println(here + ":Worker(0) started after main's fast's stolen..." );
            worker00.run();
        } catch (t:Throwable) {
            Runtime.println(here +"Uncaught exception in main: " + t);
            t.printStackTrace();
        }
    }
}
