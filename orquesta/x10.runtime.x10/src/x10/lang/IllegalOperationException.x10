package x10.lang;

import java.lang.RuntimeException;

public class IllegalOperationException extends RuntimeException {

    public IllegalOperationException(String msg) {
        super(msg);
    }
}
