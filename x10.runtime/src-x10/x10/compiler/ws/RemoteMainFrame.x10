package x10.compiler.ws;

import x10.compiler.Header;

abstract public class RemoteMainFrame extends RegularFrame {
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
        fresh = true;
    }
    
    var fresh:Boolean; //true: a remote task that never got executed; 
                       //false: redo() by worker, like a regular frame

    public def this(Int, o:RemoteMainFrame) {
        super(-1, o);
        fresh = false;
    }

    public abstract def fast(worker:Worker):void;
}
