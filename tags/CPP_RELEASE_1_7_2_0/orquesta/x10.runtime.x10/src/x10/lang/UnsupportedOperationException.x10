package x10.lang;

import java.lang.RuntimeException;

class UnsupportedOperationException extends RuntimeException {

    UnsupportedOperationException() {
        super("unsupported operation");
    }

    UnsupportedOperationException(String msg) {
        super(msg);
    }
}
