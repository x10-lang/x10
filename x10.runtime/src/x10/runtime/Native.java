package x10.runtime;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * "Native" functions mapped through to Java.  In the long run the
 * idea is that this becomes the "X10 native interface" (akin to JNI,
 * but of course better).  For the prototype, this interface contains
 * the few native functions that we just feel like we have to provide.
 *
 * In principle, this class can be subclassed, but since it is
 * unlikely that we will have multiple implementations of any of these
 * functions other than the default, we currently do without.
 *
 * @author Christian Grothoff
 */
public class Native {

    private final Logger logger_ 
	= Logger.getAnonymousLogger();

    public long currentTimeMillis() {
	return System.currentTimeMillis();
    }

    public void log(Level l,
		    String message,
		    Object param1) {
	logger_.log(l, message, param1);
    }

    public void log(Level l,
		    String message,
		    Object[] params) {
	logger_.log(l, message, params);
    }

    public void log(Level l,
		    String message,
		    Throwable thrown) {
	logger_.log(l, message, thrown);
    }

} // end of Native