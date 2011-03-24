package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Inline;

public abstract class TryFrame extends RegularFrame {
    // constructor
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
    }

    public def this(Int, o:TryFrame) {
        super(-1, o);
    }

    @Inline public final def rethrow() {
        if (!isNULL(throwable)) {
            val t = throwable;
            throwable = null;
            throw t;
        }
        return;
    }

    public def wrapResume(worker:Worker) {
        try {
            resume(worker);
        } catch (t:Abort) {
            throw t;
        } catch (t:Throwable) {
            throwable = t;
        }
    }
}
