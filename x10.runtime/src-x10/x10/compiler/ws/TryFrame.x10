package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Inline;

public abstract class TryFrame extends RegularFrame {
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
    }

    public def this(Int, o:TryFrame) {
        super(-1, o);
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

    @Inline public final def rethrow() {
        if (null != throwable) {
            val t = throwable;
            throwable = null;
            throw t;
        }
    }
}
