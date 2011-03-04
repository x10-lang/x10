/**
	 * This is an atomic implementation of the LockedDouble.
	 * (See the next type below)
	 */
import x10.util.concurrent.AtomicLong;
public final class AtomicDouble {
	private var value:AtomicLong;
	
	// Construct the value with the requested initial.
	public def this (init:Double) { 
		value = new AtomicLong(init.toRawLongBits()); 
	}
	
	// Adjust the value by delta while holding the lock.
	public def adjust (delta:Double) { 
		var oldValue:Double = Double.fromLongBits(value.get());
		var newValue:Double = oldValue + delta;
		while (!value.compareAndSet (oldValue.toRawLongBits(), 
				newValue.toRawLongBits())) {
			oldValue = Double.fromLongBits(value.get());
			newValue = oldValue + delta;
		}
	}
	
  // Get the value
  public def get ():Double = Double.fromLongBits(value.get());

  // Set the value
  public def set (newValue:Double) { this.value = new AtomicLong(newValue.toRawLongBits()); }

	// Define a toString to print out stuff
	public def toString () = "" + Double.fromLongBits(value.get());
}
