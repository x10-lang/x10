package x10.runtime;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 *
 * @author Christian Grothoff
 */
class LocalPlace_c extends PooledExecutor
    implements Place {

    LocalPlace_c(final ThreadRegistry reg) {
	super(new LinkedQueue());
	this.setThreadFactory(new ThreadFactory() {
		public Thread newThread(final Runnable cmd) {
		    Thread t = new Thread(cmd);
		    reg.registerThread(t, LocalPlace_c.this);
		    return t;
		}
	    });
	this.setMinimumPoolSize(2);
	this.setKeepAliveTime(5000);	
    }

    public void runAsync(Activity a) {
	throw new Error("not implemented");
    }

    public Activity.Result runFuture(Activity.Future a) {
	throw new Error("not implemented");
    }

}