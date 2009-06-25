//OPTIONS: -PLUGINS=x10.klock.plugin.KlockPlugin
//CLASSPATH: ../classes/klock.jar
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */


/**
 * Check if illegal uses of clock are raising an exception.
 * @author kemal 4/2005
 */
public class ClockTest14 {

	public boolean run() {
		final clock c = clock.factory.clock();
		boolean gotException;
		next;
		c.resume();
		c.drop();
		//chk(c.dropped());
		next; // empty clock set is acceptable, next is no-op
		gotException = false;
		try {
			c.resume();
		} catch (ClockUseException e) {
			gotException = true;
		}
		//chk(gotException);
		gotException = false;
		try {
			async clocked(c) { }
		} catch (ClockUseException e) {
			gotException = true;
		}
		//chk(gotException);
		return true;
	}

	public static void main(String[] args) {
		new ClockTest14().run();
	}
}

