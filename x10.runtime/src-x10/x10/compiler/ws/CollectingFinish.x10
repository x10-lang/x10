package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Ifdef;
import x10.compiler.Inline;
import x10.compiler.Uninitialized;
import x10.util.IndexedMemoryChunk;
import x10.compiler.Header;

import x10.util.Stack;

abstract public class CollectingFinish[T] extends FinishFrame {
    @Uninitialized public val reducer:Reducible[T];
    @Uninitialized public val resultRail:IndexedMemoryChunk[T];

    public def this(up:Frame, rd:Reducible[T]) {
        super(up);
        this.reducer = rd;
        val size = Runtime.NTHREADS;
        val zero = rd.zero();
        resultRail = IndexedMemoryChunk.allocateUninitialized[T](size);
        for(var i:int = 0; i < size; i++){
            resultRail(i) = zero;
        }
    }

    @Ifdef("__CPP__")
    public def this(x:Int, o:CollectingFinish[T]) {
        super(x, o);
        //need copy the value too
        this.reducer = o.reducer;
        this.resultRail = o.resultRail;
    }
    
    @Inline public final def accept(t:T, worker:Worker){
        val id = worker.id;
        resultRail(id) = reducer(resultRail(id), t);
    }
    
    //In finish frame's fast path, the result is stored here.
    @Inline public final def fastResult(worker:Worker):T {
        val result = resultRail(worker.id);
        resultRail.deallocate();
        return result;
    }
    
    @Inline public final def result():T {
        //do merge
        var result:T = resultRail(0);
        val size = Runtime.NTHREADS;
        for(var i:int = 1; i < size; i++){
            result = reducer(result, resultRail(i));
        }
        resultRail.deallocate();
        return result;
    }
}
