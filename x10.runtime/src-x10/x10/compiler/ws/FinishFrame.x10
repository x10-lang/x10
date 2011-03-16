package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Uninitialized;
import x10.util.Stack;

abstract public class FinishFrame extends Frame {
    @Uninitialized public var asyncs:Int;
    @Uninitialized public var redirect:FinishFrame;
    @Uninitialized public val stack:Stack[Throwable];

    // constructor
    @Header public def this(up:Frame) {
        super(up);
//        this.asyncs = 1;
        this.redirect = NULL[FinishFrame]();
    }
    
    // copy constructor
    public def this(Int, o:FinishFrame) {
        super(o.up.realloc());
        stack = new Stack[Throwable]();
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

    public def wrapBack(worker:Worker, frame:Frame) {
        if (null != frame.throwable) {
            stack.push(frame.throwable);
        } else {
            back(worker, frame);
        }
    }

    public def wrapResume(worker:Worker) {
        throwable = MultipleExceptions.make(stack);
        if (null != throwable) return;
        try {
            resume(worker);
        } catch (t:Abort) {
            throw t;
        } catch (t:Throwable) {
            throwable = t;
        }
    }
}
