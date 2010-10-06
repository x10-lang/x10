package x10.compiler.ws;

import x10.compiler.Header;
import x10.compiler.Uninitialized;

abstract public class FinishFrame extends Frame {
    @Uninitialized public var asyncs:Int;
    @Uninitialized public var redirect:FinishFrame;

    // constructor
    @Header public def this(up:Frame) {
        super(up);
//        this.asyncs = 1;
        this.redirect = NULL[FinishFrame]();
    }
    
    // copy constructor
    public def this(Int, o:FinishFrame) {
        super(o.up.realloc());
        this.asyncs = 1;
        //this.redirect = this; //moved to realloc because of V2.1
        this.redirect = NULL[FinishFrame]();
    }

    // copy methods
    public abstract def remap():FinishFrame;

    public def realloc() {
        if (null != redirect) return redirect;
        val tmp = remap();
        //assign moved to here
        tmp.redirect = tmp;
        redirect = tmp;
        return tmp;
    }
}