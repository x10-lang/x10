package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Inline;
import x10.compiler.Uninitialized;
import x10.util.IndexedMemoryChunk;
import x10.compiler.Header;

import x10.util.Stack;

//For collecting Finish frame usage

abstract public class CollectingFinish[T] extends FinishFrame {
    @Uninitialized public val reducer:Reducible[T];
    @Uninitialized public val resultRail:IndexedMemoryChunk[T];
    @Ifdef("__CPP__") @Uninitialized public var result:T; //Only for CPP

    public def this(up:Frame, rd:Reducible[T]) {
        super(up);
        this.reducer = rd;
        @Ifdef("__CPP__") {this.result = rd.zero();}
        @Ifndef("__CPP__"){ //java path 
            val size = Runtime.NTHREADS;
            this.resultRail = IndexedMemoryChunk.allocateUninitialized[T](size);
            for(var i:int = 0; i < size; i++){
                resultRail(i) = reducer.zero();
            }
        }
    }

    @Ifdef("__CPP__")
    public def this(x:Int, o:CollectingFinish[T]) {
        super(x, o);
        //need copy the value too
        this.reducer = o.reducer;
        this.result = o.result;
        val size = Runtime.NTHREADS;
        this.resultRail = IndexedMemoryChunk.allocateUninitialized[T](size);
        for(var i:int = 0; i < size; i++){
            resultRail(i) = reducer.zero();
        }
    }
    
    @Inline public final def accept(t:T, worker:Worker){
        @Ifdef("__CPP__"){
            if(redirect == null){
               result = reducer(result, t);
            }
            else {
               val id = worker.id;
               val rr = cast[FinishFrame, CollectingFinish[T]](redirect).resultRail;
               rr(id) = reducer(rr(id), t);
            }
        } 
        @Ifndef("__CPP__"){
            val id = worker.id;
            resultRail(id) = reducer(resultRail(id), t);
        }
    }
    
    //In finish frame's fast path, the result is stored here.
    @Inline public final def fastResult(worker:Worker):T {
        @Ifdef("__CPP__"){
            return result;
        }
        @Ifndef("__CPP__"){
            val result = resultRail(worker.id);
            resultRail.deallocate();
            return result;
        }
    
    }
    
    @Inline public final def result():T {
        //do merge
        @Ifdef("__CPP__"){
            val size = Runtime.NTHREADS;
            for(var i:int = 0; i < size; i++){
                result = reducer(result, resultRail(i));
            }
            resultRail.deallocate();
            return result;
        }
        @Ifndef("__CPP__"){
            var result:T = resultRail(0);
            val size = Runtime.NTHREADS;
            for(var i:int = 1; i < size; i++){
                result = reducer(result, resultRail(i));
            }
            resultRail.deallocate();
            return result;
        }
    }
}
