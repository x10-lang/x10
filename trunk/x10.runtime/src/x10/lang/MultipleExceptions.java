package x10.lang;

import java.util.Stack;

/**
 * @author Christian Grothoff
 */
public class MultipleExceptions extends x10.lang.Exception {
    public final Stack exceptions; // <Throwable>
    public MultipleExceptions(Stack s) {
        this.exceptions = s;
    }
}
