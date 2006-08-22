/**
 * This class implements the notion of places in X10. The maximum
 * number of places is determined by a configuration parameter
 * (MAX_PLACES). Each place is indexed by a nat, from 0 to MAX_PLACES;
 * thus there are MAX_PLACES+1 places. This ensures that there is
 * always at least 1 place, the 0'th place.
 
 * <p>We use a dependent parameter to ensure that the compiler can track
 * indices for places.
 * 
 * <p>Note that place(i), for i =< MAX_PLACES, can now be used as a non-empty type.
 
 * <p>Thus it is possible to run an async at another place, without
 * using arays --- just use async(place(i)) {...} for an appropriate
 * i.
 
 * @author vj
 */

package x10.lang;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import x10.base.TypeArgument;
import x10.cluster.ClusterConfig;
import x10.cluster.ClusterPlace;
import x10.cluster.ClusterRuntime;
import x10.cluster.HasResult;
import x10.cluster.X10Node;
import x10.cluster.comm.RPCHelper;
import x10.cluster.message.MessageType;
import x10.runtime.Activity;
import x10.runtime.LocalPlace_c;

public /*value*/ abstract class place /*(nat i : i =< MAX_PLACES)*/ extends x10.lang.Object 
implements TypeArgument, ValueType{
	private static int count_ = 0;
	public /*final*/ /*nat*/int id;
	
	/** The number of places in this run of the system. Set on
	 * initialization, through the command line/init parameters file.
	 * is initialized by a specific runtime, e.g. x10.runtime.DefaultRuntime_c
	 */
	/*config*/ public static /*final*/ /*nat*/ int MAX_PLACES = 10;
	
	
	protected place() {
		synchronized (place.class) {
			id = count_++;
		} 
	}
	
	public static abstract /*value*/ class factory implements ValueType {
		/**  Return the place numbered i, using modulo MAX_PLACES
		 *  arithmetic.
		 */
		abstract public place/*(i % MAX_PLACES)*/ place( final /*nat*/int i );
		
		/** Return the set of places from 0 to last. */
		abstract public Set/*<place>*/ places( /*nat*/int last);
		
		/** Return the place of the current activity.
		 */
		abstract public place here();
	}
	public static final factory factory = Runtime.factory.getPlaceFactory();
	
	/** The last place in this program execution.
	 */
	public static /*final*/ place/*(MAX_PLACES)*/ LAST_PLACE /*= factory.place(MAX_PLACES-1)*/;
	
	/** The first place in this program execution.
	 */
	public static /*final*/ place/*(0)*/ FIRST_PLACE /*= factory.place(0)*/;
	
	public static /*final*/ Set/*<place>*/ places /*= factory.places( MAX_PLACES-1 )*/;	
	/**
	 * Needs to be called after MAX_PLACES has its final value.
	 *
	 */
	public static void initialize() {
		assert(places == null); //should be invoked only once
		LAST_PLACE = factory.place(MAX_PLACES-1);
		FIRST_PLACE = factory.place(0);
		places = factory.places(MAX_PLACES-1);
		
		locks_ = new place[MAX_PLACES];
		for(Iterator it = places.iterator(); it.hasNext(); ) {
			place p = (place) it.next();
			locks_[p.id] = p;
		}
	}
	public static place places(int i) { return factory.place(i);}
	public boolean equals(java.lang.Object o) {
		boolean ret = false;
		if (o != null && o instanceof x10.lang.place) {
			x10.lang.place op = (x10.lang.place) o;
			ret = op.id == id;
		}
		return ret;
	}
	
	/** Returns the next place, using modular arithmetic. Thus the
	 * next place for the last place is the first place. 
	 */
	public place/*(id+1 % MAX_PLACES)*/ next()  { 
		final place result = next(1);
		// System.out.println("place:" + this + ".next()=" + result);
		return result; 
	}
	
	/** Returns the previous place, using modular arithmetic. Thus the
	 * previous place for the first place is the last place. 
	 */
	public place/*(i-1 % MAX_PLACES)*/ prev()  { 
		return next(-1); 
	}
	
	
	/** Returns the k'th next place, using modular arithmetic. k may
	 * be negative.
	 */
	public place/*(i+k % MAX_PLACES)*/ next( final int k ) {
		int index = ( id + k);
		while (index < 0) { index += MAX_PLACES; }
		final place result = factory.place( index % MAX_PLACES);
		return result; 
	}
	
	/**  Is this the first place?
	 */
	public boolean isFirst() { 
		return id==0;
	}
	
	/** Is this the last place?
	 */
	public boolean isLast() { 
		return id==MAX_PLACES -1; 
	}
	
	public String toString() {
		return "place(id=" + id +")"; 
	}
	
	/**
	 *  Return a lock to use for 'atomic' construct.  Place objects can be easily
	 *  created multiple time for a single place id.
	 *  @author xinb
	 */
	private static place[] locks_ = null;
	public Object getLock() {
		return locks_[this.id];
	}
	
	
	/////////////////////////////////////////////////////////////////////
	//Lifecycle APIs
	////////////////////////////////////////////////////////////////////
	public enum PlaceState {IDLE, BUSY, WAIT, TERMINATED}		
	
	/**
	 * Dynamically create a new place, which is not linked with the statically
	 * configured places.  By default this newly created place will run on the
	 * same VM as the calling place.
	 * 
	 * @return
	 */
	public static place newPlace() {
		place p = createPlace();
		
		//map this place correctly and inform other VMs, if necessary
		ClusterConfig.addDynamicPlace(ClusterRuntime.getNode(), p);
		
		return p;
	}
	
	private static HashMap<Integer, place> dynPlaces_ = new HashMap<Integer, place>();
	private static place createPlace() {
		place p; 
		if(ClusterConfig.multi)
			p = new ClusterPlace();
		else
			p = new LocalPlace_c();
		
		dynPlaces_.put(new Integer(p.id), p);
		
		return p;
	}
	
	/**
	 * Get the dynamically created 'place' instance by its id, if not existing,
	 * then create one.
	 * XXX move to another type, should be masked from programmer API.
	 * @param id
	 * @return
	 */
	public static place dynPlace(int id) {
		place ret = dynPlaces_.get(id);
		if(ret == null) {
			ret = createPlace();
			ret.id = id;  //make sure the id is consistent
			dynPlaces_.put(new Integer(id), ret);
		}
		return ret;
	}
	
	public static place newPlace(final X10Node nd) {
		if(ClusterRuntime.getNode().sameNode(nd)) {
			return newPlace();
		} else {
			try {
				java.lang.Integer pid = (java.lang.Integer) RPCHelper.serializeCodeW(nd, new HasResult(MessageType.MIS) {
					private place ret;
					public void run() {
						//System.out.println("place.newPlace: Creating new places ....");
						ret = newPlace(); 
						//System.out.println("place.newPlace: Creating new places .... done.");
					}
					public java.lang.Object getResult() {
						return ret.id;
					}
				});
				
				return dynPlace(pid.intValue());
			}catch(java.lang.Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * The current state of this place: IDLE, BUSY, WAIT, TERMINATED.
	 * @return
	 */
	public abstract /*synchronized*/ PlaceState getState();
	
	/**
	 * Terminate this place and release all the associated resources at
	 * the earliest moment.  If there is task running, wait for it to
	 * finish, but receive no more new tasks.
	 * 
	 */
	public abstract /*synchronized*/ void shutdown();
}
