/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.clock;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import x10.runtime.Activity;
import x10.runtime.Clock;
import x10.runtime.PoolRunner;
import x10.runtime.Report;
/**
 * Clock manager is responsible to handle clock management inside an activity.
 * It basically handle a Clock and a List of clock and switch from one strategy 
 * to another according number of clock registered with the activity.
 * 
 * @author vcave
 *
 */
public class ClockManagerImpl implements ClockManager {

	protected Activity activity;
	private Clock singleClock;
	private List clockList;

	public ClockManagerImpl(Activity activity)
	{
		this.activity = activity;
	}

	/**
	 * Construct the clock manager.
	 * @param activity The activity to associates with.
	 * @param clocks The clock list to register.
	 */
	public ClockManagerImpl(Activity activity, List clocks) {
		this(activity);
		if (Report.should_report(Report.ACTIVITY, 3)) {
			Report.report(3, PoolRunner.logString() + " adding clocks "
					+ clocks + " to " + activity);
		}
		this.clockList = clocks;
	}
	
	/**
	 * Construct the clock manager.
	 * @param activity The activity to associates with.
	 * @param clocks The clock to register.
	 */
	public ClockManagerImpl(Activity activity, Clock clock) {
		this(activity);
		if (Report.should_report(Report.ACTIVITY, 3)) {
			Report.report(3, PoolRunner.logString() + " adding clock "
					+ clock + " to " + activity);
		}
		this.singleClock = clock;
	}

	/**
	 * Switch from a single clock strategy to a clock list one.
	 *
	 */
	private void switchFromSingleToListStrategy()
	{
		this.clockList = new LinkedList();
		this.clockList.add(this.singleClock);
		this.singleClock = null;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#addClock(x10.runtime.Clock)
	 */
	public/*myThread*/void addClock(Clock c) {
		if (Report.should_report(Report.CLOCK, 3)) {
			Report.report(3, PoolRunner.logString() + " " + activity + " adds " + c
					+ ".");
		}
			if(this.singleClock != null) {
				this.switchFromSingleToListStrategy();
			}
			this.clockList.add(c);
		}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#dropClock(x10.runtime.Clock)
	 */
	public/*myThread*/void dropClock(Clock c) {
		if (Report.should_report(Report.CLOCK, 3)) {
			Report.report(3, PoolRunner.logString() + " " + activity + " drops "
					+ c + ".");
		}
		
		if (this.singleClock != null) {
				if(this.singleClock.equals(c)) {
					// Activity associated with this ClockManager has the responsability
					// to dismiss (i.e. set to null his reference) this clock manager 
					// as no more clocks are associated with.
					this.singleClock = null;
				} else {
					if (Report.should_report(Report.CLOCK, 3)) {
						Report.report(3, PoolRunner.logString() + " " + activity + " dropClock attempt failed because clock to drop is not known by the activity "
								+ c + ".");
					}
				}
		} else {
			this.clockList.remove(c);
		}
		
	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#dropAllClocks()
	 */
	public /*myThread*/void dropAllClocks() {
		
		if(this.singleClock != null) {
			this.singleClock.drop(this.activity);
			this.singleClock = null;
		}
		else {
			for (Iterator it = this.clockList.iterator(); it.hasNext();) {
				Clock c = (Clock) it.next();
				c.drop(this.activity);
			}
		}
		
		if (Report.should_report(Report.CLOCK, 3)) {
			Report.report(3, PoolRunner.logString() + " " + activity
					+ " drops all clocks.");
		}
		
		// Activity associated with this ClockManager has the responsability
		// to dismiss (i.e. set to null his reference) this clock manager 
		// as no more clocks are associated with.
	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#doNext()
	 */
	public/*myThread*/void doNext() {
		if(this.singleClock != null)
		{
			if (Report.should_report(Report.ACTIVITY, 3)) {
				Report.report(3, PoolRunner.logString() + " " + activity
						+ ".doNext() on " + this.singleClock);
			}
//			this.singleClock.resume();  // resume is called by doNext
			this.singleClock.doNext();
		} else {
			if (Report.should_report(Report.ACTIVITY, 3)) {
				Report.report(3, PoolRunner.logString() + " " + activity
						+ ".doNext() on " + this.clockList);
			}
			Iterator it = this.clockList.iterator();
			while (it.hasNext()) {
				Clock c = (Clock) it.next();
				c.resume();
			}
	
			it = this.clockList.iterator();
			while (it.hasNext()) {
				Clock c = (Clock) it.next();
				c.doNext();
			}
		}

	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#registerClocks()
	 */
	public void registerClocks() {
		if(this.singleClock != null) {
			this.singleClock.register(this.activity);
		} else {
				Iterator it = clockList.iterator();
				while (it.hasNext()) {
					Clock c = (Clock) it.next();
					c.register(this.activity);
				}
		}
	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#checkClockUse(x10.runtime.Clock)
	 */
	public Clock checkClockUse(Clock c) {
		// should never being delegated
		assert false;
		return null; 
	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#getNbRegisteredClocks
	 */
	public int getNbRegisteredClocks() {
		if (this.singleClock != null)
			return 1;
		
		if(this.clockList != null)
			return this.clockList.size();
		
		return 0;
	}
}
