package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Inline;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;
import x10.compiler.Uninitialized;

import x10.util.GrowableRail;

abstract public class FinishFrame extends Frame {
    @Uninitialized public var asyncs:Int;
    @Uninitialized transient protected var exceptions:GrowableRail[Exception];

    @Ifdef("__CPP__")
    @Uninitialized public var redirect:FinishFrame;

    @Header public def this(up:Frame) {
        super(up);
        this.exceptions = null;
        @Ifdef("__CPP__") {
            this.redirect = null;
        }
        @Ifndef("__CPP__") {
            this.asyncs = 1;
        }
    }

    @Ifdef("__CPP__")
    public def this(Int, o:FinishFrame) {
        super(o.up.realloc());
        this.asyncs = 1;
        this.exceptions = null;
    }

    @Ifdef("__CPP__")
    public abstract def remap():FinishFrame;

    @Ifdef("__CPP__")
    public def realloc() {
        if (null != redirect) return redirect;
        val tmp = remap();
        tmp.redirect = tmp;
        if (null != exceptions) {
            tmp.exceptions = new GrowableRail[Exception](exceptions.size());
            Runtime.atomicMonitor.lock();
	    while (!exceptions.isEmpty()) tmp.exceptions.add(exceptions.removeLast());
            exceptions = null;
            Runtime.atomicMonitor.unlock();
        }
        redirect = tmp;
        return tmp;
    }

    public def wrapBack(worker:Worker, frame:Frame) {
        if (null != worker.throwable) {
            //Runtime.atomicMonitor.lock();
            caught(worker.throwable);
            worker.throwable = null;
            //Runtime.atomicMonitor.unlock();
        }
    }

    public def wrapResume(worker:Worker) {
        var n:Int;
        Runtime.atomicMonitor.lock(); n = --asyncs; Runtime.atomicMonitor.unlock();
        if (0 != n) throw Abort.ABORT;
        worker.throwable = MultipleExceptions.make(exceptions);
    }

    @Inline public final def append(s:GrowableRail[Exception]) {
        if (null != s) { //nobody will change s again. No need lock to protect s.
            Runtime.atomicMonitor.lock();
            if (null == exceptions) exceptions = new GrowableRail[Exception]();
	    while (!s.isEmpty()) exceptions.add(s.removeLast());
            Runtime.atomicMonitor.unlock();
        }
    }

    @Inline public final def append(ff:FinishFrame) {
        append(ff.exceptions);
    }

    @NoInline public final def caught(t:Exception) {
//        Runtime.println("CAUGHT: " + t);
        if (t == Abort.ABORT) throw Abort.ABORT;
        Runtime.atomicMonitor.lock();
        if (null == exceptions) exceptions = new GrowableRail[Exception]();
        exceptions.add(t);
        Runtime.atomicMonitor.unlock();
    }

    @Inline public final def rethrow() {
        if (null != exceptions) rethrowSlow();
    }

    @NoInline @NoReturn public final def rethrowSlow() {
        throw new MultipleExceptions(exceptions);
    }

    @Inline public final def check() {
        if (null != exceptions) {
            while (!exceptions.isEmpty()) {
                Runtime.pushException(exceptions.removeLast());
            }
        }
    }
}
